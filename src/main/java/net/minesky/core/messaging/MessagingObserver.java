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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessagingObserver {

    private List<MessagingListener> listeners;
    private final MongoCollection collection;
    private final ExecutorService executorService;

    public MessagingObserver() {
        this.collection = MineSkyDB.getMongoClient().getDatabase("minesky").getCollection("messaging");
        this.listeners = new ArrayList<>();
        this.executorService = Executors.newSingleThreadExecutor();

        observeChanges();
    }

    public void disconnect() {
        executorService.shutdownNow();
    }

    private void observeChanges() {
        executorService.submit(() -> {
            List<Bson> pipeline = Collections.singletonList(
                    Aggregates.match(Filters.in("operationType", "insert"))
            );

            while (!executorService.isShutdown()) {
                try {
                    try (MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch(pipeline).iterator()) {
                        while (cursor.hasNext()) {
                            ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
                            Document updatedDocument = changeStreamDocument.getFullDocument();

                            if (updatedDocument == null)
                                continue;

                            if (updatedDocument.containsKey("channel")) {
                                String key = updatedDocument.getString("channel");
                                String value = updatedDocument.getString("value");

                                notifyListeners(key, value);

                                collection.deleteOne(new Document("_id", updatedDocument.getObjectId("_id")));
                            }
                        }
                    }
                } catch (Exception e) {
                    CoreMain.logger.severe("[Messaging] An error occurred when trying to parse a received message. Exception: " + e.getMessage());
                    e.printStackTrace();

                    // Pausa breve antes de tentar novamente
                    try {
                        Thread.sleep(500); // 0.5s de pausa
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
    }

    private void notifyListeners(String channel, String message) {
        for (MessagingListener listener : listeners) {
            listener.onDocumentUpdated(channel, message);
        }
    }

    public void addListener(String pluginId, MessagingListener listener) {
        listeners.add(listener);
        CoreMain.logger.info("[Listeners] A new "+CoreMain.currentPlatform+" plugin: "+pluginId+" added a new Listener: "+listener.toString());
    }

    public void removeChangeListener(MessagingListener listener) {
        listeners.remove(listener);
    }

}
