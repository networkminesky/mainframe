package net.minesky.spigot;

import net.minesky.core.CoreMain;
import net.minesky.core.runtime.MainframeRuntime;
import net.minesky.spigot.commands.MainCommand;
import net.minesky.spigot.events.Events;
import net.minesky.spigot.misc.SpigotPluginListener;
import net.minesky.spigot.speedtest.SpigotSpeedTestBridge;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMain extends JavaPlugin {

    private static SpigotMain instance;

    public static SpigotMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Mainframe (Spigot/Folia) starting...");

        MainframeRuntime.bootstrap(
                CoreMain.PlatformType.SPIGOT,
                getDataFolder(),
                getLogger(),
                task -> getServer().getAsyncScheduler().runNow(this, t -> task.run()),
                getClass()
        );
        MainframeRuntime.bindSpeedTest(new SpigotSpeedTestBridge(this));

        getCommand("mainframe").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "mainframe:test", new SpigotPluginListener());
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        CoreMain.onDisable();
    }
}
