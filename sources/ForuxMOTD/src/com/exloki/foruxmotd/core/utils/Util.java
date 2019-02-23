package com.exloki.foruxmotd.core.utils;

import com.exloki.foruxmotd.core.transform.Transformer;

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
        List<T> list = new ArrayList<>(values.length);

        for(T val : values) {
            list.add(val);
        }

        return list;
    }

    public static <T> Set<T> asSet(T... values) {
        Set<T> set = new HashSet<>(values.length);
        Collections.addAll(set, values);

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

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue( Map<K, V> map ) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
                return ( o1.getValue() ).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }

        return result;
    }

    public static <T> void safeRemoveElement(Collection<T> collection, T element) {
        Iterator<T> iterator = collection.iterator();
        while(iterator.hasNext()) {
            T next = iterator.next();
            if(next.equals(element)) {
                iterator.remove();
                return;
            }
        }
    }

    public interface MatchFunc<T> {
        boolean matches(T obj);
    }

    public static <T> void safeRemoveElement(Collection<T> collection, MatchFunc<T> removeFunc) {
        Iterator<T> iterator = collection.iterator();
        while(iterator.hasNext()) {
            T next = iterator.next();
            if(removeFunc.matches(next)) {
                iterator.remove();
                return;
            }
        }
    }

    public static <T> void safeRemoveElements(Collection<T> collection, MatchFunc<T> removeFunc) {
        Iterator<T> iterator = collection.iterator();
        while(iterator.hasNext()) {
            T next = iterator.next();
            if(removeFunc.matches(next)) {
                iterator.remove();
            }
        }
    }

    public static <T> T findElement(Collection<T> collection, MatchFunc<T> matchFunc) {
        for(T element : collection) {
            if(matchFunc.matches(element)) {
                return element;
            }
        }

        return null;
    }

    /*
     * Array functions
     */

    public static <T> boolean contains(T[] array, T element) {
        return contains(array, element, false);
    }

    public static <T> boolean contains(T[] array, T element, boolean operatorComparison) {
        for(T type : array) {
            if(operatorComparison ? type == element : type.equals(element)) {
                return true;
            }
        }

        return false;
    }

    public static <T> void insertAt(int index, T[] array, T val) {
        for(int k = array.length-1; k >= index; k--) {
            if(k == index) {
                array[k] = val;
                return;
            }
            array[k] = array[k-1];
        }
    }

    public static <T> void shiftElements(T[] array, T defaultElement, int shift) {
        if(shift > 0) {
            System.arraycopy(array, 0, array, shift, array.length - shift);
            Arrays.fill(array, 0, shift, defaultElement);
        } else {
            int abs = Math.abs(shift);
            System.arraycopy(array, abs, array, 0, array.length - abs);
            Arrays.fill(array, array.length - abs, array.length, defaultElement);
        }
    }

    public static <T> void rotateElements(T[] array, int order) {
        if (array == null || array.length == 0 || order < 0) {
            throw new IllegalArgumentException("array cannot be null or empty; order cannot be less than zero");
        }

        if(order > array.length) {
            order = order % array.length;
        }

        //length of first part
        int a = array.length - order;

        reverse(array, 0, a-1);
        reverse(array, a, array.length-1);
        reverse(array, 0, array.length-1);

    }

    private static <T> void reverse(T[] array, int left, int right) {
        if(array == null || array.length == 1)
            return;

        while(left < right) {
            T temp = array[left];
            array[left] = array[right];
            array[right] = temp;
            left++;
            right--;
        }
    }

    /*
     * Number functions
     */

    public static boolean isNumeric(String string) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(string, pos);
        return string.length() == pos.getIndex();
    }

    public static boolean isInteger(String string) {
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
        if(timeInMilliseconds < 1000) {
            return useShortUnits ? "0s" : "0 seconds";
        }

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

    /*
     * Random utility
     */

    private static Random random = new Random();

    public static void renewRandom() {
        random = new Random();
    }

    public static int randomInt() {
        return randomInt(0, 100);
    }

    public static int randomInt(int exclMax) {
        return randomInt(0, exclMax);
    }

    public static int randomInt(int inclMin, int exclMax) {
        return random.nextInt(exclMax - inclMin) + inclMin;
    }

    public static double randomDouble() {
        return randomDouble(0.0, 1.0);
    }

    public static double randomDouble(double exclMax) {
        return randomDouble(0.0, exclMax);
    }

    public static double randomDouble(double inclMin, double exclMax) {
        return (random.nextDouble() * (exclMax - inclMin)) + inclMin;
    }

    public static <T> T randomValue(Collection<T> collection) {
        if(collection.size() == 0) {
            return null;
        }

        int j = randomInt(0, collection.size());
        if(collection instanceof List<?>) {
            return ((List<T>)collection).get(j);
        }

        int k = 0;
        for(T val : collection) {
            if(k++ == j) {
                return val;
            }
        }

        return null; // Should never be reached
    }

    public static <T> T randomValue(T[] array) {
        if(array.length == 0) {
            return null;
        }

        return array[randomInt(0, array.length)];
    }

    /*
     * Enum Functions
     */

    public static <E extends Enum<E>> EnumSet<E> getEnumsByPartialName(Class<E> clazz, E[] searchArray, String partialName) {
        EnumSet<E> enumSet = EnumSet.noneOf(clazz);
        for(E val : searchArray) {
            if(val.name().contains(partialName)) {
                enumSet.add(val);
            }
        }

        return enumSet;
    }

    /*
     * Miscellaneous
     */

    public static int roundUpToMultiple(int input, int multiple) {
        return (input + (multiple-1)) / multiple * multiple;
    }
}
