package net.minesky.core;

import net.minesky.api.messaging.Messaging;
import net.minesky.core.databridge.MineSkyDB;
import net.minesky.core.messaging.DefaultListener;
import net.minesky.core.messaging.MessagingObserver;
import net.minesky.core.servers.Server;
import net.minesky.core.servers.ServerData;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public final class CoreMain {

    public enum PlatformType {
        VELOCITY,
        SPIGOT
    }

    public static PlatformType currentPlatform;
    public static File pluginDirectory;
    public static Logger logger;

    private static UUID thisServerUUID;
    private static Server thisServer;

    private CoreMain() {
    }

    public static boolean isOnVelocity() {
        return currentPlatform == PlatformType.VELOCITY;
    }

    public static Server getCurrentServer() {
        if (thisServer == null) {
            thisServer = new Server(new ServerData(currentPlatform, "id", "name", getCurrentServerUUID()));
        }
        return thisServer;
    }

    public static void setCurrentServer(ServerData data) {
        thisServer = new Server(data);
        thisServerUUID = data.uuid();
    }

    public static UUID getCurrentServerUUID() {
        if (thisServerUUID == null) {
            thisServerUUID = UUID.randomUUID();
        }
        return thisServerUUID;
    }

    public static void initializeCredentials(Class<?> classLoader) {
        try {
            TXTReader.saveDefaultCredentialsTXT(classLoader);
            TXTReader.getMongoDbURI();
        } catch (IOException ex) {
            logger.severe("Failed to load credentials.txt: " + ex.getMessage());
        }
    }

    public static void simpleInitialization(PlatformType type, File dataDirectory, Logger logger, Class<?> classLoader) {
        CoreMain.pluginDirectory = dataDirectory;
        CoreMain.logger = logger;
        initializeCredentials(classLoader);
        CoreMain.currentPlatform = type;
        initializeConnection();
    }

    public static void onDisable() {
        logger.info("Shutting down Mainframe (MongoDB + messaging)...");
        if (Messaging.getMessagingObserver() != null) {
            Messaging.getMessagingObserver().disconnect();
        }
        if (MineSkyDB.mongoClient != null) {
            MineSkyDB.mongoClient.close();
        }
    }

    public static void initializeConnection() {
        logger.info("[MongoDB] Connecting. URI: " + MineSkyDB.getURI());
        MineSkyDB.firstConnect();

        if (new File(pluginDirectory, "disable-messaging.txt").exists()) {
            logger.warning("[Messaging] disable-messaging.txt found — messaging disabled.");
            return;
        }

        logger.info("[Messaging] Starting change-stream observer...");
        MessagingObserver observer = new MessagingObserver();
        Messaging.setMessagingObserver(observer);
        observer.addListener("mainframe", new DefaultListener());
    }
}
