package com.exloki.foruxd.itemdata;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemType extends ItemData implements IItemData {
    private static ItemType i = new ItemType();

    public static ItemType get() {
        return i;
    }

    public ItemType() {
        super(ItemPart.TYPE, new String[]{"id.", "type.", "identity.", "item."});
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack applyValue(ItemStack item, String value) throws DataException {
        Material mat = Material.getMaterial(value);
        if (mat == null) {
            try {
                mat = Material.getMaterial(Integer.parseInt(value));
            } catch (NumberFormatException ex) {
                return null;
            } // Can't make an item without an ID!
        }
        if (mat == null) {
            throw new DataException(value + " is not a valid ItemStack type!");
        }

        if (item != null) {
            item.setType(mat);
            return item;
        }

        return new ItemStack(mat);
    }

    @Override
    public ItemMeta applyMetaValue(ItemMeta item, String value) throws DataException {
        throw new UnsupportedOperationException();
    }
}
