package com.exloki.foruxi.core;

import com.exloki.foruxi.core.commands.ICommand;
import com.exloki.foruxi.core.config.PluginWrapper;
import com.exloki.foruxi.core.exceptions.InvalidArgumentsException;
import com.exloki.foruxi.core.exceptions.LException;
import com.exloki.foruxi.core.exceptions.PlayerNotFoundException;
import com.exloki.foruxi.core.utils.Persist;
import com.exloki.foruxi.core.utils.StringPair;
import com.exloki.foruxi.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.logging.Logger;

public class LPlugin extends PluginWrapper {

    /*
     * CORE
	 */

    protected PluginManager pm;
    public Gson gson;
    public Persist persist;

    public LPlugin(String messsagePrefix, String cmdClassPath) {
        this.gson = this.getGsonBuilder().create();
        this.persist = new Persist(this);

        this.messagePrefix = messsagePrefix;
        this.commandClassPath = cmdClassPath;
        this.logPrefix = "[" + this.getName() + "] ";
    }

    public boolean preEnable() {
        logIfDebug("Pre-enabling plugin...", false);

        loadLang();
        pm = getServer().getPluginManager();

        return true;
    }

	/*
	 * COMMAND HANDLING
	 */

    private String commandClassPath;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        return onLCommand(sender, command, commandLabel, args, LPlugin.class.getClassLoader(), commandClassPath);
    }

    public boolean onLCommand(final CommandSender cSender, final Command command, final String commandLabel, final String[] args, final ClassLoader classLoader, final String commandPath) {
        try {
            boolean isConsole = true;
            if (cSender instanceof Player) isConsole = false;

            ICommand cmd;

            try {
                Class<?> clazz = classLoader.loadClass(commandPath + command.getName());
                cmd = (ICommand) clazz.newInstance();
                cmd.setPlugin(this);
            } catch (Exception ex) {
                send(cSender, "This command is not currently loaded. If you think this is an error, please contact an Admin");
                Bukkit.getLogger().info(String.format("Command '%s' was executed by '%s', but the command is not loaded", commandLabel, cSender.getName()));
                ex.printStackTrace();
                return true;
            }

            if (!isConsole) {
                if (!cmd.getPermission().isEmpty() && !cSender.hasPermission(cmd.getPermission())) {
                    cSender.sendMessage(Msg.ER_PERMS.toString());
                    return true;
                }
            }

            try {
                cmd.run(getServer(), cSender, commandLabel, command, args);
                return true;
            } catch (InvalidArgumentsException ex) {
                if (ex.showHelpMessages() && cmd.hasHelpMessages()) {
                    for (int k = 0; k < cmd.getHelpMessages().size(); k++) {
                        StringPair pair = cmd.getHelpMessages().get(k);
                        if (pair.isOneSet() && !cSender.hasPermission(pair.getOne()))
                            continue;

                        cSender.sendMessage(pair.getZero());
                    }
                    return true;
                }

                if (!ex.getMessage().isEmpty()) {
                    cSender.sendMessage(Msg.ER_USAGE.withVars(ex.getMessage()));
                    return true;
                }

                if (!cmd.getUsage().equals("*unavailable*")) {
                    cSender.sendMessage(Msg.ER_USAGE.withVars(cmd.getUsage()));
                    return true;
                }

                cSender.sendMessage(Msg.ER_ERROR.withVars("Invalid command arguments"));
                return true;
            } catch (PlayerNotFoundException ex) {
                cSender.sendMessage(Msg.ER_PLAYER.toString());
                return true;
            } catch (LException ex) {
                cSender.sendMessage(ex.getMessage());
                return true;
            } catch (Exception ex) {
                cSender.sendMessage(Msg.ER_ERROR.withVars("An internal error has occured. Please contact an admin immediately!"));
                ex.printStackTrace();
                return true;
            }
        } catch (Throwable ex) {
            Bukkit.getLogger().severe(String.format("Failed to execute command '%s' [executer: %s]. Stack trace as follows:", commandLabel, cSender.getName()));
            ex.printStackTrace();
            return true;
        }
    }

    @Override
    public void saveConfig() {
        // We don't use any of the bukkit config writing, as this breaks our config file formatting.
    }

	/*
	 * CORE FILE PROCESSING
	 */

    private String languageFileName = "lang"; //For the future

    private void loadLang() {
        File lang = new File(getDataFolder(), languageFileName + ".yml");

        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe(String.format("[%s] Couldn't create language file.", this.getName()));
                getLogger().severe(String.format("[%s] This is a fatal error. Now disabling", this.getName()));
                this.setEnabled(false);
            }
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (Msg item : Msg.values())
            if (conf.getString(item.getPath()) == null)
                conf.set(item.getPath(), item.getDefault());

        Msg.ER_ERROR.setFile(conf);

        try {
            conf.save(lang);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/*
	 * MESSAGING
	 */

    protected String messagePrefix;

    private void send(CommandSender to, String message, String prefix) {
        if (message.contains("<br>")) {
            String[] messages = message.split("<br>");
            for (String msg : messages) {
                if (to instanceof Player)
                    to.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
                else
                    log(msg);
            }
        } else {
            if (to instanceof Player)
                to.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
            else
                log(message);
        }
    }

    public void send(CommandSender to, String message) {
        send(to, message, messagePrefix);
    }

    public void sendBlank(CommandSender to, String message) {
        send(to, message, "");
    }

    public void broadcast(String m) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', messagePrefix + m));
    }

    public void broadcastBlank(String m) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', m));
    }

	/*
	 * LOGGING
	 */

    protected static Logger logger = Bukkit.getLogger();
    private String logPrefix = "";
    public static final UUID LOKI_UUID = UUID.fromString("770789b7-a657-4b30-9418-41c803e170f4"); //For debug messages to myself when required
    public static final boolean DEBUG = false; // Enables debug for all plugins which use this core

    public void log(String log) {
        logger.info(logPrefix + log);
    }

    public void logBlank(String log) {
        logger.info(log);
    }

    public void logAsDebug(String message, boolean informLoki) {
        logger.info(logPrefix.substring(0, logPrefix.length() - 2) + " Debug] " + message);

        if (informLoki && Bukkit.getPlayer(LOKI_UUID) != null)
            Bukkit.getPlayer(LOKI_UUID).sendMessage("Debug: " + message);
    }

    public void logIfDebug(String message, boolean informLoki) {
        if (DEBUG) logAsDebug(message, informLoki);
    }

    public void logIfDebug(String message) {
        logIfDebug(message, false);
    }

	/*
	 * MISC
	 */

    public void reload() {
        loadLang();
    }

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    public void command(String command) {
        this.getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }
}
