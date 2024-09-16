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

        InputStream r = classe.getResource("/credentials.txt").openStream();

        if(!CoreMain.pluginDirectory.exists()) {
            CoreMain.logger.info("Creating plugin directory folder...");
            CoreMain.pluginDirectory.mkdir();
        }

        if(r != null) {

            File file = new File(CoreMain.pluginDirectory, "credentials.txt");

            // Use um FileOutputStream para escrever os dados do InputStream no arquivo
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = r.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    r.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            credentialsTXT = file;
        }
    }
}
