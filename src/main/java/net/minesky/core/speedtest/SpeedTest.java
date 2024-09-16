package net.minesky.core.speedtest;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minesky.core.CoreMain;
import net.minesky.velocity.VelocityMain;

public class SpeedTest {

    public static long mongoSpeed = 0L;
    public static long bungeeSpeed = 0L;
    public static long initialLong = 0L;

    public static void detectMongoSpeed(final long initialMils) {
        mongoSpeed = System.currentTimeMillis();
        initialLong = initialMils;

        CoreMain.logger.info("[SPEED-TEST] Received MongoDB message!");
    }

    public static void sendVelocityMessage(String serverName) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(System.currentTimeMillis());

        VelocityMain.staticServer.getServer(serverName).ifPresent(a -> {
            a.sendPluginMessage(VelocityMain.testChannel, out.toByteArray());
        });

    }

    public static void detectBungeeSpeed(final long initialMils) {
        bungeeSpeed = System.currentTimeMillis();

        SpigotTestResults.awaitAll();

        CoreMain.logger.info("[SPEED-TEST] Received BungeeCord message! Measuring everything in 3s, the results will be shown in this exact console...");
    }

}
