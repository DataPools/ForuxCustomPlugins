package com.exloki.foruxmotd.core.utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class BukkitUtil {

    /*
     * ItemStack related functions
     */

    public static ItemStack newItemStack(Material material) {
        return newItemStack(material, 0);
    }

    public static ItemStack newItemStack(Material material, int durability) {
        return newItemStack(material, durability, 1);
    }

    public static ItemStack newItemStack(Material material, int durability, int amount) {
        return newItemStack(material, durability, amount, "");
    }

    public static ItemStack newItemStack(Material material, int durability, int amount, String displayName) {
        return newItemStack(material, durability, amount, displayName, new String[0]);
    }

    public static ItemStack newItemStack(Material material, int durability, int amount, String displayName, String... lore) {
        ItemStack stack = new ItemStack(material, amount, (short) durability);

        if(!displayName.isEmpty() || lore.length > 0) {
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);

            if(!displayName.isEmpty()) {
                meta.setDisplayName(displayName);
            }
            if(lore.length > 0) {
                meta.setLore(Util.asList(lore));
            }

            stack.setItemMeta(meta);
        }

        return stack;
    }

    public static ItemStack setItemStackName(ItemStack item, String newDisplayname) {
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        meta.setDisplayName(newDisplayname);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setItemStackLore(ItemStack item, String... newLore) {
        ItemMeta meta = item.hasItemMeta() ? item.getItemMeta() : Bukkit.getItemFactory().getItemMeta(item.getType());
        meta.setLore(Util.asList(newLore));
        item.setItemMeta(meta);
        return item;
    }

    public static boolean compareItemStacks(ItemStack item1, ItemStack item2, boolean ignoreAmount) {
        return compareItemStacks(item1, item2, false, false, ignoreAmount, false, false, false);
    }

    public static boolean compareItemStacks(ItemStack item1, ItemStack item2, boolean ignoreMaterial, boolean ignoreDurability, boolean ignoreAmount, boolean ignoreName, boolean ignoreLores, boolean ignoreEnchants) {
        if((!ignoreMaterial && item1.getType() != item2.getType()) ||
                (!ignoreDurability && item1.getDurability() != item2.getDurability()) ||
                (!ignoreAmount && item1.getAmount() != item2.getAmount())) {
            return false;
        }

        if(item1.hasItemMeta() && item2.hasItemMeta()) {
            ItemMeta meta1 = item1.getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();

            if(!ignoreName) {
                if(meta1.hasDisplayName() != meta2.hasDisplayName() ||
                        (meta1.hasDisplayName() && !meta1.getDisplayName().equals(meta2.getDisplayName())))
                    return false;
            }

            if(!ignoreLores) {
                if(meta1.hasLore() != meta2.hasLore() || meta1.getLore().size() != meta2.getLore().size())
                    return false;
                if(meta1.hasLore()) {
                    for(int i = 0; i < meta1.getLore().size(); i++) {
                        if (!meta1.getLore().get(i).equals(meta2.getLore().get(i)))
                            return false;
                    }
                }
            }

            if(!ignoreEnchants) {
                if(meta1.hasEnchants() != meta2.hasEnchants())
                    return false;
                for(Map.Entry<Enchantment, Integer> entry : meta1.getEnchants().entrySet()) {
                    if(!meta2.getEnchants().containsKey(entry.getKey()))
                        return false;
                    if(meta2.getEnchantLevel(entry.getKey()) != entry.getValue())
                        return false;
                }
            }
        }

        return true;
    }

    public static ItemStack[] splitOversizedItemStack(ItemStack in) {
        if(in.getAmount() > in.getMaxStackSize()) {
            int count = in.getAmount() / in.getMaxStackSize();
            int remainder = in.getAmount() % in.getMaxStackSize();

            ItemStack clone;
            ItemStack[] stack = new ItemStack[remainder > 0 ? count+1 : count];
            for(int k = 0; k < count; k++) {
                clone = in.clone();
                clone.setAmount(clone.getMaxStackSize());
                stack[k] = clone;
            }

            if(remainder > 0) {
                clone = in.clone();
                clone.setAmount(remainder);
                stack[count] = clone;
            }

            return stack;
        }

        return new ItemStack[] { in };
    }

    private static final String VER_STRING = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
    private static Class<?> CB_CRAFTITEMSTACK_CLASS = null;
    private static Class<?> NMS_NBTTAGCOMPOUND_CLASS = null;
    private static Class<?> NMS_NBTTAGLIST_CLASS = null;

    private static Method CB_CRAFTITEMSTACK_METHOD_NMSCOPY = null;
    private static Method NMS_ITEMSTACK_METHOD_HASTAG = null;
    private static Method NMS_ITEMSTACK_METHOD_GETTAG = null;
    private static Method NMS_ITEMSTACK_METHOD_SETTAG = null;
    private static Method NMS_NBTTAGCOMPOUND_METHOD_SET = null;
    private static Method CB_CRAFTITEMSTACK_METHOD_ASCRAFT = null;
    static {
        try {
            Class<?> NMS_ITEMSTACK_CLASS = Class.forName("net.minecraft.server." + VER_STRING + ".ItemStack");
            CB_CRAFTITEMSTACK_CLASS = Class.forName("org.bukkit.craftbukkit." + VER_STRING + ".inventory.CraftItemStack");
            NMS_NBTTAGCOMPOUND_CLASS = Class.forName("net.minecraft.server." + VER_STRING + ".NBTTagCompound");
            NMS_NBTTAGLIST_CLASS = Class.forName("net.minecraft.server." + VER_STRING + ".NBTTagList");
            Class<?> NMS_NBTBASE_CLASS = Class.forName("net.minecraft.server." + VER_STRING + ".NBTBase");

            CB_CRAFTITEMSTACK_METHOD_NMSCOPY = CB_CRAFTITEMSTACK_CLASS.getDeclaredMethod("asNMSCopy", ItemStack.class);
            NMS_ITEMSTACK_METHOD_HASTAG = NMS_ITEMSTACK_CLASS.getDeclaredMethod("hasTag");
            NMS_ITEMSTACK_METHOD_GETTAG = NMS_ITEMSTACK_CLASS.getDeclaredMethod("getTag");
            NMS_ITEMSTACK_METHOD_SETTAG = NMS_ITEMSTACK_CLASS.getDeclaredMethod("setTag", NMS_NBTTAGCOMPOUND_CLASS);
            NMS_NBTTAGCOMPOUND_METHOD_SET = NMS_NBTTAGCOMPOUND_CLASS.getDeclaredMethod("set", String.class, NMS_NBTBASE_CLASS);
            CB_CRAFTITEMSTACK_METHOD_ASCRAFT = CB_CRAFTITEMSTACK_CLASS.getDeclaredMethod("asCraftMirror", NMS_ITEMSTACK_CLASS);
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            Bukkit.getLogger().severe("Unable to determine NMS version - item glow will not be supported!");
        }
    }

    public static ItemStack addGlow(ItemStack item) {
        if(CB_CRAFTITEMSTACK_METHOD_ASCRAFT == null) {
            Bukkit.getLogger().severe("Unable to determine NMS version - item glow is not be supported!");
            return item;
        }

        try {
            Object nmsStack = CB_CRAFTITEMSTACK_METHOD_NMSCOPY.invoke(CB_CRAFTITEMSTACK_CLASS, item);
            Object tag = null;
            if(!(Boolean)NMS_ITEMSTACK_METHOD_HASTAG.invoke(nmsStack)) {
                tag = NMS_NBTTAGCOMPOUND_CLASS.newInstance();
                NMS_ITEMSTACK_METHOD_SETTAG.invoke(nmsStack, tag);
            }
            if(tag == null) tag = NMS_ITEMSTACK_METHOD_GETTAG.invoke(nmsStack);
            Object tagList = NMS_NBTTAGLIST_CLASS.newInstance();
            NMS_NBTTAGCOMPOUND_METHOD_SET.invoke(tag, "ench", tagList);
            NMS_ITEMSTACK_METHOD_SETTAG.invoke(nmsStack, tag);
            return (ItemStack) CB_CRAFTITEMSTACK_METHOD_ASCRAFT.invoke(CB_CRAFTITEMSTACK_CLASS, nmsStack);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | IllegalArgumentException ex) {
            Bukkit.getLogger().severe("Unknown NMS version encountered - item glow is not be supported!");
            return item;
        }
    }

    /*
     * Player related functions
     */

    public static int givePlayerItems(Player player, boolean doOverflow, ItemStack... items) {
        int k = 0;
        items: for(ItemStack item : items) {
            if(item == null) continue;
            if(item.getAmount() > item.getMaxStackSize()) {
                k = k+givePlayerItems(player, doOverflow, splitOversizedItemStack(item));
                continue;
            }

            if(player.getInventory().firstEmpty() < 0) {
                // Check for existing stacks
                if(player.getInventory().contains(item.getType())) {
                    for(int l = 0; l < player.getInventory().getSize(); l++) {
                        ItemStack invItem = player.getInventory().getItem(l);
                        if(invItem == null) continue;

                        if(invItem.getType().equals(item.getType()) && invItem.getDurability() == item.getDurability() && invItem.getAmount() < item.getMaxStackSize()) {
                            if(invItem.hasItemMeta() || item.hasItemMeta()) {
                                // Meta comparison
                                if(!compareItemStacks(invItem, item, true))
                                    continue;
                            }

                            if(invItem.getAmount() + item.getAmount() <= item.getMaxStackSize()) {
                                // No need to overflow
                                ItemStack clone = invItem.clone();
                                clone.setAmount(invItem.getAmount() + item.getAmount());
                                player.getInventory().setItem(l, clone);
                                k++;
                                continue items;
                            }

                            int leftover = item.getMaxStackSize() - (invItem.getAmount() + item.getAmount());
                            ItemStack clone = invItem.clone();
                            clone.setAmount(invItem.getMaxStackSize());
                            player.getInventory().setItem(l, clone);
                            item.setAmount(leftover);
                            k++;
                            break;
                        }
                    }
                }

                if(doOverflow) {
                    Item spawned = player.getWorld().dropItem(player.getLocation(), item);
                    spawned.setPickupDelay(15);
                    k++;
                }

            } else {
                player.getInventory().addItem(item);
                k++;
            }
        }

        return k;
    }

    public static Player getPlayer(final String searchTerm) {
        return getPlayer(Bukkit.getServer(), searchTerm);
    }

    public static Player getPlayer(final Server server, final String searchTerm) {
        if(searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }

        Player exPlayer;
        try {
            exPlayer = server.getPlayer(UUID.fromString(searchTerm));
        } catch (IllegalArgumentException ex) {
            exPlayer = server.getPlayer(searchTerm);
        }

        if (exPlayer != null)
            return exPlayer;

        final List<Player> matches = server.matchPlayer(searchTerm);

        if (matches.isEmpty()) {
            final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
            for (Player player : Bukkit.getOnlinePlayers()) {
                final String displayName = Txt.stripColor(player.getName()).toLowerCase(Locale.ENGLISH);
                if (displayName.contains(matchText)) {
                    return player;
                }
            }
        } else {
            for (Player player : matches) {
                if (player.getDisplayName().startsWith(searchTerm)) {
                    return player;
                }
            }

            return matches.get(0);
        }

        return null;
    }

    public static OfflinePlayer getOfflinePlayer(final String searchTerm, final boolean deep) {
        return getOfflinePlayer(Bukkit.getServer(), searchTerm, deep);
    }

    public static OfflinePlayer getOfflinePlayer(final Server server, final String searchTerm, final boolean deep) {
        if(searchTerm == null || searchTerm.isEmpty()) {
            return null;
        }

        OfflinePlayer exPlayer;
        Player onlinePlayer = getPlayer(server, searchTerm);
        if(onlinePlayer != null) {
            return onlinePlayer;
        }

        try {
            exPlayer = server.getOfflinePlayer(UUID.fromString(searchTerm));
        } catch (IllegalArgumentException ex) {
            exPlayer = server.getOfflinePlayer(searchTerm);
        }

        if (exPlayer != null) return exPlayer;
        if (!deep) return null;

        final OfflinePlayer[] offlinePlayers = server.getOfflinePlayers();

        final String matchText = searchTerm.toLowerCase(Locale.ENGLISH);
        for (OfflinePlayer player : offlinePlayers) {
            if (player.getName().toLowerCase(Locale.ENGLISH).contains(matchText)) {
                return player;
            }
        }

        return null;
    }

    /*
     * Location related functions
     */

    public static String getStringFromLocation(Location location, boolean accurate, boolean detailed) {
        StringBuilder builder = new StringBuilder();

        builder
                .append(location.getWorld().getName())
                .append(",")
                .append(accurate ? location.getX() : location.getBlockX())
                .append(",")
                .append(accurate ? location.getY() : location.getBlockY())
                .append(",")
                .append(accurate ? location.getZ() : location.getBlockZ());

        if(detailed) {
            builder
                    .append(",")
                    .append(location.getYaw())
                    .append(",")
                    .append(location.getPitch());
        }

        return builder.toString().trim();
    }

    public static Location getLocationFromString(String string, boolean detailed) {
        String[] split = string.split(",");

        try {
            World world = Bukkit.getWorld(split[0]);
            if(world == null)
                return null;

            return detailed ?
                    new Location(
                            world,
                            Double.parseDouble(split[1]),
                            Double.parseDouble(split[2]),
                            Double.parseDouble(split[3]),
                            Float.parseFloat(split[4]),
                            Float.parseFloat(split[5])
                    ) :
                    new Location(
                            world,
                            Double.parseDouble(split[1]),
                            Double.parseDouble(split[2]),
                            Double.parseDouble(split[3])
                    );
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return null;
        }
    }

    /*
     * Vector related functions
     */

    public static Vector getDirectionVector(Location origin, Location target) {
        return target.toVector().subtract(origin.toVector()).normalize();
    }
}
