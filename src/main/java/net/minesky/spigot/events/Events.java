package net.minesky.spigot.events;

import net.minesky.core.databridge.MineSkyDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        MineSkyDatabase.createPlayerData(p.getUniqueId().toString(), p.getName());

    }

}
