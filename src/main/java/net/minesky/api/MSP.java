package net.minesky.api;

public class MSP {

    public static String error(String s) {
        return APIUtils.hex("&#db2f23"+s);
    }

    public static String warn(String s) {
        return APIUtils.hex("&#dbb323"+s);
    }

}