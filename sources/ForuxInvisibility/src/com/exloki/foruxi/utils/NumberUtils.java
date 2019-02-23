package com.exloki.foruxi.utils;

public class NumberUtils {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * @param string - String to convert to int
     * @return int from string, -1 otherwise
     */
    public static int parseInt(String string) {
        int i;
        try {
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
        return i;
    }

    public static int parseInt(String string, int def) {
        int i;
        try {
            i = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return def;
        }
        return i;
    }

    public static double parseDouble(String string) {
        double i;
        try {
            i = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return -1;
        }
        return i;
    }
}
