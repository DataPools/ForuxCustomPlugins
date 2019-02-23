package com.exloki.foruxd.itemdata;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemDamage extends ItemData implements IItemData {
    private static ItemDamage i = new ItemDamage();

    public static ItemDamage get() {
        return i;
    }

    public ItemDamage() {
        super(ItemPart.DAMAGE, new String[]{"du.", "durability.", "damage.", "dura."});
    }

    @Override
    public ItemStack applyValue(ItemStack item, String value) throws DataException {
        if (item == null) return null;

        short damage = 0;
        try {
            damage = (short) Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new DataException(damage + " is not a valid ItemStack durability (integer)!");
        }

        if (damage < 0)
            throw new DataException(damage + " is not a valid ItemStack durability (must be >= 0)");

        item.setDurability(damage);
        return item;
    }

    @Override
    public ItemMeta applyMetaValue(ItemMeta item, String value) throws DataException {
        throw new UnsupportedOperationException();
    }
}
