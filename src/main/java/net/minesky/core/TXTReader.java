package net.minesky.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TXTReader {

    public static String getFromFile(File f) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(f.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String getMongoDbURI() {
        return getFromFile(credentialsTXT);
    }

    private static File credentialsTXT;

    public static void saveDefaultCredentialsTXT(Class<?> classe) throws IOException {
        if(!CoreMain.pluginDirectory.exists()) {
            CoreMain.logger.info("Creating plugin directory folder...");
            CoreMain.pluginDirectory.mkdir();
        }

        File toFind = new File(CoreMain.pluginDirectory, "credentials.txt");
        if(!toFind.exists()) {
            CoreMain.logger.info("Creating default credentials.txt");
            boolean created = toFind.createNewFile();
            if (created) {
                CoreMain.logger.info("Empty credentials.txt created successfully.");
            }
        } else {
            CoreMain.logger.info("credentials found! Loading it in.");
        }

        credentialsTXT = toFind;
    }
}