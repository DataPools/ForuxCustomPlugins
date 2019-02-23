package com.exloki.loretransfer.listeners;

import com.exloki.loretransfer.LoreTransfer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.ANVIL) {
            final Inventory inventory = event.getClickedInventory();

            if(event.getSlotType() == InventoryType.SlotType.RESULT) {
                // Check for enchanted books in either slot
                ItemStack book0 = inventory.getItem(0);
                ItemStack book1 = inventory.getItem(1);

                if(book0 == null || book1 == null) return;

                boolean enchantedBook0 = false;
                boolean enchantedBook1 = false;

                if(book0 != null && book0.getType().equals(Material.ENCHANTED_BOOK) && book0.hasItemMeta() && book0.getItemMeta().hasLore()) {
                    enchantedBook0 = true;
                }
                if(book1 != null && book1.getType().equals(Material.ENCHANTED_BOOK) && book1.hasItemMeta() && book1.getItemMeta().hasLore()) {
                    enchantedBook1 = true;
                }

                ItemStack resultItem = event.getCurrentItem();
                if(resultItem != null) {
                    if(enchantedBook0 || enchantedBook1) {
                        event.getView().setCursor(inventory.getItem(2));
                        inventory.clear();
                    }
                }
            } else {
                // Check for enchanted book in the other slot or cursor
                ItemStack cursor = event.getCursor();
                ItemStack other = inventory.getItem(event.getSlot() == 0 ? 1 : 0);

                handleNonResultClick(other, cursor, event.getClickedInventory());
            }
        } else if(event.getClick() == ClickType.SHIFT_LEFT && event.getCurrentItem() != null) {
            // Shift clicking
            if(event.getView().getTopInventory().getType() == InventoryType.ANVIL) {
                Inventory anvil = event.getView().getTopInventory();
                if(anvil.getItem(0) == null && anvil.getItem(1) != null) {
                    handleNonResultClick(anvil.getItem(1), event.getCurrentItem(), anvil);
                } else if(anvil.getItem(1) == null && anvil.getItem(0) != null) {
                    handleNonResultClick(anvil.getItem(0), event.getCurrentItem(), anvil);
                }
            }
        }
    }

    private void handleNonResultClick(ItemStack inSlot, ItemStack inComing, final Inventory anvilInventory) {
        if((inSlot == null || inSlot.getType() == Material.AIR) || (inComing == null || inComing.getType() == Material.AIR)) return;

        List<String> lores = null;
        boolean fromCursor = false;
        if(inSlot.getType() == Material.ENCHANTED_BOOK) {
            if(inSlot.hasItemMeta() && inSlot.getItemMeta().hasLore()) {
                lores = inSlot.getItemMeta().getLore();
            }
        }
        if(inComing.getType() == Material.ENCHANTED_BOOK) {
            if(inComing.hasItemMeta() && inComing.getItemMeta().hasLore()) {
                if(lores == null) {
                    lores = inComing.getItemMeta().getLore();
                    fromCursor = true;
                } else {
                    return; // Don't transfer lores between enchanted books
                }
            }
        }

        if(lores == null) return;

        final ItemStack newItem = (fromCursor ? inSlot : inComing).clone();
        ItemMeta meta = newItem.hasItemMeta() ? newItem.getItemMeta() : Bukkit.getItemFactory().getItemMeta(newItem.getType());
        if(meta.hasLore()) {
            lores.addAll(0, meta.getLore());
            meta.setLore(lores);
        } else {
            meta.setLore(lores);
        }
        newItem.setItemMeta(meta);

        // Set a tick after this event
        new BukkitRunnable() {
            public void run() {
                if(anvilInventory != null) {
                    anvilInventory.setItem(2, newItem);
                }
            }
        }.runTask(LoreTransfer.i);
    }
}
