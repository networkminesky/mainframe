package net.minesky.spigot.speedtest;

import net.minesky.core.CoreMain;
import net.minesky.core.messaging.MessagingSender;
import net.minesky.core.runtime.SpeedTestBridge;
import net.minesky.spigot.SpigotMain;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpigotSpeedTestBridge implements SpeedTestBridge {

    private long mongoSpeed;
    private long bungeeSpeed;
    private long initialLong;

    private final SpigotMain plugin;

    public SpigotSpeedTestBridge(SpigotMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public void sendPluginChannelSpeedProbe(String serverName, long timestampMillis) {
        // handled on Velocity
    }

    @Override
    public void onMongoSpeedProbe(long originTimestampMillis) {
        mongoSpeed = System.currentTimeMillis();
        initialLong = originTimestampMillis;
        CoreMain.logger.info("[SPEED-TEST] Mongo message received on backend.");
        awaitResults();
    }

    @Override
    public void onSpeedResultsLine(String line) {
        CoreMain.logger.info(line);
    }

    public void onBungeeProbe(long originTimestampMillis) {
        bungeeSpeed = System.currentTimeMillis();
        initialLong = originTimestampMillis;
        awaitResults();
    }

    private void awaitResults() {
        AtomicInteger ticks = new AtomicInteger();
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            if (ticks.incrementAndGet() > 10) {
                task.cancel();
                return;
            }
            if (mongoSpeed != 0 && bungeeSpeed != 0 && initialLong != 0) {
                List<String> msg = new ArrayList<>();
                msg.add("§a[SPEED-TEST] Done.");
                msg.add("§6[SPEED-TEST] Bungee: " + (bungeeSpeed - initialLong) + "ms");
                msg.add("§6[SPEED-TEST] Mongo: " + (mongoSpeed - initialLong) + "ms");

                StringBuilder sb = new StringBuilder();
                for (String line : msg) {
                    sb.append("☺").append(line);
                    CoreMain.logger.info(line);
                }
                MessagingSender.publish("test-speed-results", sb.toString());

                mongoSpeed = 0;
                bungeeSpeed = 0;
                initialLong = 0;
                task.cancel();
            }
        }, 1, 20);
    }
}
