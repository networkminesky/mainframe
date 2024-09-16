package net.minesky.api;

import org.bukkit.ChatColor;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIUtils {

    public static String textWithBackground(String s) {

        StringBuilder bd = new StringBuilder();

        for(char c : ChatColor.stripColor(s).toCharArray()) {
            bd.append("\uF820\uF802").append(c);
        }

        return "\uF820\uF802 " + bd;
    }

    public static String hexColor(String s) {
        return APIUtils.hex(s);
    }

    public static String rarityNameAndColor(String rarity) {
        switch(rarity.toLowerCase()) {
            case "comum": {
                return APIUtils.c("&f\uF815 &7");
            }
            case "incomum": {
                return APIUtils.c("&f\uF816 &2");
            }
            case "raro": {
                return APIUtils.c("&f\uF817 &3");
            }
            case "epico":
            case "epica": {
                return APIUtils.hex("&f\uF818 &#9a35c4");
            }
            case "lendario": {
                return APIUtils.c("&f\uF819 &6");
            }
            default: {
                return "";
            }
        }
    }

    public static List<String> c(List<String> list) {

        int n = 0;
        for(String s : list) {
            list.set(n, hex(s));
            n++;
        }

        return list;
    }

    public static String rarityColor(String rarity) {
        switch(rarity.toLowerCase()) {
            case "comum": {
                return APIUtils.c("&7");
            }
            case "incomum": {
                return APIUtils.c("&2");
            }
            case "raro": {
                return APIUtils.c("&3");
            }
            case "epico":
            case "epica": {
                return APIUtils.hex("&#9a35c4");
            }
            case "lendario": {
                return APIUtils.c("&6");
            }
            default: {
                return "";
            }
        }
    }

    public static boolean isBedrockUser(UUID u) {
        return false;
        //return FloodgateApi.getInstance().isFloodgatePlayer(u);
    }

    private static String createHex(String hexString) {
        hexString = hexString.replace("&", "");
        return net.md_5.bungee.api.ChatColor.of(hexString).toString();
    }

    public static String hex(String message) {
        Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(result, createHex(matcher.group()));
        }

        matcher.appendTail(result);
        message = result.toString();

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String rarityName(String rarity) {
        switch (rarity.toLowerCase()) {
            case "comum": {
                return APIUtils.c("&f\uF815");
            }
            case "incomum": {
                return APIUtils.c("&f\uF816");
            }
            case "raro": {
                return APIUtils.c("&f\uF817");
            }
            case "epico":
            case "epica": {
                return APIUtils.c("&f\uF818");
            }
            case "lendario": {
                return APIUtils.c("&f\uF819");
            }
            default: {
                return "";
            }
        }
    }
    public static String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
