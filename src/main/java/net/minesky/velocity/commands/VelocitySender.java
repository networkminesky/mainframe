package net.minesky.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minesky.core.commands.Sender;

public class VelocitySender extends Sender {

    private final CommandSource source;

    public VelocitySender(CommandSource source) {
        this.source = source;
    }

    @Override
    public void sendMessage(String s) {
        source.sendMessage(LegacyComponentSerializer.legacySection().deserialize(s.replace("&", "§")));
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

}
