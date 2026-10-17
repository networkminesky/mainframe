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
import net.minesky.core.runtime.MainframeRuntime;
import org.bson.Document;

import java.util.Collections;

public final class PlayerDatabase {

    private PlayerDatabase() {
    }

    public static MongoCollection<Document> playersCollection() {
        return MineSkyDB.getPlayersCollection();
    }

    public static void setPlayerData(String nameOrUUID, UpdatedData newData, SetOneCallback callback) {
        MainframeRuntime.async().execute(() -> {
            Document d = new Document();
            for (String key : newData.getList().keySet()) {
                d.put(key, newData.getList().get(key));
            }

            Document update = new Document("$set", d);
            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);

            try {
                PlayerLookup lookup = resolveLookup(nameOrUUID);
                if (lookup.error != null) {
                    callback.onSetError(lookup.error);
                    return;
                }

                Document updatedDocument = playersCollection().findOneAndUpdate(lookup.query, update, options);
                if (updatedDocument != null) {
                    callback.onSetDone();
                } else {
                    callback.onSetError(ErrorType.UNKNOWN_PLAYER);
                }
            } catch (MongoException ex) {
                CoreMainSafeLog.log(ex);
                callback.onSetError(ErrorType.DATABASE_ERROR);
            }
        });
    }

    public static void getPlayerSpecificDataAsync(
            String nameOrUUID,
            ValueType valueType,
            String key,
            FindValueCallback callback
    ) {
        getPlayerDataAsync(nameOrUUID, new FindOneCallback() {
            @Override
            public void onQueryError(ErrorType type) {
                callback.onQueryError(type);
            }

            @Override
            public void onQueryDone(Document document) {
                if (document == null) {
                    callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
                    return;
                }

                Object value = readNested(document, key);
                if (value == null) {
                    callback.onQueryDone(document, defaultFor(valueType), false);
                } else {
                    callback.onQueryDone(document, value, true);
                }
            }
        });
    }

    public static void getPlayerDataByNameAsync(String nickName, FindOneCallback callback) {
        MainframeRuntime.async().execute(() -> {
            try {
                Document query = new Document("latest-nickname", nickName.toLowerCase());
                Document result = playersCollection().find(query).first();
                if (result == null) {
                    callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
                } else {
                    callback.onQueryDone(result);
                }
            } catch (MongoException ex) {
                callback.onQueryError(ErrorType.DATABASE_ERROR);
            }
        });
    }

    public static void getPlayerDataAsync(String nameOrUUID, FindOneCallback callback) {
        MainframeRuntime.async().execute(() -> {
            PlayerLookup lookup = resolveLookup(nameOrUUID);
            if (lookup.error != null) {
                callback.onQueryError(lookup.error);
                return;
            }

            try {
                Document result = playersCollection().find(lookup.query).first();
                if (result == null) {
                    callback.onQueryError(ErrorType.UNKNOWN_PLAYER);
                } else {
                    callback.onQueryDone(result);
                }
            } catch (MongoException ex) {
                callback.onQueryError(ErrorType.DATABASE_ERROR);
            }
        });
    }

    private static Object readNested(Document document, String dottedKey) {
        String[] keyParts = dottedKey.split("\\.");
        Object value = document;
        for (String part : keyParts) {
            if (value instanceof Document doc) {
                value = doc.get(part);
            } else {
                return null;
            }
        }
        return value;
    }

    private static Object defaultFor(ValueType valueType) {
        return switch (valueType) {
            case INTEGER -> 0;
            case DOUBLE -> 0.0;
            case LONG -> 0L;
            case BOOLEAN -> false;
            case LIST -> Collections.emptyList();
            case OBJECT, STRING -> null;
        };
    }

    private static PlayerLookup resolveLookup(String nameOrUUID) {
        String param = nameOrUUID.toLowerCase();
        if (param.length() >= 3 && param.length() <= 16) {
            return new PlayerLookup(new Document("latest-nickname", param), null);
        }
        if (param.length() == 36) {
            return new PlayerLookup(new Document("uuid", param), null);
        }
        return new PlayerLookup(null, ErrorType.NOT_FORMATED_NAME);
    }

    private record PlayerLookup(Document query, ErrorType error) {
    }

    /** Avoids coupling PlayerDatabase to CoreMain for stack traces only */
    private static final class CoreMainSafeLog {
        static void log(MongoException ex) {
            ex.printStackTrace();
        }
    }
}
