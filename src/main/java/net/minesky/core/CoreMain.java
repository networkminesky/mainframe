package net.minesky.core;

import net.minesky.core.databridge.MineSkyDB;
import net.minesky.api.messaging.Messaging;
import net.minesky.core.messaging.MessagingObserver;
import net.minesky.core.messaging.DefaultListener;
import net.minesky.core.servers.Server;
import net.minesky.core.servers.ServerData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class CoreMain {

    public enum PlatformType {
        VELOCITY,
        SPIGOT
    }

    public static PlatformType currentPlatform;
    public static File pluginDirectory;

    private static UUID thisServerUUID;
    private static Server thisServer;

    public static boolean isOnVelocity() {
        return currentPlatform == PlatformType.VELOCITY;
    }

    public static Server getCurrentServer() {
        if(thisServer == null)
            thisServer = new Server(new ServerData(currentPlatform, "id", "name", getCurrentServerUUID()));

        return thisServer;
    }

    public static UUID getCurrentServerUUID() {
        if(thisServerUUID == null)
            thisServerUUID = UUID.randomUUID();

        return thisServerUUID;
    }

    public static Logger logger;

    public static void initializeCredentials(Class<?> classLoader) {
        try {
            TXTReader.saveDefaultCredentialsTXT(classLoader);

            TXTReader.getMongoDbURI(); // test the method
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }
    }

    public static void simpleInitialization(PlatformType type, File dataDirectory, Logger logger, Class<?> classLoader) {
        CoreMain.pluginDirectory = dataDirectory;
        CoreMain.logger = logger;

        CoreMain.initializeCredentials(classLoader);

        CoreMain.currentPlatform = type;

        CoreMain.initializeConnection();
    }

    public static void onDisable() {
        logger.info("Disabling plugin, shutting down MongoDB connections & observers...");
        Messaging.getMessagingObserver().disconnect();

        MineSkyDB.mongoClient.close();
    }

    public static void initializeConnection() {
        logger.info("[MongoDB] Iniciando conexão com a Database. URI usado: "+ MineSkyDB.getURI());
        MineSkyDB.firstConnect();

        if(new File(pluginDirectory, "disable-messaging.txt").exists()) {
            logger.warning("[MongoDB-Messaging] File disable-messaging.txt found! Not enabling the messaging module.");
            return;
        }

        logger.info("[MongoDB-Messaging] Initializing MongoDB observers...");
        Messaging.setMessagingObserver(new MessagingObserver());

        logger.info("[MongoDB-Messaging] Initialized... Plugins can now register new Listeners using the Messaging class.");

        logger.info("[MongoDB-Messaging] Initializing a new test listener on environment "+currentPlatform);

        Messaging.getMessagingObserver().addListener("mainframe", new DefaultListener());
    }

}
