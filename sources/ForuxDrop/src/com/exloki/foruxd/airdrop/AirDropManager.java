package com.exloki.foruxd.airdrop;

import com.exloki.core_foruxd.utils.Txt;
import com.exloki.foruxd.ForuxDrop;
import com.exloki.foruxd.TL;
import com.exloki.foruxd.hooks.FactionsHook;
import com.exloki.foruxd.hooks.WorldGuardHook;
import com.exloki.foruxd.utils.LTask;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AirDropManager {

    private List<AirDropPackage> packages;

    public AirDropManager() {
        reload();
    }

    public void reload() {
        packages = ForuxDrop.getSettings().loadAirDropPackages();
        stopRandomAirDropTask();
        startRandomAirDropTask();
    }

    public AirDropPackage getPackage(String name) {
        for(AirDropPackage pack : packages) {
            if(pack.getName().equalsIgnoreCase(name)) {
                return pack;
            }
        }
        return null;
    }

    public List<AirDropPackage> getPackages() {
        return new ArrayList<>(packages);
    }

    public AirDropPackage getRandomPackage() {
        return packages.get(RandomUtils.nextInt(0, packages.size()));
    }

    public boolean callAirDrop(Player caller, Location target, AirDropPackage package_) {
        // Ensure target is not above 127
        if (target.getBlockY() > 127) {
            if (caller != null) {
                caller.sendMessage(TL.ER_DROP_TOO_HIGH.toString());
            }
            return false;
        }

        // Ensure target is on the ground
        if(target.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            if (caller != null) {
                caller.sendMessage(TL.ER_DROP_NOT_GROUNDED.toString());
            }
            return false;
        }

        // Determine creation point
        Location createPoint = target.clone();
        createPoint.setY(128);

        // Ensure not in Factions land
        if (caller != null) {
            if(FactionsHook.isInFactionTerritory(target)) {
                caller.sendMessage(TL.ER_DROP_IN_FACTION_LAND.toString());
                return false;
            }
        }

        // Ensure not in WorldGuard region
        if(WorldGuardHook.hasRegions(target)) {
            if(caller != null) {
                caller.sendMessage(TL.ER_DROP_IN_WORLD_GUARD.toString());
            }
            return false;
        }

        // Ensure between target & spawn point is clear
        if(!isClearBetween(createPoint, target)) {
            if(caller != null) {
                caller.sendMessage(TL.ER_DROP_AREA_NOT_CLEAR.toString());
            }
            return false;
        }

        AirDrop airdrop = new AirDrop(createPoint, target, package_);
        airdrop.create();
        airdrop.descendToTarget();
        return true;
    }

    private boolean isClearBetween(Location top, Location bottom) {
        Location pointer;
        for(int e = 0; e < (top.getBlockY() - bottom.getBlockY()); e++) {
            for(int x = -1; x < 2; x++) {
                for(int z = -1; z < 2; z++) {
                    pointer = new Location(top.getWorld(), top.getBlockX()+x, top.getBlockY()-e, top.getBlockZ()+z);
                    if(pointer.getBlock().getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // RANDOM AIR DROPS

    private LTask airDropTask;

    public void startRandomAirDropTask() {
        stopRandomAirDropTask();

        airDropTask = new LTask(ForuxDrop.i, ForuxDrop.getSettings().getDropInteveral() * 20, ForuxDrop.getSettings().getDropInteveral() * 20) {
            public void run() {
                callRandomAirDrop(0);
            }
        };
    }

    public void callRandomAirDrop(int attempts) {
        Location dropLoc = generateLocation();
        if(dropLoc == null || attempts >= 50) {
            ForuxDrop.i.log("Unable to find suitable location for random air drop! Skipping....");
            return;
        }

        if(callAirDrop(null, dropLoc, getRandomPackage())) {
            Bukkit.broadcastMessage(format(ForuxDrop.getSettings().getBroadcastMessage(), dropLoc));
        } else {
            callRandomAirDrop(++attempts); // Retry
        }
    }

    private String format(String message, Location dropLocation) {
        return Txt.parseColor(message.replaceAll("<world>", dropLocation.getWorld().getName())
                .replaceAll("<x>", dropLocation.getBlockX()+"")
                .replaceAll("<y>", dropLocation.getBlockY()+"")
                .replaceAll("<z>", dropLocation.getBlockZ()+""));
    }

    private Location generateLocation() {
        int k = 0;
        Location location = null;
        while(k++ < 1000 && location == null) {
            location = getRandomLocation();
            if(!isLocationValid(location)) {
                location = null;
            }
        }

        return location;
    }

    private boolean isLocationValid(Location location) {
        Location clone = location.clone();
        if(clone.getBlockY() > 128) return false;
        if(isClearBetween(clone.add(0,256-clone.getY(),0), clone)) {
            if(!WorldGuardHook.hasRegions(clone)) {
                return true;
            }
        }

        return false;
    }

    private Location getRandomLocation() {
        List<String> worldNames = ForuxDrop.getSettings().getWorldNames();
        String wName = worldNames.size() > 1 ? worldNames.get(RandomUtils.nextInt(0, worldNames.size())) : worldNames.get(0);
        World world = Bukkit.getWorld(wName);

        int minX = ForuxDrop.getSettings().getMinXDrop();
        int maxX = ForuxDrop.getSettings().getMaxXDrop();
        int minZ = ForuxDrop.getSettings().getMinZDrop();
        int maxZ = ForuxDrop.getSettings().getMaxZDrop();

        int boundX = Math.abs(minX - maxX);
        int boundZ = Math.abs(minZ - maxZ);

        Location loc =  new Location(world, minX + RandomUtils.nextInt(0, boundX+1), 100, minZ + RandomUtils.nextInt(0, boundZ+1));
        Block highest = world.getHighestBlockAt(loc);
        return highest.getLocation();
    }

    public void stopRandomAirDropTask() {
        if(airDropTask != null) {
            airDropTask.cancel();
        }
    }
}
