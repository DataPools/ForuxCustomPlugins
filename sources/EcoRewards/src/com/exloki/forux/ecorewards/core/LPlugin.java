package com.exloki.forux.ecorewards.core;

import com.exloki.forux.ecorewards.Msg;
import com.exloki.forux.ecorewards.core.commands.ICommand;
import com.exloki.forux.ecorewards.core.commands.InvalidArgumentsException;
import com.exloki.forux.ecorewards.core.commands.LException;
import com.exloki.forux.ecorewards.core.commands.PlayerNotFoundException;
import com.exloki.forux.ecorewards.core.utils.Pair;
import com.exloki.forux.ecorewards.core.utils.Persist;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LPlugin extends JavaPlugin {

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
        loadLang();
        pm = getServer().getPluginManager();

        return true;
    }

	/*
	 * Command handling
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
            } catch (InvalidArgumentsException ex) {
                if (ex.showHelpMessages() && cmd.hasHelpMessages()) {
                    for (int k = 0; k < cmd.getHelpMessages().size(); k++) {
                        Pair<String, String> pair = cmd.getHelpMessages().get(k);
                        if (pair.isOneSet() && !cSender.hasPermission(pair.getOne()))
                            continue;

                        cSender.sendMessage(pair.getZero());
                    }
                } else if (!ex.getMessage().isEmpty()) {
                    cSender.sendMessage(Msg.ER_USAGE.with(ex.getMessage()));
                } else if (!cmd.getUsage().isEmpty()) {
                    cSender.sendMessage(Msg.ER_USAGE.with(cmd.getUsage()));
                } else {
                    cSender.sendMessage(Msg.ER_ERROR.with("Invalid command arguments"));
                }
            } catch (PlayerNotFoundException ex) {
                cSender.sendMessage(Msg.ER_PLAYER.toString());
            } catch (LException ex) {
                cSender.sendMessage(ex.getMessage());
            } catch (Exception ex) {
                cSender.sendMessage(Msg.ER_ERROR.with("An internal error has occurred. Please contact an admin immediately!"));
                ex.printStackTrace();
            }
        } catch (Throwable ex) {
            Bukkit.getLogger().severe(String.format("Failed to execute command '%s' [executor: %s]. Stack trace as follows:", commandLabel, cSender.getName()));
            ex.printStackTrace();
        }

        return true;
    }

	/*
	 * Core file processing
	 */

    protected Path languageFilePath;

    private void loadLang() {
        languageFilePath = getDataFolder().toPath().resolve("lang.yml");

        if (!Files.exists(languageFilePath)) {
            try {
                Files.createDirectory(languageFilePath.getParent());
                Files.createFile(languageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe(String.format("[%s] Couldn't create language file.", this.getName()));
                getLogger().severe(String.format("[%s] This is a fatal error. Now disabling", this.getName()));
                this.setEnabled(false);
                return;
            }
        }

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(languageFilePath.toFile());
        for (Msg item : Msg.values()) {
            if (conf.getString(item.getPath()) == null)
                conf.set(item.getPath(), item.getDefault());
        }

        Msg.setFile(conf);

        try {
            conf.save(languageFilePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Reload functionality
     */

    private Set<Reloadable> reloadables = new HashSet<>();

    public void registerReloadable(Reloadable reloadable) {
        reloadables.add(reloadable);
    }

    public void reload() {
        loadLang();

        for(Reloadable reloadable : reloadables) {
            reloadable.reload();
        }
    }

	/*
	 * Messaging
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
	 * Logging
	 */

    protected static Logger logger = Bukkit.getLogger();
    private String logPrefix = "";

    public void log(String log) {
        logger.info(logPrefix + log);
    }

    public void logBlank(String log) {
        logger.info(log);
    }

	/*
	 * Miscellaneous
	 */

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    @Override
    public void saveConfig() {
        // We don't use any of the bukkit config writing, as this breaks our config file formatting.
    }
}
