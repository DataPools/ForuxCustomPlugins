package com.exloki.foruxi;

import com.exloki.foruxi.core.LPlugin;
import com.exloki.foruxi.invisibility.InvisibilityManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class ForuxInvisibility extends LPlugin {
    public static ForuxInvisibility i;
    private static transient Settings settings;

    private InvisibilityManager manager = new InvisibilityManager();

	/*
     * CONSTRUCTION / ENABLE / DISABLE
	 */

    public ForuxInvisibility() {
        super("[ForuxInvisibility] ", ForuxInvisibility.class.getPackage().getName() + ".commands.Command");
        i = this;
    }

    @Override
    public void onEnable() {
        if (!preEnable())
            return;

        logIfDebug("Enabling plugin...", false);

        settings = new Settings(this);

        manager = new InvisibilityManager();

        registerListeners(pm);

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        logIfDebug("Disabling plugin...", false);

        manager.deactivateAll();

        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

	/*
	 * LISTENERS
	 */

    private void registerListeners(PluginManager pm) {
        HandlerList.unregisterAll(this);

        logIfDebug("Registering listeners", false);

        pm.registerEvents(manager, this);

        logIfDebug("All listeners successfully registered", false);
    }

	/*
	 * UTILS / GETTERS
	 */

    @Override
    public void reload() {
        super.reload();

        settings.reloadConfig();

    }

    public static Settings getSettings() {
        return settings;
    }

    public InvisibilityManager getInvisibilityManager() {
        return manager;
    }
}
