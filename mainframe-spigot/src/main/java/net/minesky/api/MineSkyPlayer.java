package net.minesky.api;

import net.minesky.api.mineskymod.MineSkyModHandler;
import net.minesky.api.mineskymod.MineSkyModPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MineSkyPlayer {

    private final Player spigotPlayer;
    private final MineSkyModPlayer modPlayer;
    private final boolean bedrock;

    public MineSkyPlayer(Player player) {
        this.spigotPlayer = player;
        this.bedrock = APIUtils.isBedrockUser(player.getUniqueId());
        if (MineSkyModHandler.mineskyModPlayers.containsKey(player.getUniqueId())) {
            this.modPlayer = new MineSkyModPlayer(this, MineSkyModHandler.mineskyModPlayers.get(player.getUniqueId()));
        } else {
            this.modPlayer = null;
        }
    }

    public static MineSkyPlayer from(Player p) {
        return new MineSkyPlayer(p);
    }

    @Nullable
    public MineSkyModPlayer getModPlayer() {
        return modPlayer;
    }

    public Player getSpigotPlayer() {
        return spigotPlayer;
    }

    public boolean isBedrock() {
        return bedrock;
    }

    public boolean usingMineSkyMod() {
        return modPlayer != null;
    }

    public void titleFade(int fadeIn, int fade, int fadeOut) {
        spigotPlayer.sendTitle(APIUtils.c("§0\uE001⌭\uE001⌭\uE001"), "", fadeIn, fade, fadeOut);
    }

    public void sendErrorMessage(String s) {
        spigotPlayer.sendMessage(APIUtils.hex("&#f54236⌇ " + s));
    }

    public void sendWarnMessage(String s) {
        spigotPlayer.sendMessage(APIUtils.hex("&#f5a214⌌ " + s));
    }

    public void sendNoPermission() {
        sendErrorMessage("Esse comando não existe ou você não possui permissão.");
    }

    public String getCachedMainClass() {
        return "";
    }
}
