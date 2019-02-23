package com.exloki.forux.ecorewards;

import com.exloki.forux.ecorewards.core.LPlugin;
import com.exloki.forux.ecorewards.hooks.VaultHook;
import com.exloki.forux.ecorewards.listeners.PlayerListener;
import com.exloki.forux.ecorewards.managers.GUIManager;
import com.exloki.forux.ecorewards.managers.PlayerManager;
import com.exloki.forux.ecorewards.managers.TaskManager;
import com.exloki.forux.ecorewards.managers.TierManager;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

@Getter
public class EcoRewards extends LPlugin {

    private transient Settings settings;

    private transient PlayerManager playerManager;
    private transient TierManager tierManager;
    private transient TaskManager taskManager;

	/*
     * Enable / Disable
	 */

    public EcoRewards() {
        super("[EcoRewards] ", EcoRewards.class.getPackage().getName() + ".commands.Command");
    }

    @Override
    public void onEnable() {
        if (!preEnable())
            return;

        settings = new Settings(this);
        messagePrefix = settings.getPrefixMessage();

        playerManager = new PlayerManager(this);
        tierManager = new TierManager(this);
        taskManager = new TaskManager();

        VaultHook.setup();

        registerListeners(pm);

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        taskManager.flushTasks();

        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void reload() {
        super.reload();
        GUIManager.reloadInventory(this);
    }

	/*
	 * Listeners
	 */

    private void registerListeners(PluginManager pm) {
        HandlerList.unregisterAll(this);

        final PlayerListener playerListener = new PlayerListener(this);
        pm.registerEvents(playerListener, this);
    }

	/*
	 * Utilities
	 */


}
