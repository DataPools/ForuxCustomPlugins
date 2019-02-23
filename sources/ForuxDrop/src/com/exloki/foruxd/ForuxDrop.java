package com.exloki.foruxd;

import com.exloki.core_foruxd.LPlugin;
import com.exloki.foruxd.airdrop.AirDropManager;
import com.exloki.foruxd.gui.GUICreator;
import com.exloki.foruxd.gui.GUIListener;
import com.exloki.foruxd.hooks.VaultHook;
import com.exloki.foruxd.hooks.WorldGuardHook;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class ForuxDrop extends LPlugin {
    public static ForuxDrop i;
    private static transient Settings settings;

    private static AirDropManager airDropManager;

	/*
     * CONSTRUCTION / ENABLE / DISABLE
	 */

    public ForuxDrop() {
        super("[Forux Drop] ", "foruxdrop", ForuxDrop.class.getPackage().getName() + ".commands.Command");
        i = this;
    }

    @Override
    public void onEnable() {
        if (!preEnable())
            return;

        logIfDebug("Enabling plugin...", false);

        VaultHook.i.hook();
        WorldGuardHook.i.hook();
        settings = new Settings(this);

        airDropManager = new AirDropManager();

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

        final GUIListener guiListener = new GUIListener();
        pm.registerEvents(guiListener, this);

        logIfDebug("All listeners successfully registered", false);
    }

	/*
	 * UTILS / GETTERS
	 */

    @Override
    public void reload() {
        super.reload();

        settings.reloadConfig();

        airDropManager.reload();

        GUICreator.refreshInventory();
    }

    public static AirDropManager getAirDropManager() {
        return airDropManager;
    }

    public static Settings getSettings() {
        return settings;
    }
}
