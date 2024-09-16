package net.minesky.api.mineskymod;

import net.minesky.api.MineSkyPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MineSkyModHandler {

    //                   PLAYER|MOD VERSION
    public static HashMap<UUID, String> mineskyModPlayers = new HashMap<>();

    public static boolean isUsingMod(MineSkyPlayer player) {
        return mineskyModPlayers.containsKey(player.getSpigotPlayer().getUniqueId());
    }
    public static boolean isUsingMod(Player p) {
        return mineskyModPlayers.containsKey(p.getUniqueId());
    }

}
