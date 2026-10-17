package net.minesky.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class TXTReader {

    private static File credentialsTXT;

    private TXTReader() {
    }

    public static String getFromFile(File f) {
        try {
            return Files.readString(f.toPath(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMongoDbURI() {
        if (credentialsTXT == null) {
            throw new IllegalStateException("credentials.txt was not initialized");
        }
        return getFromFile(credentialsTXT);
    }

    public static void saveDefaultCredentialsTXT(Class<?> ignored) throws IOException {
        if (!CoreMain.pluginDirectory.exists()) {
            CoreMain.logger.info("Creating plugin data folder...");
            CoreMain.pluginDirectory.mkdirs();
        }

        File toFind = new File(CoreMain.pluginDirectory, "credentials.txt");
        if (!toFind.exists()) {
            CoreMain.logger.info("Creating empty credentials.txt");
            if (toFind.createNewFile()) {
                CoreMain.logger.info("credentials.txt created.");
            }
        } else {
            CoreMain.logger.info("credentials.txt loaded.");
        }
        credentialsTXT = toFind;
    }
}
