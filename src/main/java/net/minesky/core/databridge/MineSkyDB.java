package net.minesky.core.databridge;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.minesky.core.CoreMain;
import net.minesky.core.TXTReader;

import org.bson.Document;

public class MineSkyDB {

    public static boolean isConnected = false;
    public static boolean debuggingDatabase = false;

    private static MongoCollection<Document> playerCollection;
    public static MongoClient mongoClient;

    // returns specific collection "players"
    public static MongoCollection<Document> getPlayersCollection() {
        if(playerCollection == null) {
            playerCollection = getMongoClient().getDatabase("minesky").getCollection("players");
            return playerCollection;
        } else {
            return playerCollection;
        }
    }

    // returns the specified collection
    public static MongoCollection<Document> getCollection(String collection) {
        return getMongoClient().getDatabase("minesky").getCollection(collection);
    }

    public static MongoCollection<Document> getGuildsCollection() {
        if(playerCollection == null) {
            playerCollection = getMongoClient().getDatabase("minesky").getCollection("guilds");
            return playerCollection;
        } else {
            return playerCollection;
        }
    }

    public static MongoClient getMongoClient() {
        if(mongoClient == null) {
            mongoClient = MongoClients.create(getDefaultMongoClientSettings());
            return mongoClient;
        } else return mongoClient;
        //return MongoClients.create(MineSkyDB.getDefaultMongoClientSettings());
    }


    public static String getURI() {
        return TXTReader.getMongoDbURI();
    }

    public static MongoClientSettings getDefaultMongoClientSettings() {
        MongoClientSettings settings = MongoClientSettings.builder()
                //.readConcern(ReadConcern.MAJORITY)
                //.writeConcern(WriteConcern.MAJORITY)
                .applyConnectionString(new ConnectionString(getURI()))
                .build();

        return settings;
    }

    public static void firstConnect() {
        try (MongoClient mongoClient = MongoClients.create(getDefaultMongoClientSettings())) {
            try {

                MongoDatabase database = mongoClient.getDatabase("minesky");
                database.runCommand(new Document("ping", 1));
                CoreMain.logger.info("[MongoDB] Conectado com sucesso a Database!");
                isConnected = true;

            } catch (MongoException e) {
                e.printStackTrace();
                for(int i = 0; i < 10; i++) {
                    CoreMain.logger.warning("[MongoDB] An error occurred while trying to connect to the MongoDB Database. Probably your URI string is invalid or this IP is not allowed in Network Access.");
                }
            }
        }
    }
}
