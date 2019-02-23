package com.exloki.foruxd.itemdata;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemName extends ItemData implements IItemData {
    private static ItemName i = new ItemName();

    public static ItemName get() {
        return i;
    }

    public ItemName() {
        super(ItemPart.NAME, new String[]{"na.", "name.", "displayname.", "display_name."});
        setApplyToMeta(true);
    }

    @Override
    public ItemStack applyValue(ItemStack item, String value) throws DataException {
        if (item == null) return null;

        ItemMeta meta = applyMetaValue(item.getItemMeta(), value);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public ItemMeta applyMetaValue(ItemMeta meta, String value) throws DataException {
        if (!value.isEmpty()) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', value).replaceAll("_", " "));
            return meta;
        }

        throw new DataException("Empty strings cannot be used for ItemStack names!");
    }
}
