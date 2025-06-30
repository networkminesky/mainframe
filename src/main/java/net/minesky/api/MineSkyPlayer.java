package net.minesky.api;

import net.minesky.api.mineskymod.MineSkyModHandler;
import net.minesky.api.mineskymod.MineSkyModPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class MineSkyPlayer {

    private final Player spigotplayer;
    private final MineSkyModPlayer modplayer;
    private final boolean bedrock;

    public MineSkyPlayer(Player spi) {
        this.spigotplayer = spi;
        this.bedrock = APIUtils.isBedrockUser(spi.getUniqueId());
        if(MineSkyModHandler.mineskyModPlayers.containsKey(spi.getUniqueId())) {
            this.modplayer = new MineSkyModPlayer(this,
                    MineSkyModHandler.mineskyModPlayers.get(spi.getUniqueId()));
        } else {
            this.modplayer = null;
        }
    }

    @Nullable
    public MineSkyModPlayer getModPlayer() {
        return this.modplayer;
    }

    public void titleFade(int fadeIn, int fade, int fadeOut) {
        getSpigotPlayer().sendTitle(APIUtils.c("§0\uE001⌭\uE001⌭\uE001"), "", fadeIn, fade, fadeOut);
    }

    public boolean usingMineSkyMod() {
        return this.modplayer != null;
    }

    public static MineSkyPlayer from(Player p) { return new MineSkyPlayer(p); }

    public void sendErrorMessage(String s) {
        getSpigotPlayer().sendMessage(APIUtils.hex("&#f54236⌇ "+s));
    }

    public boolean isBedrock() {
        return this.bedrock;
    }

    public String getCachedMainClass() {
        return ""; //PlayerEvents.mainClass.getOrDefault(getSpigotPlayer().getUniqueId(), "");
    }

    public void sendNoPermission() {
        sendErrorMessage("Esse comando não existe ou você não possui permissão.");
    }

    public Player getSpigotPlayer() {
        return this.spigotplayer;
    }

    public void sendWarnMessage(String s) {
        getSpigotPlayer().sendMessage(APIUtils.hex("&#f5a214⌌ "+s));
    }

}
