package net.minesky.core.speedtest;

import net.minesky.core.CoreMain;
import net.minesky.core.messaging.MessagingSender;
import net.minesky.spigot.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SpigotTestResults {

    public static void awaitAll() {
        AtomicInteger n = new AtomicInteger();
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(SpigotMain.getInstance(), (task) -> {
            n.getAndIncrement();
            if(n.get() == 10) {
                CoreMain.logger.info("[SPEED-TEST] Speed Test timed out, only received the bungee speed measurement.");
                task.cancel();
                return;
            }

            if(SpeedTest.mongoSpeed != 0 && SpeedTest.bungeeSpeed != 0L && SpeedTest.initialLong != 0L) {
                List<String> msg = new ArrayList<>();
                StringBuilder sb = new StringBuilder();

                msg.add("§a[SPEED-TEST] Speed Test measurement concluded! Results:");
                msg.add("§a[SPEED-TEST] Initial Long (got from Mongo initial mills) = "+SpeedTest.initialLong + " | Diff: "+(SpeedTest.initialLong - System.currentTimeMillis()));
                msg.add("§6[SPEED-TEST] Bungee Speed : "+(SpeedTest.bungeeSpeed - SpeedTest.initialLong)+"ms");
                msg.add("§6[SPEED-TEST] MongoDB Speed : "+(SpeedTest.mongoSpeed - SpeedTest.initialLong)+"ms");

                for(String s : msg) {
                    sb.append("\\☺").append(s);
                    CoreMain.logger.info(s);
                }

                MessagingSender.sendMessage("test-speed-results", sb.toString());

                SpeedTest.initialLong = 0;
                SpeedTest.bungeeSpeed = 0;
                SpeedTest.mongoSpeed = 0;

                task.cancel();
                return;
            }

            n.getAndIncrement();
        }, 60, 20);
    }
}
