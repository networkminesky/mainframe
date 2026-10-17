package net.minesky.spigot.events;

import net.minesky.core.databridge.MineSkyDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MineSkyDatabase.createPlayerData(
                event.getPlayer().getUniqueId().toString(),
                event.getPlayer().getName()
        );
    }
}
