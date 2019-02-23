package com.exloki.balancedfly.core;

import com.exloki.balancedfly.Msg;
import com.exloki.balancedfly.core.commands.CommandMeta;
import com.exloki.balancedfly.core.commands.CoreCommand;
import com.exloki.balancedfly.core.persist.YamlConfigurationFile;
import com.exloki.balancedfly.core.persist.YamlResourceFile;
import com.exloki.balancedfly.core.persist.YamlResourceFileManager;
import com.exloki.balancedfly.core.utils.JPersist;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public abstract class LPlugin extends JavaPlugin {

    protected PluginManager pm;

    @Getter
    public Gson gson;
    @Getter
    public JPersist jpersist;

    @Getter
    private YamlResourceFileManager resourceFileManager;


    public LPlugin(String messsagePrefix) {
        this.gson = this.getGsonBuilder().create();
        this.jpersist = new JPersist(this);

        this.messagePrefix = messsagePrefix;
        this.logPrefix = "[" + this.getName() + "] ";
    }

    @Override
    public void onEnable() {
        try {
            if(!Files.exists(getDataFolder().toPath())) {
                Files.createDirectory(getDataFolder().toPath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            log("Unable to create plugin directory, disabling...");
            this.setEnabled(false);
            return;
        }

        pm = getServer().getPluginManager();

        resourceFileManager = new YamlResourceFileManager(this);
        resourceFileManager.hookToObject(this);
        resourceFileManager.writeDefaults();
        resourceFileManager.loadAll();

        loadLang();
        onStart();
    }

    @Override
    public void onDisable() {
        close();
        onStop();
    }

    public abstract void onStart();
    public abstract void onStop();

	/*
	 * Command handling
	 */

    public final <T extends CoreCommand> T registerCommand(T command) {
        PluginCommand pluginCommand = getCommand(command.getName());
        if (pluginCommand == null) {
            try {
                Constructor commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                commandConstructor.setAccessible(true);
                pluginCommand = (PluginCommand) commandConstructor.newInstance(command.getName(), this);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not register command " + command.getName());
            }
            CommandMap commandMap;
            try {
                PluginManager pluginManager = Bukkit.getPluginManager();
                Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (CommandMap) commandMapField.get(pluginManager);
            } catch (Exception ex) {
                throw new IllegalStateException("Could not register command " + command.getName());
            }
            CommandMeta annotation = command.getClass().getAnnotation(CommandMeta.class); //Get the commandMeta
            if (annotation != null) {
                pluginCommand.setAliases(Arrays.asList(annotation.aliases()));
                pluginCommand.setDescription(annotation.description());
                pluginCommand.setUsage(annotation.usage());
            }
            commandMap.register(this.getDescription().getName(), pluginCommand); //Register it with Bukkit
        }
        pluginCommand.setExecutor(command); //Set the executor
        pluginCommand.setTabCompleter(command); //Tab completer

        if (command.getPlugin() == null)
            command.setPlugin(this);
        else
            command.setPlugin(null);
        getLogger().info("Registered command /" + command.getName());

        return command;
    }

    public final void registerCommands(CoreCommand... commands) {
        for (CoreCommand command : commands) registerCommand(command);
    }

	/*
	 * Core file processing
	 */

    @YamlResourceFile(raw = true, filename = "lang.yml")
    private YamlConfigurationFile languagesFile = null;

    private void loadLang() {
        if(languagesFile == null) {
            throw new IllegalStateException("languagesFile cannot be null");
        }

        for (Msg item : Msg.values()) {
            if (languagesFile.getConfig().getString(item.getPath()) == null)
                languagesFile.getConfig().set(item.getPath(), item.getDefault());
        }

        Msg.setFile(languagesFile.getConfig());

        try {
            languagesFile.getConfig().save(languagesFile.getConfigFile());
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
        resourceFileManager.loadAll();
        loadLang();

        for(Reloadable reloadable : reloadables) {
            reloadable.reload();
        }
    }

    /*
     * Automatic object closure functionality
     */

    private Set<Closeable> closeables = new HashSet<>();

    public void registerCloseable(Closeable closeable) {
        closeables.add(closeable);
    }

    public void close() {
        for(Closeable closeable : closeables) {
            closeable.onClose();
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

    public Path getPluginFolder() {
        return getDataFolder().toPath();
    }

    @Override
    public void saveConfig() {
        // We don't use any of the bukkit config writing, as this breaks our config file formatting.
    }
}
