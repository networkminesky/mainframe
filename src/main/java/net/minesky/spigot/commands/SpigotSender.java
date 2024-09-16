package net.minesky.spigot.commands;

import net.minesky.core.commands.Sender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SpigotSender extends Sender {

    private final CommandSender sender;

    public SpigotSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String s) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

}
