package net.minesky.core.databridge;

import com.mongodb.MongoException;
import net.minesky.api.database.PlayerDatabase;
import net.minesky.api.database.UpdatedData;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.callbacks.ErrorType;
import net.minesky.core.databridge.callbacks.FindOneCallback;
import net.minesky.core.databridge.callbacks.SetOneCallback;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;

public final class MineSkyDatabase {

    private MineSkyDatabase() {
    }

    public static void createPlayerData(String uuid, String name) {
        PlayerDatabase.getPlayerDataAsync(uuid, new FindOneCallback() {
            @Override
            public void onQueryError(ErrorType type) {
                PlayerDatabase.getPlayerDataAsync(name, new FindOneCallback() {
                    @Override
                    public void onQueryError(ErrorType type) {
                        forceCreatePlayerData(uuid, name);
                        CoreMain.logger.info("[Database] Player " + name + " registered.");
                    }

                    @Override
                    public void onQueryDone(Document document) {
                        PlayerDatabase.setPlayerData(uuid, new UpdatedData("uuid", uuid), new SetOneCallback() {
                            @Override
                            public void onSetDone() {
                                CoreMain.logger.info("[Database] Updated UUID for " + name);
                            }

                            @Override
                            public void onSetError(ErrorType errorType) {
                                CoreMain.logger.warning("[Database] UUID update failed for " + name + ": " + errorType);
                            }
                        });
                    }
                });
            }

            @Override
            public void onQueryDone(Document document) {
                String latestNickname = document.getString("latest-nickname");
                if (latestNickname != null && !latestNickname.equalsIgnoreCase(name)) {
                    PlayerDatabase.setPlayerData(uuid, new UpdatedData("latest-nickname", name.toLowerCase()),
                            new SetOneCallback() {
                                @Override
                                public void onSetDone() {
                                    CoreMain.logger.info("[Database] Nick updated for " + name);
                                }

                                @Override
                                public void onSetError(ErrorType errorType) {
                                    CoreMain.logger.warning("[Database] Nick update failed for " + name + ": " + errorType);
                                }
                            });
                }
            }
        });
    }

    public static void forceCreatePlayerData(String uuid, String name) {
        try {
            Document novoDocumento = new Document()
                    .append("uuid", uuid)
                    .append("first-login", Instant.now().toEpochMilli())
                    .append("latest-nickname", name.toLowerCase())
                    .append("first-nickname", name.toLowerCase())
                    .append("discord", new Document().append("status", "nv"))
                    .append("emote-wardrobe", new Document()
                            .append("enabled", false)
                            .append("current-emote", "wave")
                            .append("teleport", false))
                    .append("profile", new Document().append("banner", "padrao"))
                    .append("terrenos", new ArrayList<String>());

            MineSkyDB.getPlayersCollection().insertOne(novoDocumento);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }
}
