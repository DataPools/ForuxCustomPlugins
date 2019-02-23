package net.richardsprojects.virtualfurnace;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateFurnaceTask extends BukkitRunnable {

	private VirtualFurnace plugin;
	private Player player;
	
	public UpdateFurnaceTask(VirtualFurnace plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	public void run() {
		player = plugin.getServer().getPlayer(player.getName());
		
		if(player != null) {
			if(player.getOpenInventory() != null) {
				if(player.getOpenInventory().getTopInventory() != null) {
					Inventory furnace = player.getOpenInventory().getTopInventory();
					if(furnace.getType() == InventoryType.FURNACE) {
						if(VirtualFurnace.getFuel(furnace) != null && VirtualFurnace.getSmelting(furnace) != null) {
							FurnaceRecipe applicableFurnRec = null;
							
							Iterator<Recipe> recipes = plugin.getServer().recipeIterator();
							ItemStack input = VirtualFurnace.getSmelting(furnace);
					        while (recipes.hasNext()) {
					            Recipe rec = recipes.next();
					            if(rec instanceof FurnaceRecipe) {
					            	FurnaceRecipe furnRec = (FurnaceRecipe) rec;
					            	if(furnRec.getInput().getType() == input.getType()) applicableFurnRec = furnRec;
					            }
					        }
					 
							
							if(applicableFurnRec != null) {
								ItemStack result = applicableFurnRec.getResult();
								double itemsFromFuel = VirtualFurnace.totalItemsFromFuel(VirtualFurnace.getFuel(furnace));
								
								if(itemsFromFuel >= VirtualFurnace.getSmelting(furnace).getAmount()) {
									double itemFromFuel = VirtualFurnace.totalItemsFromFuel(new ItemStack(VirtualFurnace.getFuel(furnace).getType(), 1));
									double requiredFuel = VirtualFurnace.getSmelting(furnace).getAmount() / itemFromFuel;
									
									ItemStack furnaceResult = new ItemStack(result.getType(), result.getAmount() * VirtualFurnace.getSmelting(furnace).getAmount());
									VirtualFurnace.setResult(furnace, furnaceResult);
									VirtualFurnace.setSmelting(furnace, null);
									
									int newFuelAmount = (int) (VirtualFurnace.getFuel(furnace).getAmount() - Math.ceil(requiredFuel));
									if(newFuelAmount > 0) {
										ItemStack newFuel = new ItemStack(VirtualFurnace.getFuel(furnace).getType(), newFuelAmount); 
										VirtualFurnace.setFuel(furnace, newFuel);
									} else {
										ItemStack fuel = VirtualFurnace.getFuel(furnace);
										if(fuel.getType() == Material.LAVA_BUCKET)
											VirtualFurnace.setFuel(furnace, new ItemStack(Material.BUCKET, 1));
										else
											VirtualFurnace.setFuel(furnace, null);
									}
								} else if(itemsFromFuel < VirtualFurnace.getSmelting(furnace).getAmount()) {
									int itemsSmelted = (int) Math.floor(itemsFromFuel);
									
									ItemStack furnaceResult = new ItemStack(applicableFurnRec.getResult().getType(), itemsSmelted);
									ItemStack smelting = VirtualFurnace.getSmelting(furnace);
									ItemStack smeltingResult = new ItemStack(smelting.getType(), smelting.getAmount() - itemsSmelted);
									VirtualFurnace.setResult(furnace, furnaceResult);
									VirtualFurnace.setSmelting(furnace, smeltingResult);
									
									ItemStack fuel = VirtualFurnace.getFuel(furnace);
									if(fuel.getType() == Material.LAVA_BUCKET)
										VirtualFurnace.setFuel(furnace, new ItemStack(Material.BUCKET, 1));
									else
										VirtualFurnace.setFuel(furnace, null);
								}
								plugin.furnaces.remove(player.getName());
								plugin.furnaces.put(player.getName(), furnace);
								player.updateInventory();
							}
						}		
					}
				}
			}
		}	
	}
}
