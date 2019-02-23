package com.exloki.forux.ecorewards.listeners;

import com.exloki.forux.ecorewards.EcoRewards;
import com.exloki.forux.ecorewards.managers.GUIManager;
import com.exloki.forux.ecorewards.runnables.BalanceWatcherTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private EcoRewards plugin;

    public PlayerListener(EcoRewards plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getTaskManager().registerTask(player.getUniqueId(), new BalanceWatcherTask(plugin, player).runTaskTimer(plugin, 200, 200));
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(player.hasMetadata(GUIManager.IN_USE_META)) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(player.hasMetadata(GUIManager.IN_USE_META)) {
            player.removeMetadata(GUIManager.IN_USE_META, plugin);
        }
    }
}
