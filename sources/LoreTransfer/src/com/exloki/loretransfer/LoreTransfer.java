package com.exloki.loretransfer;

import com.exloki.loretransfer.core.LPlugin;
import com.exloki.loretransfer.listeners.PlayerListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class LoreTransfer extends LPlugin {
    public static LoreTransfer i;
    private static transient Settings settings;

	/*
     * CONSTRUCTION / ENABLE / DISABLE
	 */

    public LoreTransfer() {
        super("[LoreTransfer] ", LoreTransfer.class.getPackage().getName() + ".commands.Command");
        i = this;
    }

    @Override
    public void onEnable() {
        if (!preEnable())
            return;

        logIfDebug("Enabling plugin...", false);

        settings = new Settings(this);
        messagePrefix = settings.getPrefixMessage();

        registerListeners(pm);

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        logIfDebug("Disabling plugin...", false);

        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

	/*
	 * LISTENERS
	 */

    private void registerListeners(PluginManager pm) {
        HandlerList.unregisterAll(this);

        logIfDebug("Registering listeners", false);

        final PlayerListener playerListener = new PlayerListener();
        pm.registerEvents(playerListener, this);

        logIfDebug("All listeners successfully registered", false);
    }

	/*
	 * UTILS / GETTERS
	 */

    @Override
    public void reload() {
        super.reload();

        settings.reloadConfig();

        //TODO
    }

    public static Settings getSettings() {
        return settings;
    }
}
