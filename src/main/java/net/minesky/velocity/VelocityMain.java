package net.minesky.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import net.minesky.core.CoreMain;
import net.minesky.velocity.commands.MainCommand;
import net.minesky.velocity.events.Events;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "mainframe", name = "Mainframe Plugin - Velocity", version = "1.3.1-BETA",
        url = "https://minesky.com.br", authors = {"Drawn"})
public class VelocityMain {

    public final ProxyServer server;
    public static ProxyServer staticServer;

    private final Logger l;

    public static Path dataDirectory;

    public static ChannelIdentifier testChannel = new LegacyChannelIdentifier("mainframe:test");

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        staticServer = server;

        this.l = logger;

        VelocityMain.dataDirectory = dataDirectory;

        l.info("Initializing plugin.");
        l.info("Running on Velocity.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        server.getEventManager().register(this, new Events());

        CoreMain.simpleInitialization(CoreMain.PlatformType.VELOCITY, dataDirectory.toFile(), l, this.getClass());

        server.getChannelRegistrar().register(testChannel);

        CommandManager commandManager = server.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("mainframev")
                .aliases("mfv", "mainframevelocity")
                .plugin(this)
                .build();

        commandManager.register(commandMeta, new MainCommand());

    }

}
