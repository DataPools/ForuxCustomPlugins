package com.exloki.forux.ecorewards.itemdata;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemAmount extends ItemData implements IItemData {
    private static ItemAmount i = new ItemAmount();

    public static ItemAmount get() {
        return i;
    }

    public ItemAmount() {
        super(ItemPart.AMOUNT, new String[]{"am.", "amount.", "count."});
    }

    @Override
    public ItemStack applyValue(ItemStack item, String value) throws DataException {
        if (item == null) return null;

        int amount = 0;
        try {
            amount = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new DataException(amount + " is not a valid ItemStack amount (integer)!");
        }

        if (amount < 1)
            throw new DataException(amount + " is not a valid ItemStack amount (must be >= 1)");

        item.setAmount(amount);
        return item;
    }

    @Override
    public ItemMeta applyMetaValue(ItemMeta item, String value) throws DataException {
        throw new UnsupportedOperationException();
    }
}
