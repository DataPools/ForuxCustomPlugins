package com.exloki.foruxd.gui;

import com.exloki.foruxd.ForuxDrop;
import com.exloki.foruxd.TL;
import com.exloki.foruxd.airdrop.AirDropPackage;
import com.exloki.foruxd.hooks.VaultHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class GUIListener implements Listener {

    public static final String TRACKING_META = "foruxdrops.in_gui";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(player.hasMetadata(TRACKING_META)) {
            event.setCancelled(true);

            if(event.getRawSlot() == event.getSlot()) {
                int slot = event.getSlot();

                if(!GUICreator.getCurrentSlotMap().containsKey(slot)) {
                    return;
                }
                AirDropPackage pack = GUICreator.getCurrentSlotMap().get(slot);

                if(VaultHook.economy.getBalance(player) >= pack.getPrice()) {
                    Location target = player.getLocation();
                    Vector vector = target.getDirection();
                    vector.setY(0);
                    vector.multiply(3);
                    target.add(vector);

                    if(ForuxDrop.getAirDropManager().callAirDrop(player, target, pack)) {
                        VaultHook.economy.withdrawPlayer(player, pack.getPrice());
                        player.sendMessage(TL.AIR_DROP_IBNOUND.toString());
                    }
                } else {
                    player.sendMessage(TL.ER_CANNOT_AFFORD_DROP.toString());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(player.hasMetadata(TRACKING_META)) {
            player.removeMetadata(TRACKING_META, ForuxDrop.i);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(event.getFrom().distance(event.getTo()) > 0.1 && player.hasMetadata(TRACKING_META)) {
            player.closeInventory();
            player.removeMetadata(TRACKING_META, ForuxDrop.i);
        }
    }
}
