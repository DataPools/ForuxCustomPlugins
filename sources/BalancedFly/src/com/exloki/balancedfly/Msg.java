package com.exloki.balancedfly;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Msg {
    // Error Messages
    ER_ERROR("error-error", "&4Error&f: <var>"),
    ER_USAGE("error-incorrect_usage", "&4Error&f: Invalid parameters: &7<var>"),
    ER_PERMS("error-no_permissions", "&4Error&f: You lack the required permissions for this command"),
    ER_PLAYER("error-player_not_found", "&4Error&f: Player not found"),

    CMD_PRIMARY("cmd-primary", "&7"),
    CMD_SECONDARY("cmd-secondary", "&r&o"),

    SUCCESS("success_colour", "&a"),
    FAILURE("failure_colour", "&c"),

    WARMUP_STARTING("&aFlight will be toggled in 10 seconds..."),
    WARMUP_INTERRUPTED("&cFlight toggle cancelled, did you move?"),

    FLIGHT_DISALLOWED("&cFlying is not allowed in this area!")

    // End
    ;

    protected static FileConfiguration LANG;

    private String path;
    private String def;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param defaultMessage The default string.
     */
    Msg(String path, String defaultMessage) {
        this.path = path;
        this.def = defaultMessage;
    }

    Msg(String defaultMessage) {
        this.path = name().toLowerCase();
        this.def = defaultMessage;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(path, def));
    }

    // Convenience
    public String get() {
        return toString();
    }

    private static final Pattern varPattern = Pattern.compile("(?i)<var>");

    /**
     * Replaces all instances of &gt;var&lt; with the specified variables, in order
     *
     * @param variables
     * @return
     */
    public String with(Object... variables) {
        String input = toString();
        int k = 0;
        while (varPattern.matcher(input).find() && k < variables.length) {
            input = input.replaceFirst(varPattern.pattern(), Matcher.quoteReplacement(String.valueOf(variables[k])));
            k++;
        }

        return input;
    }

    /**
     * Replaces all instances of the specified strings with the specified replacement in the message
     *
     * @param replacementMap The map containing match & replacement strings
     * @param useRegexe      Whether or not Regular Expression should be used when searching
     */
    public String replace(Map<String, String> replacementMap, boolean useRegexe) {
        String input = toString();

        for (Entry<String, String> entry : replacementMap.entrySet()) {
            Pattern pat = Pattern.compile(useRegexe ? entry.getKey() : Pattern.quote(entry.getKey()));
            while (pat.matcher(input).find())
                input = input.replaceFirst(pat.pattern(), entry.getValue());
        }

        return input;
    }

    /**
     * Set the {@code FileConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(FileConfiguration config) {
        LANG = config;
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Get all the elements of this Enum.
     *
     * @return An arry of values for this Enum.
     */
    public Msg[] getValues() {
        return values();
    }
}