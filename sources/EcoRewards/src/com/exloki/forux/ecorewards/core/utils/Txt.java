package com.exloki.forux.ecorewards.core.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Txt {
    public static final char FUNKY_ARROW_CHAR = '�';
    public static final char COLOUR_CODE_CHAR = '�';

    public static String parse(String string, Object... args) {
        // Parse the colours on the completed output!
        return parseColor(String.format(string, args));
    }

    public static String parseColor(String string) {
        string = string.replaceAll("(§([a-z0-9]))", "\u00A7$2");
        string = string.replaceAll("(&([a-z0-9]))", "\u00A7$2");
        string = string.replace("&&", "&");
        return string;
    }

    public static String stripColor(String string) {
        string = string.replaceAll("(�([a-z0-9]))", "");
        string = string.replaceAll("(&([a-z0-9]))", "");
        return string;
    }

    public static String upperCaseFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String loopArgs(List<String> args, int startPoint) {
        StringBuilder ret = new StringBuilder();
        for (int i = startPoint; i < args.size(); i++) {
            if (i != startPoint)
                ret.append(" ");

            ret.append(args.get(i));
        }
        return ret.toString();
    }

    public static String implode(Collection<String> list, String delimiter) {
        StringBuilder ret = new StringBuilder();
        int k = 0;
        for (String str : list) {
            if (k++ != 0)
                ret.append(delimiter);

            ret.append(str);
        }
        return ret.toString();
    }

    public static String implode(String[] array, String delimiter) {
        StringBuilder ret = new StringBuilder();
        int k = 0;
        for (String str : array) {
            if (k++ != 0)
                ret.append(delimiter);

            ret.append(str);
        }
        return ret.toString();
    }

    public static String repeat(String s, int times) {
        if (times <= 0)
            return "";
        return s + repeat(s, times - 1);
    }

    public static String cleanIpAddress(String input) {
        input = input.replaceAll("/", "").trim();
        return input.contains(":") ? input.split(":")[0] : input;
    }

    private static ConcurrentHashMap<String, String> specialCharsMap = new ConcurrentHashMap<>();

    static {
        specialCharsMap.put("'", "/apost");
        specialCharsMap.put("\\", "/bkslash");
        specialCharsMap.put("*", "/astr");
        specialCharsMap.put("(", "/openbr");
        specialCharsMap.put(")", "/closebr");
        specialCharsMap.put(";", "/semic");
        specialCharsMap.put("[", "/opensqbr");
        specialCharsMap.put("]", "/closesqbr");
        specialCharsMap.put("$", "/dolsign");
        specialCharsMap.put(":", "/colon");
    }

    public static String sanitize(String input) {
        if (input.isEmpty()) return "";

        for (Entry<String, String> ent : specialCharsMap.entrySet()) {
            input = input.replaceAll(Pattern.quote(ent.getKey()), Matcher.quoteReplacement(ent.getValue()));
        }

        return input.trim();
    }

    public static String unsanitize(String input) {
        if (input.isEmpty()) return "";

        for (Entry<String, String> ent : specialCharsMap.entrySet()) {
            input = input.replaceAll(Pattern.quote(ent.getValue()), Matcher.quoteReplacement(ent.getKey()));
        }

        return input.trim();
    }

    // -------------------------------------------- //
    // Paging and chrome-tools like titleize - Courtesy of MassiveCore
    // -------------------------------------------- //

    private final static int PAGEHEIGHT_PLAYER = 9;
    private final static int PAGEHEIGHT_CONSOLE = 50;
    private final static String titleizeLine = repeat("_", 52);
    private final static int titleizeBalance = -1;

    public static String titleize(String str) {
        return titleize(str, ChatColor.GOLD, ChatColor.WHITE);
    }

    public static String titleize(String str, ChatColor primaryColour, ChatColor secondaryColour) {
        String center = ".[ " + secondaryColour + str + primaryColour + " ].";
        int centerlen = ChatColor.stripColor(center).length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = (centerlen / 2) - titleizeBalance;
        int eatRight = (centerlen - eatLeft) + titleizeBalance;

        if (eatLeft < pivot)
            return primaryColour + titleizeLine.substring(0, pivot - eatLeft) + center + titleizeLine.substring(pivot + eatRight);
        return primaryColour + center;
    }

    public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title) {
        return getPage(lines, pageHumanBased, title, PAGEHEIGHT_PLAYER);
    }

    public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title, CommandSender sender) {
        return getPage(lines, pageHumanBased, title, (sender instanceof Player) ? Txt.PAGEHEIGHT_PLAYER : Txt.PAGEHEIGHT_CONSOLE);
    }

    public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title, int pageheight) {
        ArrayList<String> ret = new ArrayList<>();
        int pageZeroBased = pageHumanBased - 1;
        int pagecount = (int) Math.ceil(((double) lines.size()) / pageheight);

        ret.add(titleize(title + parse("<a>") + " " + pageHumanBased + "/" + pagecount));

        if (pagecount == 0) {
            ret.add(parse("&cSorry. No Pages available."));
            return ret;
        } else if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(parse("&cInvalid page. Must be between 1 and " + pagecount));
            return ret;
        }

        int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }

        ret.addAll(lines.subList(from, to));

        return ret;
    }
}
