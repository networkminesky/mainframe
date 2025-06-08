package net.minesky.spigot.spigotapi;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minesky.spigot.SpigotMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NetworkUtils {

    public static void connectPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);

        player.sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void rawMessagePlayer(String playerName, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MessageRaw");
        out.writeUTF(playerName);
        out.writeUTF(message);

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
    }
    public static void rawProxyBroadcast(String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MessageRaw");
        out.writeUTF("ALL");
        out.writeUTF(message);

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void rawKickPlayerFromProxy(String playerName, String reasonMessage) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("KickPlayer");
        out.writeUTF(playerName);
        out.writeUTF(reasonMessage);

        Iterables.getFirst(Bukkit.getOnlinePlayers(), null).sendPluginMessage(SpigotMain.getInstance(), "BungeeCord", out.toByteArray());
    }

}
