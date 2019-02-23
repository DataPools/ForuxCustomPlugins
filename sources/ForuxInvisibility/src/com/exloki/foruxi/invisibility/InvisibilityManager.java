package com.exloki.foruxi.invisibility;

import com.exloki.foruxi.Msg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.UUID;

public class InvisibilityManager implements Listener {

    private HashSet<UUID> invisiblePlayers;

    public InvisibilityManager() {
        invisiblePlayers = new HashSet<>();
    }

    public void activate(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        invisiblePlayers.add(player.getUniqueId());
    }

    public boolean isActivated(Player player) {
        return invisiblePlayers.contains(player.getUniqueId());
    }

    public void deactivate(Player player, boolean cooldown, boolean verbose) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        invisiblePlayers.remove(player.getUniqueId());

        if(verbose) {
            player.sendMessage(Msg.INVISIBILITY_ABORTED.toString());
        }

        if(cooldown && !player.hasPermission("foruxinvisibility.bypass")) {
            CooldownManager.cooldown(player);
        }
    }

    public void deactivateAll() {
        for(UUID id : invisiblePlayers) {
            Player player = Bukkit.getPlayer(id);
            deactivate(player, false, true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, false, true);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, false, true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, true, true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, true, true);
        }
    }


    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

            Player player = (Player) event.getDamager();

            if(invisiblePlayers.contains(player.getUniqueId())) {
                deactivate(player, true, true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, true, true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, true, true);
        }
    }

    @EventHandler
    public void onEnderpearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && invisiblePlayers.contains(player.getUniqueId())) {
            deactivate(player, true, true);
        }
    }

    private static final EnumSet<Material> INTERACTABLES = EnumSet.of(Material.LEVER, Material.STONE_BUTTON, Material.WOOD_BUTTON,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR);

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && INTERACTABLES.contains(event.getClickedBlock().getType())) {
            if(invisiblePlayers.contains(player.getUniqueId())) {
                deactivate(player, true, true);
            }
        }
    }
}
