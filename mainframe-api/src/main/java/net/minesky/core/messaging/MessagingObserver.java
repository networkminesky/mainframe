package net.minesky.core.messaging;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.MineSkyDB;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MessagingObserver {

    private final CopyOnWriteArrayList<MessagingListener> listeners = new CopyOnWriteArrayList<>();
    private final MongoCollection<Document> collection;
    private final ExecutorService executorService;

    public MessagingObserver() {
        this.collection = MessagingSender.collection();
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "mainframe-messaging");
            t.setDaemon(true);
            return t;
        });
        observeChanges();
    }

    public void disconnect() {
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private void observeChanges() {
        executorService.submit(() -> {
            List<Bson> pipeline = Collections.singletonList(
                    Aggregates.match(Filters.in("operationType", "insert"))
            );

            while (!executorService.isShutdown()) {
                try {
                    try (MongoCursor<ChangeStreamDocument<Document>> cursor =
                                 collection.watch(pipeline).iterator()) {
                        while (cursor.hasNext() && !executorService.isShutdown()) {
                            ChangeStreamDocument<Document> change = cursor.next();
                            Document full = change.getFullDocument();
                            if (full == null || !full.containsKey("channel")) {
                                continue;
                            }

                            NetworkMessage message = NetworkMessage.fromMongoDocument(full);
                            notifyListeners(message);

                            Object id = full.get("_id");
                            if (id != null) {
                                collection.deleteOne(new Document("_id", id));
                            }
                        }
                    }
                } catch (Exception e) {
                    CoreMain.logger.severe("[Messaging] Change stream error: " + e.getMessage());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
    }

    private void notifyListeners(NetworkMessage message) {
        for (MessagingListener listener : listeners) {
            if (listener.canListenToOtherChannels()
                    || message.channel().equals(listener.getChannel())) {
                try {
                    listener.onMessage(message);
                } catch (Exception ex) {
                    CoreMain.logger.severe("[Messaging] Listener " + listener + " failed: " + ex.getMessage());
                }
            }
        }
    }

    public void addListener(String pluginId, MessagingListener listener) {
        listeners.add(listener);
        CoreMain.logger.info("[Listeners] " + pluginId + " registered on " + CoreMain.currentPlatform + ": " + listener);
    }

    public void removeListener(MessagingListener listener) {
        listeners.remove(listener);
    }

    public List<MessagingListener> getListeners() {
        return new ArrayList<>(listeners);
    }
}
