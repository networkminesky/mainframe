package net.minesky.api;

import net.minesky.api.util.TextFormat;
import org.bukkit.ChatColor;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.UUID;

public final class APIUtils {

    private APIUtils() {
    }

    public static String textWithBackground(String s) {
        StringBuilder bd = new StringBuilder();
        for (char c : ChatColor.stripColor(s).toCharArray()) {
            bd.append("\uF820\uF802").append(c);
        }
        return "\uF820\uF802 " + bd;
    }

    public static String hexColor(String s) {
        return hex(s);
    }

    public static String hex(String message) {
        return TextFormat.legacy(message);
    }

    public static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> c(List<String> list) {
        return TextFormat.legacy(list);
    }

    public static boolean isBedrockUser(UUID u) {
        return FloodgateApi.getInstance().isFloodgatePlayer(u);
    }

    public static String rarityNameAndColor(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "comum" -> c("&f\uF815 &7");
            case "incomum" -> c("&f\uF816 &2");
            case "raro" -> c("&f\uF817 &3");
            case "epico", "epica" -> hex("&f\uF818 &#9a35c4");
            case "lendario" -> c("&f\uF819 &6");
            default -> "";
        };
    }

    public static String rarityColor(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "comum" -> c("&7");
            case "incomum" -> c("&2");
            case "raro" -> c("&3");
            case "epico", "epica" -> hex("&#9a35c4");
            case "lendario" -> c("&6");
            default -> "";
        };
    }

    public static String rarityName(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "comum" -> c("&f\uF815");
            case "incomum" -> c("&f\uF816");
            case "raro" -> c("&f\uF817");
            case "epico", "epica" -> c("&f\uF818");
            case "lendario" -> c("&f\uF819");
            default -> "";
        };
    }
}
