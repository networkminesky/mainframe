package net.minesky.spigot.misc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.minesky.core.runtime.MainframeRuntime;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SpigotPluginListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!"mainframe:test".equals(channel) || MainframeRuntime.speedTest() == null) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        long origin = in.readLong();
        if (MainframeRuntime.speedTest() instanceof net.minesky.spigot.speedtest.SpigotSpeedTestBridge bridge) {
            bridge.onBungeeProbe(origin);
        }
    }
}
