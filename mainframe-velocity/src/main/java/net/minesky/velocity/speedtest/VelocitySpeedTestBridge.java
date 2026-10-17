package net.minesky.velocity.speedtest;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.ProxyServer;
import net.minesky.core.CoreMain;
import net.minesky.core.runtime.SpeedTestBridge;
import net.minesky.velocity.VelocityMain;

public class VelocitySpeedTestBridge implements SpeedTestBridge {

    private final ProxyServer server;

    public VelocitySpeedTestBridge(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void sendPluginChannelSpeedProbe(String serverName, long timestampMillis) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeLong(timestampMillis);
        server.getServer(serverName).ifPresent(registered ->
                registered.sendPluginMessage(VelocityMain.testChannel, out.toByteArray())
        );
    }

    @Override
    public void onMongoSpeedProbe(long originTimestampMillis) {
        // handled on backend
    }

    @Override
    public void onSpeedResultsLine(String line) {
        CoreMain.logger.info(line);
    }
}
