package com.exloki.balancedfly;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FlightManager implements Listener {
    private BalancedFlyMain plugin;
    private Map<UUID, WarmupData> warmupMap = new HashMap<>();
    private Cache<UUID, Long> fallCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    public FlightManager(BalancedFlyMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage();

        if(player.hasPermission("balancedfly.bypass")) return;
        if(message.equalsIgnoreCase("/fly") || message.toLowerCase().startsWith("/fly ")) {
            if(!player.getAllowFlight() && player.hasPermission("essentials.fly")) {
                // Toggling on
                event.setCancelled(true);
                event.setMessage("/unknownCommand");

                // Deny if in certain WG regions
                ApplicableRegionSet applicableRegions = WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
                for (ProtectedRegion region : applicableRegions) {
                    if (plugin.getSettings().getRegionNames().contains(region.getId())) {
                        player.sendMessage(Msg.FLIGHT_DISALLOWED.get());
                        return;
                    }
                }

                // Deny if in certain worlds
                if(plugin.getSettings().getWorldNames().contains(player.getWorld().getName())) {
                    player.sendMessage(Msg.FLIGHT_DISALLOWED.get());
                    return;
                }

                warmupMap.put(player.getUniqueId(), new WarmupData(new BukkitRunnable() {
                    public void run() {
                        Bukkit.dispatchCommand(player, "fly"); // Doesn't trigger preprocess event
                        warmupMap.remove(player.getUniqueId());
                    }
                }.runTaskLater(plugin, 20 * plugin.getSettings().getWarmupDuration()), player.getLocation()));
                player.sendMessage(Msg.WARMUP_STARTING.get());
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        WarmupData warmupData = warmupMap.get(player.getUniqueId());

        if(warmupData != null) {
            if(event.getTo().distance(warmupData.getOrigin()) >= 1.5) {
                player.sendMessage(Msg.WARMUP_INTERRUPTED.get());
                warmupData.getTask().cancel();
                warmupMap.remove(player.getUniqueId());
            }
        }

        if(player.getAllowFlight() && !player.hasPermission("balancedfly.bypass")) {
            ApplicableRegionSet applicableRegions = WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegions(event.getTo());
            for (ProtectedRegion region : applicableRegions) {
                if(plugin.getSettings().getRegionNames().contains(region.getId())) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(Msg.FLIGHT_DISALLOWED.get());
                    fallCache.put(player.getUniqueId(), System.currentTimeMillis());
                    break;
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            WarmupData warmupData = warmupMap.get(player.getUniqueId());

            if(warmupData != null) {
                player.sendMessage(Msg.WARMUP_INTERRUPTED.get());
                warmupData.getTask().cancel();
                warmupMap.remove(player.getUniqueId());
            }

            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && fallCache.asMap().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                fallCache.invalidate(player.getUniqueId());
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if(player.getAllowFlight() && !player.hasPermission("balancedfly.bypass") && plugin.getSettings().getWorldNames().contains(player.getWorld().getName())) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @Getter
    @AllArgsConstructor
    private class WarmupData {
        private BukkitTask task;
        private Location origin;
    }
}
