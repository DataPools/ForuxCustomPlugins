package com.exloki.foruxi.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    private TimeUtils() {
    }

    public enum TimeUnit {
        YEARS(7, Long.parseLong("29030400000"), "year", "years", "y"),
        MONTHS(6, Long.parseLong("2419200000"), "month", "months", "mo"),
        WEEKS(5, 604800000, "week", "weeks", "w"),
        DAYS(4, 86400000, "day", "days", "d"),
        HOURS(3, 3600000, "hour", "hours", "h", "hr", "hrs"),
        MINUTES(2, 60000, "minute", "minutes", "m", "min", "mins"),
        SECONDS(1, 1000, "second", "seconds", "s", "sec", "secs"),
        MILLISECONDS(0, 1, "millisecond", "milliseconds", "ms");

        String[] Aliases;
        long Multi;
        int id;

        TimeUnit(int id, long multi, String... aliases) {
            this.id = id;
            Aliases = aliases;
            Multi = multi;
        }

        public int getId() {
            return id;
        }

        public String[] getAliases() {
            return Aliases;
        }

        public String getLongSingular() {
            return Aliases[0];
        }

        public String getLongPlural() {
            return Aliases[1];
        }

        public String getShortAlias() {
            return Aliases[2];
        }

        public long getMultiplier() {
            return Multi;
        }

        public boolean isMoreAccurateThan(TimeUnit other) {
            return this.id < other.getId();
        }

        public boolean isLessAccurateThan(TimeUnit other) {
            return this.id > other.getId();
        }

        public boolean isEqualOrMoreAccurateThan(TimeUnit other) {
            return this.id <= other.getId();
        }

        public boolean isEqualOrLessAccurateThan(TimeUnit other) {
            return this.id >= other.getId();
        }

        public boolean matches(String input) {
            for (String string : Aliases) {
                if (input.equalsIgnoreCase(string))
                    return true;
            }

            return false;
        }

        public static TimeUnit match(String input) {
            for (TimeUnit unit : TimeUnit.values()) {
                if (unit.matches(input))
                    return unit;
            }

            return null;
        }
    }

	/*
     * Current Time
	 */

    public static String getCurrentTime() {
        return getCurrentTime("MM-dd hh:mm:ss a");
    }

    public static String getCurrentTime(String format) {
        return getFormattedTime(System.currentTimeMillis(), format);
    }

    public static boolean isInLast(long timestamp, long milliseconds) {
        return System.currentTimeMillis() - timestamp <= milliseconds;
    }

    public static boolean isInLast(long timestamp, int amount, TimeUnit unit) {
        return System.currentTimeMillis() - timestamp <= (amount * unit.getMultiplier());
    }

	/*
	 * Formatting Static Time
	 */

    public static String getFormattedTime(long mili, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date(mili);
        return dateFormat.format(date);
    }

    public static String getFormattedTime(long mili) {
        return getFormattedTime(mili, "MM-dd hh:mm:ss a");
    }

	/*
	 * Misc
	 */

    /**
     * Uses the timeout timestamp to invoke the getTimeSpan(); method
     */
    public static String getTimeRemaining(long timeout) {
        long span = timeout - System.currentTimeMillis();

        return getTimeSpan(span);
    }

    /**
     * Converts milliseconds to a formatted string equivalent (1000ms to '1 second')
     */
    public static String getTimeSpan(long timeInMilis) {
        StringBuilder time = new StringBuilder();
        double[] timesList = new double[4];
        String[] unitsList = new String[]{"second", "minute", "hour", "day"};

        timesList[0] = timeInMilis / 1000;
        timesList[1] = Math.floor(timesList[0] / 60);
        timesList[0] = timesList[0] - (timesList[1] * 60);
        timesList[2] = Math.floor(timesList[1] / 60);
        timesList[1] = timesList[1] - (timesList[2] * 60);
        timesList[3] = Math.floor(timesList[2] / 24);
        timesList[2] = timesList[2] - (timesList[3] * 24);

        for (int j = 3; j > -1; j--) {
            double d = timesList[j];
            if (d < 1) continue;
            time.append((int) d).append(" ").append(unitsList[j]).append(d > 1 ? "s " : " ");
        }

        return time.toString().trim();
    }

    /**
     * Converts milliseconds to a formatted string equivalent (1000ms to '1 second')
     */
    public static String getTimeSpan(long timeInMilis, TimeUnit minUnit, TimeUnit maxUnit, boolean shortUnits, boolean printZeros) {
        StringBuilder time = new StringBuilder();

        for (TimeUnit unit : TimeUnit.values()) {
            if (unit.isEqualOrMoreAccurateThan(maxUnit) && unit.isEqualOrLessAccurateThan(minUnit)) {
                long unitValue = timeInMilis / unit.getMultiplier();
                if (unitValue > 0) {
                    time.append(unitValue + (shortUnits ? unit.getShortAlias() + " " : " " + (unitValue > 1 ? unit.getLongPlural() : unit.getLongSingular()) + " "));
                } else {
                    time.append(printZeros ? "0" + (shortUnits ? unit.getShortAlias() + " " : " " + unit.getLongPlural() + " ") : "");
                }
                timeInMilis = timeInMilis % unit.getMultiplier();
            }
        }

        if (time.toString().trim().isEmpty()) {
            return "0 " + minUnit.getLongPlural();
        }

        return time.toString().trim();
    }

    private static final Pattern TIME_INPUT_PATTERN = Pattern.compile("^(?i)(t:)*(\\d+)(mo|s|m|h|d|w)*$");

    /**
     * Gets an expiration long based on string input of duration (example: t:3d, t:5mo, 1w, etc)
     */
    public static long getTimeoutFromString(String stringFormat) {
        return System.currentTimeMillis() + getLengthFromString(stringFormat);
    }

    /**
     * Gets an `length` long based on string input of duration (example: t:3d, t:5mo, 1w, etc)
     */
    public static long getLengthFromString(String stringFormat) {
        try {
            Matcher matcher = TIME_INPUT_PATTERN.matcher(stringFormat);
            if (matcher.matches()) {
                return NumberUtils.parseInt(matcher.group(2), 0) * (matcher.group(3) != null ? TimeUnit.match(matcher.group(3)).getMultiplier() : 1000);
            }
        } catch (Exception ex) {
            return 0; // Catch any malformed input exceptions
        }

        return 0;
    }
}
