package com.exloki.foruxd.itemdata;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface IItemData {
    ItemStack applyValue(ItemStack item, String value) throws DataException;

    ItemMeta applyMetaValue(ItemMeta item, String value) throws DataException;
}
