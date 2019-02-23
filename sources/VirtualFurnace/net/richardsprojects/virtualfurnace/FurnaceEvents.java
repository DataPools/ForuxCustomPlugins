package net.richardsprojects.virtualfurnace;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FurnaceEvents implements Listener {
	
	private VirtualFurnace plugin;
	
	public FurnaceEvents(VirtualFurnace plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void furnaceRemoveItem(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		if(e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT) {
			if(e.getView().getTopInventory() != null) {
				if(e.getView().getTopInventory().getType() == InventoryType.FURNACE) {
					Inventory furnaceInv = e.getView().getTopInventory();
					
					if(furnaceInv.getTitle().equals("InstantFurnace")) {
						plugin.furnaces.remove(player.getName());
						plugin.furnaces.put(player.getName(), e.getView().getTopInventory());
									
						new UpdateFurnaceTask(plugin, player).runTaskLaterAsynchronously(plugin, 5);
					}
				}
			}
		}
	}
	
	/* Manually handle shift-click:
	 * Game can't understand when there is not a container attached too it
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void furnaceShiftClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		
		if(e.getView().getTopInventory() != null) {
			if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
				if(e.getView().getTopInventory().getType() == InventoryType.FURNACE) {
					Inventory furnaceInv = e.getView().getTopInventory();
				
					// Calculate clicked inventory
					boolean clickedInventoryFurnace = false;
					if(e.getClickedInventory() != null) {
						if(e.getClickedInventory().getTitle() != null) {
							String title = e.getClickedInventory().getTitle();
							if(title.equals("InstantFurnace")) {
								clickedInventoryFurnace = true;
							}
						}
					}
					
					// Calculate inventory
					boolean inventoryFurnace = false;
					if(e.getInventory() != null) {
						if(e.getInventory().getTitle() != null) {
							String title = e.getInventory().getTitle();
							if(title.equals("InstantFurnace")) {
								inventoryFurnace = true;
							}
						}
					}
					
					if(inventoryFurnace || clickedInventoryFurnace) {
						// they shift-clicked something out of the furnace
						if(inventoryFurnace && clickedInventoryFurnace) {
							if(e.getCurrentItem() != null) {
								ItemStack cItem = e.getCurrentItem();
								HashMap<Integer, ItemStack> rItems = player.getInventory().addItem(cItem);
								if(rItems.isEmpty()) {
									int slot = e.getRawSlot();
									if(slot == 0) {
										VirtualFurnace.setSmelting(furnaceInv, null);
									} else if(slot == 1) {
										VirtualFurnace.setFuel(furnaceInv, null);
									} else if(slot == 2) {
										VirtualFurnace.setResult(furnaceInv, null);
									}
								} else {
									Entry<Integer, ItemStack> entry = rItems.entrySet().iterator().next();
									if(entry != null) {
										int slot = e.getRawSlot();
										if(slot == 0) {
											VirtualFurnace.setSmelting(furnaceInv, entry.getValue());
										} else if(slot == 1) {
											VirtualFurnace.setFuel(furnaceInv, entry.getValue());
										} else if(slot == 2) {
											VirtualFurnace.setResult(furnaceInv, entry.getValue());
										}
									}
								}
							}
						} else {
							// they shift-clicked something into the furnace
							if(e.getCurrentItem() != null) {
								ItemStack cItem = e.getCurrentItem();
								if(VirtualFurnace.totalItemsFromFuel(cItem) != 0 && VirtualFurnace.getFuel(furnaceInv) == null) {
									VirtualFurnace.setFuel(furnaceInv, cItem);
									e.getView().getBottomInventory().setItem(e.getSlot(), null);
								} else if(VirtualFurnace.getSmelting(furnaceInv) == null) {
									e.getView().getBottomInventory().setItem(e.getSlot(), null);
									VirtualFurnace.setSmelting(furnaceInv, cItem);
								}
							}
						}
						
						e.setCancelled(true);
						player.updateInventory();
						plugin.furnaces.remove(player.getName());
						plugin.furnaces.put(player.getName(), e.getView().getTopInventory());
						new UpdateFurnaceTask(plugin, player).runTaskLaterAsynchronously(plugin, 5);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void loadFurnaceOnLogin(PlayerJoinEvent e) {
		if(!plugin.furnaces.containsKey(e.getPlayer().getName())) {
			plugin.loadFurnaceFromDisk(e.getPlayer().getName());
		}
	}
}
