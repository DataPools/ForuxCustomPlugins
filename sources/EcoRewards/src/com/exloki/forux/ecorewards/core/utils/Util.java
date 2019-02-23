package com.exloki.forux.ecorewards.core.utils;

import com.exloki.forux.ecorewards.core.transform.Transformer;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

public class Util {

    /*
     * Map / List / Set functions
     */

    public static <T> Map<T, T> asMap(T... values) {
        Map<T, T> map = new HashMap<>();

        for(int k = 0; k < values.length; k++) {
            if((k & 1) != 0) {
                map.put(values[k-1], values[k]);
            }
        }

        return map;
    }

    public static <T> List<T> asList(T... values) {
        return Arrays.asList(values);
    }

    public static <T> Set<T> asSet(T... values) {
        Set<T> set = new HashSet<>();
        for(T value : values) {
            set.add(value);
        }

        return set;
    }

    public static <T, R> List<R> transformList(List<T> original, Transformer<T, R> transformer) {
        List<R> newList = new ArrayList<>();

        for(T value : original) {
            newList.add(transformer.transform(value));
        }

        return newList;
    }

    public static <T, R> Set<R> transformSet(Set<T> original, Transformer<T, R> transformer) {
        Set<R> newSet = new HashSet<>();

        for(T value : original) {
            newSet.add(transformer.transform(value));
        }

        return newSet;
    }

    /*
     * Number functions
     */

    public static boolean isNumeric(String string)
    {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(string, pos);
        return string.length() == pos.getIndex();
    }

    public static boolean isInteger(String string)
    {
        int index = 0;
        if(string.charAt(0) == '-' || string.charAt(0) == '+') {
            index = 1;
        }

        for(int k = index; k < string.length(); k++) {
            if(string.charAt(0) < '0' && string.charAt(0) > '9')
                return false;
        }

        return true;
    }

    public static int getInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static double getDouble(String string, double defaultValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /*
     * Time functions
     */

    public static String getFormalTimeRemaining(long millisecondsTimestamp) {
        long span = millisecondsTimestamp - System.currentTimeMillis();

        String ret = getFormalTimeFromMillis(span);
        return ret.isEmpty() ? "0 seconds" : ret;
    }

    public static String getFormalTimeFromMillis(long timeInMilliseconds) {
        return getFormalTimeFromMillis(timeInMilliseconds, false);
    }

    public static String getFormalTimeFromMillis(long timeInMilliseconds, boolean useShortUnits) {
        StringBuilder time = new StringBuilder();
        double[] timesList = new double[4];
        String[] unitsList = useShortUnits ? new String[]{"s", "m", "h", "d"} : new String[]{"second", "minute", "hour", "day"};

        timesList[0] = timeInMilliseconds / 1000;
        timesList[1] = Math.floor(timesList[0] / 60);
        timesList[0] = timesList[0] - (timesList[1] * 60);
        timesList[2] = Math.floor(timesList[1] / 60);
        timesList[1] = timesList[1] - (timesList[2] * 60);
        timesList[3] = Math.floor(timesList[2] / 24);
        timesList[2] = timesList[2] - (timesList[3] * 24);

        for (int j = 3; j > -1; j--) {
            double d = timesList[j];
            if (d < 1) continue;

            if(useShortUnits) {
                time.append((int) d).append(unitsList[j]).append(" ");
            } else {
                time.append((int) d).append(" ").append(unitsList[j]).append(d > 1 ? "s " : " ");
            }
        }

        return time.toString().trim();
    }
}
