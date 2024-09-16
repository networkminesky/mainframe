package net.minesky.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.MineSkyDatabase;

import java.util.UUID;

public class Events {

    @Subscribe
    public void PlayerChooseInitialServer(PlayerChooseInitialServerEvent e) {

        Player p = e.getPlayer();

        MineSkyDatabase.createPlayerData(p.getUniqueId().toString(), p.getUsername());

    }

    @Subscribe
    public void ProxyShutdownEvent(ProxyShutdownEvent e) {
        CoreMain.onDisable();
    }
}
