package com.exloki.foruxi;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An enum for requesting strings from the language file. Credit to drtshock for the idea
 */
public enum Msg {
    // Error Messages
    ER_ERROR("error-error", "&4Error&f: <var>"),
    ER_USAGE("error-incorrect_usage", "&4Error&f: Invalid parameters: &7<var>"),
    ER_PERMS("error-no_permissions", "&4Error&f: You lack the required permissions for this command"),
    ER_CONSOLE("error-must_be_console", "Error: You cannot perform this command from console"),
    ER_PLAYER("error-player_not_found", "&4Error&f: Player not found"),

    SUCCESS("success_colour", "&a"),
    FAILURE("failure_colour", "&c"),

    COOLDOWN_IN_EFFECT("cooldown_in_effect", "&cYou must wait <var> until you can use this again."),
    INVISIBILITY_APPLIED("invisibility_applied", "&aYou are now invisible!"),
    INVISIBILITY_REMOVED("invisibility_removed", "&cYou are no longer invisible!"),
    INVISIBILITY_ABORTED("invisibility_aborted", "&cYour invisibility has been removed!"),

    // End
    ;

    protected static YamlConfiguration LANG;

    private String path;
    private String def;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    Msg(String path, String start) {
        this.path = path;
        this.def = start;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(path, def));
    }

    private static final Pattern varPattern = Pattern.compile("(?i)<var>");

    /**
     * Replaces all instances of &gt;var&lt; with the specified variables, in order
     *
     * @param variables
     * @return
     */
    public String withVars(String... variables) {
        String input = toString();
        int k = 0;
        while (varPattern.matcher(input).find() && k < variables.length) {
            input = input.replaceFirst(varPattern.pattern(), Matcher.quoteReplacement(variables[k]));
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
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public void setFile(YamlConfiguration config) {
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