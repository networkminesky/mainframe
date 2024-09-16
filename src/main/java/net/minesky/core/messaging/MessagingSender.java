package net.minesky.core.messaging;

import com.mongodb.client.MongoCollection;
import net.minesky.core.databridge.MineSkyDB;
import org.bson.Document;

public class MessagingSender {

    public static void sendMessage(String channel, String value) {
        Document document = new Document()
                .append("channel", channel)
                .append("value", value);

        // Insere o documento no MongoDB
        MongoCollection<Document> collection = MineSkyDB.getMongoClient().getDatabase("minesky").getCollection("messaging");

        collection.insertOne(document);

        collection.deleteOne(document);

    }

}
