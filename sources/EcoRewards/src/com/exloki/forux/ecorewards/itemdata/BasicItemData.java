package com.exloki.forux.ecorewards.itemdata;

import org.bukkit.Material;

public class BasicItemData {
    private Material material;
    private short durability;

    public BasicItemData(Material type, short damage) {
        this.material = type;
        this.durability = damage;
    }

    public Material getType() {
        return material;
    }

    public short getDurability() {
        return durability;
    }
}
