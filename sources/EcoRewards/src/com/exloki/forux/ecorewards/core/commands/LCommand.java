package com.exloki.forux.ecorewards.core.commands;

import com.exloki.forux.ecorewards.Msg;
import com.exloki.forux.ecorewards.core.LPlugin;
import com.exloki.forux.ecorewards.core.utils.BukkitUtil;
import com.exloki.forux.ecorewards.core.utils.Pair;
import com.exloki.forux.ecorewards.core.utils.Txt;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class LCommand implements ICommand {
    @Getter
    private final transient String name;
    @Getter
    private final transient String permission;
    @Setter
    protected transient LPlugin plugin;

    protected String usageString;
    protected String descString;
    @Getter @Setter
    protected List<Pair<String, String>> helpMessages;

    // Command specific variables
    protected CommandSender sender;
    protected String label;
    protected Player player;
    protected boolean isConsole;
    protected List<String> args;

    protected LCommand(final String name, final String permission) {
        this.name = name;
        this.permission = permission;
    }

    protected LCommand(final String name, final String permission, final String usage) {
        this.name = name;
        this.permission = permission;
        this.usageString = usage;
    }

    protected LCommand(final String name, final String permission, final String usage, final String description) {
        this.name = name;
        this.permission = permission;
        this.usageString = usage;
        this.descString = description;
    }

    @Override
    public String getDescription() {
        return descString == null ? "" : descString;
    }

    @Override
    public String getUsage() {
        return usageString == null ? "" : usageString.replaceAll("<command>", label == null ? name : label);
    }

    @Override
    public boolean hasHelpMessages() {
        return helpMessages != null && !helpMessages.isEmpty();
    }

    public void addHelpMessage(String message, String permission) {
        if (helpMessages == null) helpMessages = new ArrayList<>();
        helpMessages.add(new Pair<>(Txt.parseColor(message), permission));
    }

    public void addHelpMessage(String message) {
        addHelpMessage(message, "");
    }

	/*
	 * Command Processing / Execution
	 */

    @Override
    public void run(final Server server, final CommandSender sender, final String commandLabel, final Command cmd, final String[] args) throws Exception {
        // Setup execution time specific variables
        this.sender = sender;
        this.label = commandLabel;
        this.isConsole = !(sender instanceof Player);
        this.player = isConsole ? null : (Player) sender;
        this.args = Arrays.asList(args);

        perform(commandLabel, cmd);
    }

    protected abstract void perform(final String commandLabel, final Command cmd) throws Exception;

	/*
	 * Finding Players
	 */

    protected Player getPlayer(UUID uuid) throws PlayerNotFoundException {
        return getPlayer(uuid, true);
    }

    protected Player getPlayer(UUID uuid, boolean throwException) throws PlayerNotFoundException {
        Player found = Bukkit.getPlayer(uuid);
        if (found == null) {
            if (throwException)
                throw new PlayerNotFoundException();
            return null;
        }

        return found;
    }

    protected Player getPlayer(final String searchTerm) throws PlayerNotFoundException {
        return getPlayer(searchTerm, true);
    }

    protected Player getPlayer(final String searchTerm, boolean throwException) throws PlayerNotFoundException {
        return getPlayer(Bukkit.getServer(), searchTerm, throwException);
    }

    protected Player getPlayer(final Server server, final String searchTerm, boolean throwException) throws PlayerNotFoundException {
        Player player = BukkitUtil.getPlayer(server, searchTerm);
        if(player != null)
            return player;

        if (throwException)
            throw new PlayerNotFoundException();
        return null;
    }

    protected Player getPlayer(final int argIndex) throws PlayerNotFoundException, InvalidArgumentsException {
        if (args.size() <= argIndex) {
            throw new InvalidArgumentsException();
        }
        if (args.get(argIndex).isEmpty()) {
            throw new PlayerNotFoundException();
        }

        return getPlayer(args.get(argIndex));
    }

    protected OfflinePlayer getOfflinePlayer(final String searchTerm) throws PlayerNotFoundException {
        return getOfflinePlayer(searchTerm, false);
    }

    protected OfflinePlayer getOfflinePlayer(final String searchTerm, final boolean deep) throws PlayerNotFoundException {
        OfflinePlayer found = BukkitUtil.getOfflinePlayer(searchTerm, deep);
        if(found != null) {
            return found;
        }

        throw new PlayerNotFoundException();
    }

	/*
	 * Argument Processing
	 */

    protected boolean argSet(int idx) {
        return this.args.size() >= idx + 1;
    }

    // String
    protected String arg(int idx, String def) {
        if (this.args.size() < idx + 1)
            return def;

        return this.args.get(idx);
    }

    protected String arg(int idx) {
        return this.arg(idx, null);
    }

    protected String argStr(int idx, String def) {
        return this.arg(idx, def);
    }

    protected String argStr(int idx) {
        return this.arg(idx, null);
    }

    // Integer
    protected Integer strAsInt(String str, Integer def) {
        if (str == null)
            return def;

        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return def;
        }
    }

    protected Integer argInt(int idx, Integer def) {
        return strAsInt(this.arg(idx), def);
    }

    protected Integer argInt(int idx) {
        return this.argInt(idx, null);
    }

    // Double
    protected Double strAsDouble(String str, Double def) {
        if (str == null)
            return def;

        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return def;
        }
    }

    protected Double argDouble(int idx, Double def) {
        return strAsDouble(this.arg(idx), def);
    }

    protected Double argDouble(int idx) {
        return this.argDouble(idx, null);
    }

    // Boolean
    protected Boolean strAsBool(String str) {
        str = str.toLowerCase();
        return str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1");
    }

    protected Boolean argBool(int idx, boolean def) {
        String str = this.arg(idx);
        if (str == null)
            return def;

        return strAsBool(str);
    }

    protected Boolean argBool(int idx) {
        return this.argBool(idx, false);
    }

    /*
     * Player messaging
     */

    protected void msg(Msg message) {
        sender.sendMessage(message.toString());
    }

    protected void msg(Msg message, String... vars) {
        sender.sendMessage(message.with(vars));
    }

    protected void msg(String message) {
        sender.sendMessage(Txt.parseColor(message));
    }

    protected void msg(String[] messages) {
        for (String str : messages) {
            msg(str);
        }
    }

    protected void msg(Collection<String> messages) {
        for (String str : messages) {
            msg(str);
        }
    }

    protected void msg(String message, Object... args) {
        sender.sendMessage(Txt.parse(message, args));
    }

    protected void msg(String[] messages, Object... args) {
        for (String str : messages) {
            msg(str, args);
        }
    }

    protected void msg(Collection<String> messages, Object... args) {
        for (String str : messages) {
            msg(str, args);
        }
    }
}