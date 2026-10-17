package net.minesky.api;

import org.bukkit.command.CommandSender;

public class MineSkySender {

    private final CommandSender sender;

    public MineSkySender(CommandSender spi) {
        this.sender = spi;
    }

    public CommandSender getSpigotSender() {
        return sender;
    }

    public void sendErrorMessage(String s) {
        sender.sendMessage(APIUtils.hex("&#f54236⌇ " + s));
    }

    public void sendWarnMessage(String s) {
        sender.sendMessage(APIUtils.hex("&#f5a214⌌ " + s));
    }

    public void sendNoPermission() {
        sendErrorMessage("Esse comando não existe ou você não possui permissão.");
    }
}
