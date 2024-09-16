package net.minesky.core;

import net.minesky.core.databridge.MineSkyDB;
import net.minesky.api.messaging.Messaging;
import net.minesky.core.messaging.MessagingObserver;
import net.minesky.core.messaging.TestListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class CoreMain {

    public enum PlatformType {
        VELOCITY,
        SPIGOT
    }

    public static PlatformType currentPlatform;
    public static File pluginDirectory;

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

        logger.info("[MongoDB-Messaging] Initializing MongoDB observers...");
        Messaging.setMessagingObserver(new MessagingObserver());

        logger.info("[MongoDB-Messaging] Initialized... Plugins can now register new Listeners using the Messaging class.");

        logger.info("[MongoDB-Messaging] Initializing a new test listener on environment "+currentPlatform);
        Messaging.getMessagingObserver().addListener("mainframe", new TestListener());
    }

}
