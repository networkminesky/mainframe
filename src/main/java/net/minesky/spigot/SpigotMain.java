package net.minesky.spigot;

import net.minesky.api.messaging.Messaging;
import net.minesky.core.CoreMain;
import net.minesky.spigot.commands.MainCommand;
import net.minesky.spigot.events.Events;
import net.minesky.spigot.misc.SpigotPluginListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SpigotMain extends JavaPlugin {

    public static Logger l;
    public static SpigotMain instance;

    public static SpigotMain getInstance() {
        return instance;
    }

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        l = this.getLogger();

        l.info("Initializing plugin.");
        l.info("Running on Paper/Spigot.");

        CoreMain.simpleInitialization(CoreMain.PlatformType.SPIGOT, this.getDataFolder(), this.getLogger(), this.getClass());

        this.getCommand("mainframe").setExecutor(new MainCommand());

        getServer().getPluginManager().registerEvents(new Events(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "mainframe:test", new SpigotPluginListener());

    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        CoreMain.onDisable();
    }

}