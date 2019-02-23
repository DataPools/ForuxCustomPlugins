package com.exloki.foruxd.utils;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtils {
    public static int givePlayerItems(Player player, boolean doOverflow, ItemStack... items)
    {
        int k = 0;
        items: for(ItemStack item : items)
        {
            if(item == null) continue;

            if(item.getAmount() > item.getMaxStackSize())
            {
                k = k+givePlayerItems(player, doOverflow, ItemUtils.splitOversizedStack(item));
                continue;
            }

            if(player.getInventory().firstEmpty() < 0)
            {
                // Check for existing stacks
                if(player.getInventory().contains(item.getType()))
                {
                    inv: for(int l = 0; l < player.getInventory().getSize(); l++)
                    {
                        ItemStack invItem = player.getInventory().getItem(l);
                        if(invItem == null) continue inv;

                        if(invItem.getType().equals(item.getType()) && invItem.getDurability() == item.getDurability() && invItem.getAmount() < item.getMaxStackSize())
                        {
                            if(invItem.hasItemMeta() || item.hasItemMeta())
                            {
                                // Meta comparison
                                if(!ItemUtils.compare(invItem, item, false, false, true, false, false, false)) continue;
                            }

                            if(invItem.getAmount() + item.getAmount() <= item.getMaxStackSize())
                            {
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
                            break inv;
                        }
                    }
                }

                if(doOverflow)
                {
                    Item spawned = player.getWorld().dropItem(player.getLocation(), item);
                    spawned.setPickupDelay(15);
                    k++;
                }

            }
            else
            {
                player.getInventory().addItem(item);
                k++;
            }
        }

        return k;
    }
}
