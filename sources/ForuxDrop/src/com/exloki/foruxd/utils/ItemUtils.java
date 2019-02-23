package com.exloki.foruxd.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class ItemUtils {
    public static boolean compare(ItemStack item1, ItemStack item2, boolean ignoreMaterial, boolean ignoreDurability, boolean ignoreAmount, boolean ignoreName, boolean ignoreLores, boolean ignoreEnchants) {
        if(item1 == null || item2 == null) return false;

        if (!ignoreMaterial)
            if (item1.getType() != item2.getType()) return false;

        if (!ignoreDurability)
            if (item1.getDurability() != item2.getDurability()) return false;

        if (!ignoreAmount)
            if (item1.getAmount() != item2.getAmount()) return false;

        if (item1.getItemMeta() != null && item2.getItemMeta() != null) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            if (!ignoreName) {
                if (meta1.hasDisplayName() != meta2.hasDisplayName()) return false;
                if (meta1.hasDisplayName())
                    if (!meta1.getDisplayName().equals(meta2.getDisplayName())) return false;
            }

            if (!ignoreLores) {
                if (meta1.hasLore() != meta2.hasLore()) return false;
                if (meta1.hasLore()) {
                    for (int i = 0; i < meta1.getLore().size(); i++)
                        if (!meta1.getLore().get(i).equals(meta2.getLore().get(i))) return false;
                }
            }

            if (!ignoreEnchants) {
                if (meta1.hasEnchants() != meta2.hasEnchants()) return false;
                for (Map.Entry<Enchantment, Integer> entry : meta1.getEnchants().entrySet()) {
                    if (!meta2.getEnchants().containsKey(entry.getKey()))
                        return false;
                    if (meta2.getEnchantLevel(entry.getKey()) != entry.getValue())
                        return false;
                }
            }
        }

        return true;
    }

    public static ItemStack[] splitOversizedStack(ItemStack in)
    {
        if(in.getAmount() > in.getMaxStackSize())
        {
            int count = in.getAmount() / in.getMaxStackSize();
            int remainder = in.getAmount() % in.getMaxStackSize();

            ItemStack clone;
            ItemStack[] stack = new ItemStack[remainder > 0 ? count+1 : count];
            for(int k = 0; k < count; k++)
            {
                clone = in.clone();
                clone.setAmount(clone.getMaxStackSize());
                stack[k] = clone;
            }

            if(remainder > 0)
            {
                clone = in.clone();
                clone.setAmount(remainder);
                stack[count] = clone;
            }

            return stack;
        }

        return new ItemStack[] { in };
    }
}
