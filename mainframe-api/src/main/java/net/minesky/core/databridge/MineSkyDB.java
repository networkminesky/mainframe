package net.minesky.core.databridge;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minesky.core.CoreMain;
import net.minesky.core.TXTReader;
import org.bson.Document;

public final class MineSkyDB {

    public static volatile boolean isConnected = false;
    public static volatile boolean debuggingDatabase = false;

    private static MongoCollection<Document> playersCollection;
    private static MongoCollection<Document> guildsCollection;
    public static MongoClient mongoClient;

    private MineSkyDB() {
    }

    public static MongoCollection<Document> getPlayersCollection() {
        if (playersCollection == null) {
            playersCollection = database().getCollection("players");
        }
        return playersCollection;
    }

    public static MongoCollection<Document> getGuildsCollection() {
        if (guildsCollection == null) {
            guildsCollection = database().getCollection("guilds");
        }
        return guildsCollection;
    }

    public static MongoCollection<Document> getCollection(String collection) {
        return database().getCollection(collection);
    }

    private static MongoDatabase database() {
        return getMongoClient().getDatabase("minesky");
    }

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(getDefaultMongoClientSettings());
        }
        return mongoClient;
    }

    public static String getURI() {
        return TXTReader.getMongoDbURI();
    }

    public static MongoClientSettings getDefaultMongoClientSettings() {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(getURI()))
                .build();
    }

    public static void firstConnect() {
        try (MongoClient probe = MongoClients.create(getDefaultMongoClientSettings())) {
            probe.getDatabase("minesky").runCommand(new Document("ping", 1));
            CoreMain.logger.info("[MongoDB] Connected successfully.");
            isConnected = true;
        } catch (MongoException e) {
            isConnected = false;
            CoreMain.logger.warning("[MongoDB] Connection failed — check credentials.txt and network access.");
        }
    }
}
