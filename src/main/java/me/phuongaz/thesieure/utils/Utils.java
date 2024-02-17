package me.phuongaz.thesieure.utils;

public class Utils {

    public static String randomString(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(str.length() * Math.random());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }
}
