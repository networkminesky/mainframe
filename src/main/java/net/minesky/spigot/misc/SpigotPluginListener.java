package net.minesky.spigot.misc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.minesky.core.speedtest.SpeedTest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class SpigotPluginListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        long l = in.readLong();

        SpeedTest.detectBungeeSpeed(l);

    }
}
