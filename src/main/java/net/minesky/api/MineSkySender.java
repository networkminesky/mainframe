package net.minesky.api;

import net.minesky.api.APIUtils;
import org.bukkit.command.CommandSender;

public class MineSkySender {

    private final CommandSender sender;

    public MineSkySender(CommandSender spi) {
        this.sender = spi;
    }

    public void sendErrorMessage(String s) {
        getSpigotSender().sendMessage(APIUtils.hex("&#f54236⌇ "+s));
    }

    public void sendNoPermission() {
        sendErrorMessage("Esse comando não existe ou você não possui permissão.");
    }

    public CommandSender getSpigotSender() {
        return this.sender;
    }

    public void sendWarnMessage(String s) {
        getSpigotSender().sendMessage(APIUtils.hex("&#f5a214⌌ "+s));
    }

}
