package com.exloki.forux.ecorewards.core.utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class BukkitUtil {

    /*
     * ItemStack related functions
     */

    public static ItemStack createItemStack(Material material) {
        return createItemStack(material, 0);
    }

    public static ItemStack createItemStack(Material material, int durability) {
        return createItemStack(material, durability, 1);
    }

    public static ItemStack createItemStack(Material material, int durability, int amount) {
        return createItemStack(material, durability, amount, "");
    }

    public static ItemStack createItemStack(Material material, int durability, int amount, String displayName) {
        return createItemStack(material, durability, amount, displayName, new String[0]);
    }

    public static ItemStack createItemStack(Material material, int durability, int amount, String displayName, String... lore) {
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

    public static ItemStack[] splitOversizedItemStack(ItemStack in)
    {
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

    /*
     * Player related functions
     */

    public static int givePlayerItems(Player player, boolean doOverflow, ItemStack... items)
    {
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
}
