package com.exloki.foruxd.itemdata;

import org.bukkit.ChatColor;

import java.io.Serializable;

public class ItemSearchCriteria implements Serializable {

    private boolean ignoreEnchants;
    private boolean ignoreName;
    private boolean ignoreLore;
    private boolean ignoreDurability;

    public ItemSearchCriteria(boolean ignoreEnchants, boolean ignoreName, boolean ignoreLore, boolean ignoreDurability) {
        this.ignoreEnchants = ignoreEnchants;
        this.ignoreName = ignoreName;
        this.ignoreLore = ignoreLore;
        this.ignoreDurability = ignoreDurability;
    }

    public boolean getEnchantsCriteria() {
        return ignoreEnchants;
    }

    public boolean getIgnoreName() {
        return ignoreName;
    }

    public boolean getIgnoreLore() {
        return ignoreLore;
    }

    public boolean getIgnoreDurability() {
        return ignoreDurability;
    }

    public String getColouredString(ChatColor tru, ChatColor fals) {
        return ((ignoreEnchants ? tru + "Enchants " : fals + "Enchants ") +
                (ignoreName ? tru + "Name " : fals + "Name ") +
                (ignoreLore ? tru + "Lores " : fals + "Lores ") +
                (ignoreDurability ? tru + "Durability " : fals + "Durability "))
                .trim();
    }
}
