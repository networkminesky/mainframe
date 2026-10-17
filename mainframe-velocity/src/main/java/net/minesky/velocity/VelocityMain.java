package net.minesky.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import net.minesky.core.CoreMain;
import net.minesky.core.runtime.MainframeRuntime;
import net.minesky.velocity.commands.MainCommand;
import net.minesky.velocity.events.Events;
import net.minesky.velocity.speedtest.VelocitySpeedTestBridge;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "mainframe",
        name = "Mainframe",
        version = "2.0.0-SNAPSHOT",
        url = "https://minesky.com.br",
        authors = {"Drawn"}
)
public class VelocityMain {

    public static ProxyServer staticServer;
    public static ChannelIdentifier testChannel = new LegacyChannelIdentifier("mainframe:test");

    private final ProxyServer server;
    private final Path dataDirectory;
    private final java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger("mainframe");

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        staticServer = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        julLogger.info("Mainframe (Velocity) starting...");

        MainframeRuntime.bootstrap(
                CoreMain.PlatformType.VELOCITY,
                dataDirectory.toFile(),
                julLogger,
                task -> server.getScheduler().buildTask(this, task).schedule(),
                VelocityMain.class
        );
        MainframeRuntime.bindSpeedTest(new VelocitySpeedTestBridge(server));

        server.getEventManager().register(this, new Events());
        server.getChannelRegistrar().register(testChannel);

        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("mainframev")
                .aliases("mfv", "mainframevelocity")
                .plugin(this)
                .build();
        commandManager.register(commandMeta, new MainCommand());
    }
}
