package net.minesky.spigot.misc;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minesky.spigot.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SpigotConnection {

    private SpigotConnection() {
    }

    public static void sendPlayerToServer(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendPlayerToServer(String playerName, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(playerName);
        out.writeUTF(server);
        Player any = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (any != null) {
            any.sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
