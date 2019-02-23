package com.exloki.forux.ecorewards.managers;

import com.exloki.forux.ecorewards.EcoRewards;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class GUIManager {

    public static final String IN_USE_META = "ecorewards.in_gui";

    private static Inventory currentInventory;

    public static void reloadInventory(EcoRewards plugin) {
        ImmutableMap<Double, TierManager.TierData> map = plugin.getTierManager().getTierDataMap();

        int size = ((map.size() + 9 - 1) / 9) * 9;
        Inventory newInventory = Bukkit.createInventory(null, size, plugin.getSettings().getGuiTitle());

        int k = 0;
        for(Map.Entry<Double, TierManager.TierData> entry : map.entrySet()) {
            newInventory.setItem(k++, entry.getValue().getDisplayItem());
        }

        currentInventory = newInventory;
    }

    public static Inventory getCurrentInventory(EcoRewards plugin) {
        if(currentInventory == null) {
            reloadInventory(plugin);
        }

        return currentInventory;
    }
}
