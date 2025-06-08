package net.minesky.api.database;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import net.minesky.core.databridge.MineSkyDB;
import net.minesky.core.databridge.callbacks.ErrorType;
import net.minesky.core.databridge.callbacks.FindOneCallback;
import net.minesky.core.databridge.callbacks.FindValueCallback;
import net.minesky.core.databridge.callbacks.SetOneCallback;
import org.bson.Document;

import java.util.Collections;

public class PlayerDatabase {

    public static MongoCollection<Document> playersCollection() {
        return MineSkyDB.getPlayersCollection();
    }

    public static void setPlayerData(String nameOrUUID, UpdatedData newData, final SetOneCallback callback) {
        Document d = new Document();
        for (String key : newData.getList().keySet()) {
            Object value = newData.getList().get(key);
            d.put(key, value);
        }

        Document update = new Document("$set", d);

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

        //MongoClient mongoClient = MineSkyDB.getMongoClient();

        try {
            String param = nameOrUUID;
            String key;
            if (param.length() <= 16 && param.length() >= 3) {
                key = "latest-nickname";
            } else {
                if (param.length() != 36) {
                    callback.onSetError(ErrorType.NOT_FORMATED_NAME);
                    return;
                }
                key = "uuid";
            }
            param = param.toLowerCase();

            Document query = new Document(key, param);

            Document updatedDocument = playersCollection().findOneAndUpdate(query, update, options);

            if (updatedDocument != null) {
                callback.onSetDone();
            } else {
                callback.onSetError(ErrorType.UNKNOWN_PLAYER);
            }
        } catch (MongoException ex) {
            ex.printStackTrace();
            callback.onSetError(ErrorType.DATABASE_ERROR);
        }
    }

    public static void getPlayerSpecificDataAsync(String nameOrUUID, ValueType valueType, String key, final FindValueCallback callback) {
        getPlayerDataAsync(nameOrUUID, new FindOneCallback() {
            @Override
            public void onQueryError(ErrorType type) {
                callback.onQueryError(type);
            }

            @Override
            public void onQueryDone(Document document) {
                if(document == null) {
                    callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
                    return;
                }

                String[] keyParts = key.split("\\.");
                Object value = document;

                for (String part : keyParts) {
                    if (value instanceof Document) {
                        value = ((Document) value).get(part);
                    } else {
                        value = null;
                        break;
                    }
                }

                if (value == null) {

                    if(valueType == ValueType.INTEGER)
                        callback.onQueryDone(document, (int) 0, false);
                    if(valueType == ValueType.DOUBLE)
                        callback.onQueryDone(document, 0.0, false);
                    if(valueType == ValueType.LONG)
                        callback.onQueryDone(document, 0L, false);
                    if(valueType == ValueType.OBJECT || valueType == ValueType.STRING)
                        callback.onQueryDone(document, null, false);
                    if(valueType == ValueType.BOOLEAN)
                        callback.onQueryDone(document, false, false);
                    if(valueType == ValueType.LIST)
                        callback.onQueryDone(document, Collections.emptyList(), false);

                } else {
                    callback.onQueryDone(document, value, true);
                }
            }
        });
    }

    public static void getPlayerDataByNameAsync(String nickName, final FindOneCallback callback) {
        try {
            Document query = new Document("latest-nickname", nickName.toLowerCase());

            final Document result = playersCollection().find(query).first();

            if(result == null)
                callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
            else
                callback.onQueryDone(result);

        } catch (MongoException ex) {
            callback.onQueryError(ErrorType.DATABASE_ERROR);
        }
    }

    public static void getPlayerDataAsync(String nameOrUUID, final FindOneCallback callback) {

        String param = nameOrUUID;
        String key = null;
        if (param.length() <= 16 && param.length() >= 3) {
            key = "latest-nickname";
        } else {
            //param = param.replaceAll("-", "");
            if (param.length() != 36) {
                return;
            }
            key = "uuid";
        }
        param = param.toLowerCase();

        Document query = new Document(key, param);

        try {
            final Document result = playersCollection().find(query).first();

            if(result == null)
                callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
            else
                callback.onQueryDone(result);

        } catch (MongoException ex) {
            callback.onQueryDone(null);
        }

    }

}
