package net.minesky.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import net.minesky.core.CoreMain;
import net.minesky.core.databridge.MineSkyDatabase;

public class Events {

    @Subscribe
    public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        MineSkyDatabase.createPlayerData(
                event.getPlayer().getUniqueId().toString(),
                event.getPlayer().getUsername()
        );
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        CoreMain.onDisable();
    }
}
