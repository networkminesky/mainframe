package net.minesky.core.messaging;

import com.mongodb.client.MongoCollection;
import net.minesky.core.databridge.MineSkyDB;
import org.bson.Document;

public class MessagingSender {

    public static void sendMessage(String channel, String subchannel, String value) {
        Document document = new Document()
                .append("channel", channel)
                .append("subchannel", subchannel)
                .append("value", value);

        MongoCollection<Document> collection = MineSkyDB.getMongoClient().getDatabase("minesky").getCollection("messaging");

        collection.insertOne(document);

        collection.deleteOne(document);
    }

    public static void sendMessage(String subchannel, String value) {
        sendMessage("mainframe:default", subchannel, value);
    }

}
