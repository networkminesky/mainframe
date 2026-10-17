package net.minesky.core.messaging;

import com.mongodb.client.MongoCollection;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.MineSkyDB;
import org.bson.Document;

import java.util.Map;

public final class MessagingSender {

    private static final String DEFAULT_CHANNEL = "mainframe:default";
    private static final String COLLECTION = "messaging";

    private MessagingSender() {
    }

    public static void publish(NetworkMessage message) {
        MongoCollection<Document> collection = collection();
        Document doc = message.toMongoDocument(CoreMain.getCurrentServer().getServerData().uuid().toString());
        collection.insertOne(doc);
        collection.deleteOne(doc);
    }

    public static void publish(String channel, String subchannel, MessageType type, Object payload) {
        publish(new NetworkMessage(
                channel,
                subchannel,
                type,
                payload,
                CoreMain.getCurrentServer().getServerData().uuid().toString(),
                System.currentTimeMillis(),
                Map.of()
        ));
    }

    public static void publish(String channel, String subchannel, String value) {
        publish(channel, subchannel, MessageType.STRING, value);
    }

    public static void publish(String subchannel, String value) {
        publish(DEFAULT_CHANNEL, subchannel, value);
    }

    public static void publish(String subchannel, MessageType type, Object payload) {
        publish(DEFAULT_CHANNEL, subchannel, type, payload);
    }

    /** @deprecated use {@link #publish(String, String)} */
    @Deprecated
    public static void sendMessage(String subchannel, String value) {
        publish(subchannel, value);
    }

    /** @deprecated use {@link #publish(String, String, String)} */
    @Deprecated
    public static void sendMessage(String channel, String subchannel, String value) {
        publish(channel, subchannel, value);
    }

    static MongoCollection<Document> collection() {
        return MineSkyDB.getMongoClient().getDatabase("minesky").getCollection(COLLECTION);
    }
}
