package com.exloki.foruxd.airdrop;

import com.exloki.foruxd.ForuxDrop;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AirDrop {

    private String world;

    private int currentX;
    private int currentY;
    private int currentZ;

    private Location target;

    private AirDropPackage drops;

    public AirDrop(Location createPoint, Location target, AirDropPackage drops) {
        this.target = target;
        this.world = createPoint.getWorld().getName();
        this.currentX = createPoint.getBlockX();
        this.currentY = createPoint.getBlockY();
        this.currentZ = createPoint.getBlockZ();
        this.drops = drops;
    }

    public void create() {
        build();
    }

    private void build() {
        Location pointer = new Location(Bukkit.getWorld(world), currentX, currentY, currentZ);
        pointer.getBlock().setType(Material.CHEST);
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    pointer = new Location(Bukkit.getWorld(world), currentX + x, currentY + y, currentZ + z);
                    if (pointer.getBlock().getType() != Material.AIR) continue;
                    pointer.getBlock().setType(Material.IRON_FENCE);
                }
            }
        }
    }

    private void destroy() {
        Location pointer;
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    pointer = new Location(Bukkit.getWorld(world), currentX + x, currentY + y, currentZ + z);
                    pointer.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    private BukkitTask descendTask;

    public void descendToTarget() {
        descendTask = new BukkitRunnable() {
            public void run() {
                if (currentY == target.getBlockY()) {
                    halt();
                    fillInventory();
                    return;
                }

                moveDown(1);
                flashTarget();
            }
        }.runTaskTimer(ForuxDrop.i, 2, 2);
    }

    private void moveDown(int amount) {
        destroy();
        currentY -= amount;
        build();
    }

    private void flashTarget() {
        target.getWorld().playEffect(target, Effect.SMOKE, 0);
    }

    public void halt() {
        descendTask.cancel();
    }

    private void fillInventory() {
        Block block = new Location(Bukkit.getWorld(world), currentX, currentY, currentZ).getBlock();
        Chest chest = (Chest) block.getState();

        for(ItemStack stack : drops.getItems()) {
            chest.getInventory().addItem(stack);
        }
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getCurrentZ() {
        return currentZ;
    }

    public void setCurrentZ(int currentZ) {
        this.currentZ = currentZ;
    }

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        this.target = target;
    }
}
