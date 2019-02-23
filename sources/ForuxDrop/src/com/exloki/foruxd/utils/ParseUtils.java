package com.exloki.foruxd.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

public class ParseUtils {
    public static class Alias {
        private String[] aliases;

        public Alias(String... aliases) {
            this.aliases = aliases;
        }

        public String[] getAliases() {
            return aliases;
        }

        public boolean matches(String input) {
            for (String str : aliases) {
                if (input.equalsIgnoreCase(str))
                    return true;
            }
            return false;
        }
    }

    private static Enchantment[] enchantList;
    private static Alias[] enchantAliases;

    static {
        enchantList = new Enchantment[25];
        enchantAliases = new Alias[25];

        enchantList[0] = Enchantment.ARROW_DAMAGE;
        enchantAliases[0] = new Alias("Power", "ARROW_DAMAGE");

        enchantList[1] = Enchantment.ARROW_FIRE;
        enchantAliases[1] = new Alias("Flame", "ARROW_FIRE");

        enchantList[2] = Enchantment.ARROW_INFINITE;
        enchantAliases[2] = new Alias("Infinity", "ARROW_INFINITE");

        enchantList[3] = Enchantment.ARROW_KNOCKBACK;
        enchantAliases[3] = new Alias("Punch", "ARROW_KNOCKBACK");

        enchantList[4] = Enchantment.ARROW_KNOCKBACK;
        enchantAliases[4] = new Alias("Punch", "ARROW_KNOCKBACK");

        enchantList[5] = Enchantment.DAMAGE_ALL;
        enchantAliases[5] = new Alias("Sharpness", "DAMAGE_ALL");

        enchantList[6] = Enchantment.DAMAGE_ARTHROPODS;
        enchantAliases[6] = new Alias("Bane of Arthropods", "baneofarthropods", "DAMAGE_ARTHROPODS");

        enchantList[7] = Enchantment.DAMAGE_UNDEAD;
        enchantAliases[7] = new Alias("Smite", "DAMAGE_UNDEAD");

        enchantList[8] = Enchantment.DIG_SPEED;
        enchantAliases[8] = new Alias("Efficiency", "DIG_SPEED");

        enchantList[9] = Enchantment.DURABILITY;
        enchantAliases[9] = new Alias("Unbreaking", "DURABILITY");

        enchantList[10] = Enchantment.FIRE_ASPECT;
        enchantAliases[10] = new Alias("Fire Aspect", "fireaspect", "FIRE_ASPECT");

        enchantList[11] = Enchantment.KNOCKBACK;
        enchantAliases[11] = new Alias("Knockback", "KNOCKBACK");

        enchantList[12] = Enchantment.LOOT_BONUS_BLOCKS;
        enchantAliases[12] = new Alias("Fortune", "LOOT_BONUS_BLOCKS");

        enchantList[13] = Enchantment.LOOT_BONUS_MOBS;
        enchantAliases[13] = new Alias("Looting", "LOOT_BONUS_MOBS");

        enchantList[14] = Enchantment.OXYGEN;
        enchantAliases[14] = new Alias("Respiration", "OXYGEN");

        enchantList[15] = Enchantment.PROTECTION_ENVIRONMENTAL;
        enchantAliases[15] = new Alias("Protection", "PROTECTION_ENVIRONMENTAL");

        enchantList[16] = Enchantment.PROTECTION_EXPLOSIONS;
        enchantAliases[16] = new Alias("Blast Protection", "blastprotection", "PROTECTION_EXPLOSIONS");

        enchantList[17] = Enchantment.PROTECTION_FALL;
        enchantAliases[17] = new Alias("Feather Falling", "featherfalling", "PROTECTION_FALL");

        enchantList[18] = Enchantment.PROTECTION_FIRE;
        enchantAliases[18] = new Alias("Fire Protection", "fireprotection", "PROTECTION_FIRE");

        enchantList[19] = Enchantment.PROTECTION_PROJECTILE;
        enchantAliases[19] = new Alias("Projectile Protection", "projectileprotection", "PROTECTION_PROJECTILE");

        enchantList[20] = Enchantment.SILK_TOUCH;
        enchantAliases[20] = new Alias("Silk Touch", "silktouch", "SILK_TOUCH");

        enchantList[21] = Enchantment.THORNS;
        enchantAliases[21] = new Alias("Thorns", "THORNS");

        enchantList[22] = Enchantment.WATER_WORKER;
        enchantAliases[22] = new Alias("Aqua Affinity", "aquaaffinity", "WATER_WORKER");

        enchantList[23] = Enchantment.LUCK;
        enchantAliases[23] = new Alias("Luck of The Sea", "luckofthesea", "LUCK");

        enchantList[24] = Enchantment.LURE;
        enchantAliases[24] = new Alias("Lure", "LURE");
    }

    public static Enchantment getEnchantmentByName(String name) {
        if (name.isEmpty()) return null;

        for (int j = 0; j < enchantAliases.length; j++)
            if (enchantAliases[j].matches(name))
                return enchantList[j];
        return null;
    }

    public static String getStringFromEnchantment(Enchantment e) {
        if (e == null) return null;

        for (int j = 0; j < enchantAliases.length; j++)
            if (enchantList[j].equals(e))
                return enchantAliases[j].getAliases()[0];
        return null;
    }

    public static String getStringFromLocation(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static Location getLocationFromString(String input) {
        if(!input.contains(",")) return null;

        String[] split = input.split(",");
        if(split.length != 4) return null;

        World world = Bukkit.getWorld(split[0]);
        if(world == null) return null;

        try {
            return new Location(world, Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
