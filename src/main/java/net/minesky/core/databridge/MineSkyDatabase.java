package net.minesky.core.databridge;

import com.mongodb.MongoException;
import net.minesky.api.database.UpdatedData;
import net.minesky.api.database.PlayerDatabase;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.callbacks.ErrorType;
import net.minesky.core.databridge.callbacks.FindOneCallback;
import net.minesky.core.databridge.callbacks.SetOneCallback;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;

public class MineSkyDatabase {

    public static void createPlayerData(String uuid, String name) {
        PlayerDatabase.getPlayerDataAsync(uuid, new FindOneCallback() {
            @Override
            public void onQueryError(ErrorType type) {
                // nao tem uuid, então checa se tem por nick!
                PlayerDatabase.getPlayerDataAsync(name, new FindOneCallback() {
                    @Override
                    public void onQueryError(ErrorType type) {
                        // nao tem uuid e nem o nick, criando do zero:
                        MineSkyDatabase.forceCreatePlayerData(uuid, name);
                        CoreMain.logger.info("[Database] Player "+name+ " registrado na Database!");
                    }

                    // não tem uuid mas tem nick, atualizando a uuid então!
                    @Override
                    public void onQueryDone(Document document) {

                        PlayerDatabase.setPlayerData(uuid, new UpdatedData("uuid",
                                uuid), new SetOneCallback() {
                            @Override
                            public void onSetDone() {
                                CoreMain.logger.info("[Database] Player "+name+" had his UUID changed and the Database updated his UUID!");
                            }

                            @Override
                            public void onSetError(ErrorType errorType) {
                                CoreMain.logger.info("[Database] Player "+name+" had his UUID changed, tried to change his UUID on database but a error occured: "+errorType);
                            }
                        });

                    }
                });

                return;
            }

            @Override
            public void onQueryDone(Document document) {
                String latestNickname = document.getString("latest-nickname");

                // já tem na database, checando se o jogador trocou o nick.
                if(!latestNickname.equalsIgnoreCase(name)) {

                    PlayerDatabase.setPlayerData(uuid, new UpdatedData("latest-nickname",
                            name.toLowerCase()), new SetOneCallback() {
                        @Override
                        public void onSetDone() {
                            CoreMain.logger.info("[Database] Player "+name+" changed his nick-name, database updated!");
                        }

                        @Override
                        public void onSetError(ErrorType errorType) {
                            CoreMain.logger.info("[Database] Player "+name+" changed his nick-name, tried to update his data but an error occured: "+errorType);

                        }
                    });
                }

            }
        });
    }

    public static void forceCreatePlayerData(String uuid, String name) {
        try {
            Document novoDocumento = new Document();
            novoDocumento.append("uuid", uuid)
                    .append("first-login", Instant.now().toEpochMilli())
                    .append("latest-nickname", name.toLowerCase())
                    .append("first-nickname", name.toLowerCase())
                    .append("discord", new Document()
                            .append("status", "nv"))
                    .append("emote-wardrobe", new Document()
                            .append("enabled", false)
                            .append("current-emote", "wave")
                            .append("teleport", false))
                    /*.append("furniture-wardrobe", new Document()
                            .append("furniture", "sofa_moderno_grande")
                            .append("atual", "sofa_moderno_grande")
                            .append("wardrobe", false)
                            .append("cinematic", false))*/
                    .append("profile", new Document()
                            .append("banner", "padrao"))
                    .append("terrenos", new ArrayList<String>());

            MineSkyDB.getPlayersCollection().insertOne(novoDocumento);
        } catch (MongoException ex) {
            ex.printStackTrace();
        }
    }

}
