package com.exloki.foruxe.listeners;

import com.exloki.foruxe.ForuxEffects;
import com.exloki.foruxe.TL;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ForuxEffects.i.removeEffects(player);
        if(!ForuxEffects.getSettings().getDisabledWorlds().contains(player.getWorld().getName())) {
            ForuxEffects.i.applyEffects(player);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ForuxEffects.i.applyEffects(player);
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if(ForuxEffects.getSettings().getDisabledWorlds().contains(player.getWorld().getName())) {
            ForuxEffects.i.removeEffects(player);
        } else {
            ForuxEffects.i.applyEffects(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(item.getType() == Material.POTION) {
            Potion potion = Potion.fromItemStack(item);
            for (PotionEffect potionEffect : potion.getEffects()) {
                if(handle(player, potionEffect)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        for (PotionEffect potionEffect : potion.getEffects()) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if(entity instanceof Player) {
                    Player player = (Player) entity;
                    if(handle(player, potionEffect)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private boolean handle(Player player, PotionEffect effect) {
        if(player.hasPotionEffect(effect.getType())) {
            for (PotionEffect potEffect : player.getActivePotionEffects()) {
                if(potEffect.getType().equals(effect.getType())
                        && potEffect.getDuration() > 9600) {
                    player.sendMessage(TL.CANNOT_CONSUME.toString());
                    return true;
                }
            }
        }

        return false;
    }
}
