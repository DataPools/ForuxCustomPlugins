package com.exloki.foruxd.gui;

import com.exloki.foruxd.ForuxDrop;
import com.exloki.foruxd.airdrop.AirDropPackage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUICreator {

    private static Map<Integer, AirDropPackage> currentSlotMap = new HashMap<>();
    private static Inventory currentInventory;

    public static void refreshInventory() {
        List<AirDropPackage> packages = ForuxDrop.getAirDropManager().getPackages();
        int size = packages.size() % 9 == 0 ? packages.size() : packages.size();
        int rem = size % 9;
        if (rem > 0) {
            size += 9 - rem;
        }

        Inventory inv = Bukkit.createInventory(null, size, ForuxDrop.getSettings().getDropMenuTitle());

        currentSlotMap.clear();
        for(int k = 0; k < packages.size(); k++) {
            inv.setItem(k, packages.get(k).getDisplayItem());
            currentSlotMap.put(k, packages.get(k));
        }

        currentInventory = inv;
    }

    public static Map<Integer, AirDropPackage> getCurrentSlotMap() {
        return currentSlotMap;
    }

    public static Inventory getCurrentInventory() {
        if(currentInventory == null) {
            refreshInventory();
        }

        Inventory clone = Bukkit.createInventory(null, currentInventory.getSize(), currentInventory.getTitle());
        clone.setContents(currentInventory.getContents());
        return clone;
    }
}
