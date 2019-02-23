package com.exloki.foruxd.itemdata;

import com.exloki.foruxd.utils.ParseUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemEnchants extends ItemData implements IItemData {
    private static ItemEnchants i = new ItemEnchants();

    public static ItemEnchants get() {
        return i;
    }

    private static final Pattern PATTERN = Pattern.compile("^([a-zA-Z_]+)([.](\\d+))*$");
    private static Matcher MATCHER = null;

    public ItemEnchants() {
        super(ItemPart.ENCHANTS, new String[]{"en.", "enchant.", "enchantment.", "ench."});
        setApplyToMeta(true);
    }

    @Override
    public ItemStack applyValue(ItemStack item, String value) throws DataException {
        if (item == null) return null;

        ItemMeta meta = applyMetaValue(item.getItemMeta(), value);
        if (meta == null)
            throw new DataException("Unable to retrieve ItemStack metadata from type '" + item.getType().name() + "'");

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ItemMeta applyMetaValue(ItemMeta meta, String value) throws DataException {
        if (meta == null) return null;

        MATCHER = PATTERN.matcher(value);
        if (MATCHER.matches()) {
            String enchantmentName = MATCHER.group(1);
            Enchantment enchant = ParseUtils.getEnchantmentByName(enchantmentName);
            if (enchant == null)
                throw new DataException("'" + enchantmentName + "' is not a valid ItemStack enchantment name");

            int level = 1;
            if (MATCHER.groupCount() > 1 && MATCHER.group(3) != null)
                level = Integer.parseInt(MATCHER.group(3));

            meta.addEnchant(enchant, level, true);
            return meta;
        }

        throw new DataException("Unable to match enchantment pattern with '" + value + "' - Must take the format en.<enchant>[.<level>]");
    }
}
