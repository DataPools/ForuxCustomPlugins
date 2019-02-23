package com.exloki.forux.ecorewards.itemdata;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantUtil {

    private static final HashMap<Enchantment, NameSet> ENCHANTMENT_NAME_MAP = new HashMap<>();
    static {
        ENCHANTMENT_NAME_MAP.put(Enchantment.ARROW_DAMAGE, new NameSet("Power", Enchantment.ARROW_DAMAGE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.ARROW_FIRE, new NameSet("Flame", Enchantment.ARROW_FIRE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.ARROW_INFINITE, new NameSet("Infinity", Enchantment.ARROW_INFINITE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.ARROW_KNOCKBACK, new NameSet("Punch", Enchantment.ARROW_KNOCKBACK));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DAMAGE_ALL, new NameSet("Sharpness", Enchantment.DAMAGE_ALL));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DAMAGE_ARTHROPODS, new NameSet("Bane of Arthropods", Enchantment.DAMAGE_ARTHROPODS));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DAMAGE_UNDEAD, new NameSet("Smite", Enchantment.DAMAGE_UNDEAD));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DEPTH_STRIDER, new NameSet("Depth Strider", Enchantment.DEPTH_STRIDER));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DIG_SPEED, new NameSet("Haste", Enchantment.DIG_SPEED));
        ENCHANTMENT_NAME_MAP.put(Enchantment.DURABILITY, new NameSet("Unbreaking", Enchantment.DURABILITY));
        ENCHANTMENT_NAME_MAP.put(Enchantment.FIRE_ASPECT, new NameSet("Fire Aspect", Enchantment.FIRE_ASPECT));
        ENCHANTMENT_NAME_MAP.put(Enchantment.KNOCKBACK, new NameSet("Knockback", Enchantment.KNOCKBACK));
        ENCHANTMENT_NAME_MAP.put(Enchantment.LOOT_BONUS_BLOCKS, new NameSet("Fortune", Enchantment.LOOT_BONUS_BLOCKS));
        ENCHANTMENT_NAME_MAP.put(Enchantment.LOOT_BONUS_MOBS, new NameSet("Looting", Enchantment.LOOT_BONUS_MOBS));
        ENCHANTMENT_NAME_MAP.put(Enchantment.LUCK, new NameSet("Luck of The Sea", Enchantment.LUCK));
        ENCHANTMENT_NAME_MAP.put(Enchantment.LURE, new NameSet("Lure", Enchantment.LURE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.OXYGEN, new NameSet("Respiration", Enchantment.OXYGEN));
        ENCHANTMENT_NAME_MAP.put(Enchantment.PROTECTION_ENVIRONMENTAL, new NameSet("Protection", Enchantment.PROTECTION_ENVIRONMENTAL));
        ENCHANTMENT_NAME_MAP.put(Enchantment.PROTECTION_EXPLOSIONS, new NameSet("Blast Protection", Enchantment.PROTECTION_EXPLOSIONS));
        ENCHANTMENT_NAME_MAP.put(Enchantment.PROTECTION_FALL, new NameSet("Feather Falling", Enchantment.PROTECTION_FALL));
        ENCHANTMENT_NAME_MAP.put(Enchantment.PROTECTION_FIRE, new NameSet("Fire Protection", Enchantment.PROTECTION_FIRE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.PROTECTION_PROJECTILE, new NameSet("Projectile Protection", Enchantment.PROTECTION_PROJECTILE));
        ENCHANTMENT_NAME_MAP.put(Enchantment.SILK_TOUCH, new NameSet("Silk Touch", Enchantment.SILK_TOUCH));
        ENCHANTMENT_NAME_MAP.put(Enchantment.THORNS, new NameSet("Thorns", Enchantment.THORNS));
        ENCHANTMENT_NAME_MAP.put(Enchantment.WATER_WORKER, new NameSet("Aqua Affinity", Enchantment.WATER_WORKER));
    }

    public static Enchantment getEnchantmentByName(String input) {
        for(Map.Entry<Enchantment, NameSet> entry : ENCHANTMENT_NAME_MAP.entrySet()) {
            if(entry.getValue().matches(input)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static String getEnchantmentName(Enchantment enchantment) {
        for(Map.Entry<Enchantment, NameSet> entry : ENCHANTMENT_NAME_MAP.entrySet()) {
            if(entry.getKey().equals(enchantment)) {
                return entry.getValue().getPrimaryName();
            }
        }

        return null;
    }

    @Getter
    public static class NameSet {
        private String primaryName;
        private String[] alternatives;

        public NameSet(String primaryName) {
            this.primaryName = primaryName;
            this.alternatives = new String[0];
        }

        public NameSet(String primaryName, String... alternatives) {
            this.primaryName = primaryName;
            this.alternatives = alternatives;
        }

        public NameSet(String primaryName, Enchantment base) {
            this.primaryName = primaryName;

            if(this.primaryName.contains(" ")) {
                this.alternatives = new String[] { base.toString(), primaryName.replaceAll(" ", "") };
            } else {
                this.alternatives = new String[] { base.toString() };
            }
        }

        public boolean matches(String input) {
            if(primaryName.equalsIgnoreCase(input)) {
                return true;
            }

            for(String str : alternatives) {
                if(input.equalsIgnoreCase(str)) {
                    return true;
                }
            }

            return false;
        }
    }
}
