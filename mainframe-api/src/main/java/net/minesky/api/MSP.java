package net.minesky.api;

import net.minesky.api.util.TextFormat;

public final class MSP {

    private MSP() {
    }

    public static String error(String s) {
        return TextFormat.hex("&#db2f23" + s);
    }

    public static String warn(String s) {
        return TextFormat.hex("&#dbb323" + s);
    }
}
