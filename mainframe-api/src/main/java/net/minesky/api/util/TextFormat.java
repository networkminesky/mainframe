package net.minesky.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Platform-neutral & / &#RRGGBB formatting (section sign output). */
public final class TextFormat {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private TextFormat() {
    }

    public static String legacy(String message) {
        return translateAlternateColorCodes('&', hex(message));
    }

    public static String hex(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, hexToSection(matcher.group(1)));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static List<String> legacy(List<String> lines) {
        List<String> out = new ArrayList<>(lines.size());
        for (String line : lines) {
            out.add(legacy(line));
        }
        return out;
    }

    private static String hexToSection(String hex) {
        StringBuilder magic = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            magic.append('§').append(c);
        }
        return magic.toString();
    }

    private static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
