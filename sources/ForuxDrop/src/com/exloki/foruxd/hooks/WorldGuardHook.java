package com.exloki.foruxd.hooks;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook {
    public static WorldGuardHook i;
    public static WorldGuardPlugin api;

    static {
        i = new WorldGuardHook();
    }

    public void hook() {
        if (!isHooked() && canHook()) api = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    public void unhook() {
        api = null;
    }

    public boolean canHook() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        return plugin != null && plugin.isEnabled();
    }

    public boolean isHooked() {
        return api != null;
    }

    public static boolean hasRegions(Location location) {
        RegionManager manager = api.getRegionManager(location.getWorld());
        if (manager == null) return false;

        return !manager.getApplicableRegionsIDs(new Vector(location.getX(), location.getY(), location.getZ())).isEmpty();
    }
}