package net.richardsprojects.virtualfurnace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class VirtualFurnace extends JavaPlugin {

	private PluginManager pm;
	public Logger log;
	
	public Map<String, Inventory> furnaces;
	
	public void onEnable() {
		furnaces = new HashMap<String, Inventory>();
		
		pm = getServer().getPluginManager();
		log = Logger.getLogger("Minecraft");
		
		File file = new File(this.getDataFolder().getAbsolutePath() + File.separator + "furnaces");
		if(!file.exists()) file.mkdirs();
		
		getCommand("furnace").setExecutor(new FurnaceCommand(this));
		pm.registerEvents(new FurnaceEvents(this), this);
		
		// in case the plugin is reloaded while people are online
		for(Player p : getServer().getOnlinePlayers()) {
			loadFurnaceFromDisk(p.getName());
		}
	}
	
	public void onDisable() {
		log.info("Saving Virtual Furnaces to disk...");
		
		@SuppressWarnings("rawtypes")
		Iterator it = furnaces.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        String pName = (String) pair.getKey();
	        Inventory furnace = (Inventory) pair.getValue();
	        
			File furnFile = new File(this.getDataFolder().getAbsolutePath() + File.separator + "furnaces" + File.separator + pName + ".yml");
			if(furnFile.exists()) {
				FileConfiguration fDataFile = new YamlConfiguration();
				try {
					fDataFile.load(furnFile.getAbsolutePath());
					fDataFile.set("result", itemTo64(getResult(furnace)));
					fDataFile.set("smelting", itemTo64(getSmelting(furnace)));
					fDataFile.set("fuel", itemTo64(getFuel(furnace)));
						
					fDataFile.save(furnFile); // save
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
			} else {
				// create file
				try {
					if(furnFile.createNewFile()) {
						FileConfiguration fDataConfig = new YamlConfiguration();
						fDataConfig.load(furnFile.getAbsolutePath());
						
						fDataConfig.set("result", itemTo64(getResult(furnace)));
						fDataConfig.set("smelting", itemTo64(getSmelting(furnace)));
						fDataConfig.set("fuel", itemTo64(getFuel(furnace)));
						
						fDataConfig.options().copyDefaults(true);
						fDataConfig.save(furnFile);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public Inventory getFurnace(String playerName) {
		if(furnaces.containsKey(playerName))
			return furnaces.get(playerName);
		else
			return null;
	}
	
	public void createVirtualFurnace(Player player) {
		Inventory inventory = Bukkit.createInventory(player, InventoryType.FURNACE, "InstantFurnace");		
		this.furnaces.put(player.getName(), inventory);
	}

	public static double totalItemsFromFuel(ItemStack fuel) {
		double totalItems = 0;
				
		Material mat = fuel.getType();
		switch(mat) {
			case LAVA_BUCKET: 		totalItems = 100;
							  		break;
			case COAL_BLOCK:  		totalItems = 80;
							  		break;
			case BLAZE_ROD:   		totalItems = 12;
							  		break;
			case COAL:		  		totalItems = 8;
							  		break;
			case WOOD:	      		totalItems = 1.5;
							  		break;
			case LOG:	      		totalItems = 1.5;
			  				  		break;
			case LOG_2:	      		totalItems = 1.5;
							  		break;
			case WOOD_PLATE:  		totalItems = 1.5;
			  				  		break;
			case WOOD_STAIRS: 		totalItems = 1.5;
			  				  		break;
			case FENCE:       		totalItems = 1.5;
							  		break;
			case FENCE_GATE:  		totalItems = 1.5;
			  				  		break;
			case TRAP_DOOR:	  		totalItems = 1.5;
			                  		break;
			case WORKBENCH:   		totalItems = 1.5;
			  						break;
			case BOOKSHELF:     	totalItems = 1.5;
			  						break;
			case CHEST:         	totalItems = 1.5;
			  				    	break;
			case TRAPPED_CHEST: 	totalItems = 1.5;
			  						break;
			case DAYLIGHT_DETECTOR: totalItems = 1.5;
									break;
			case JUKEBOX:			totalItems = 1.5;
									break;
			case NOTE_BLOCK:		totalItems = 1.5;
									break;
			case HUGE_MUSHROOM_1:	totalItems = 1.5;
									break;
			case HUGE_MUSHROOM_2:   totalItems = 1.5;
									break;
			case BANNER:			totalItems = 1.5;
									break;
			case WOOD_AXE:			totalItems = 1;
									break;
			case WOOD_PICKAXE:		totalItems = 1;
									break;
			case WOOD_SPADE:		totalItems = 1;
									break;
			case WOOD_SWORD:		totalItems = 1;
									break;
			case WOOD_HOE:			totalItems = 1;
									break;
			case WOOD_STEP:			totalItems = .75;
									break;
			case SAPLING:			totalItems = .5;
									break;
			case STICK:				totalItems = .5;
									break;
			default:				totalItems = 0;
									break;
		}
		
		totalItems = fuel.getAmount() * totalItems;
		return totalItems;		
	}
	
	public static ItemStack getResult(Inventory furnace) {
		return furnace.getItem(2);
	}
	
	public static ItemStack getSmelting(Inventory furnace) {
		return furnace.getItem(0);
	}
	
	public static ItemStack getFuel(Inventory furnace) {
		return furnace.getItem(1);
	}
	
	public static void setResult(Inventory furnace, ItemStack i) {
		furnace.setItem(2, i);
	}
	
	public static void setSmelting(Inventory furnace, ItemStack i) {
		furnace.setItem(0, i);
	}
	
	public static void setFuel(Inventory furnace, ItemStack i) {
		furnace.setItem(1, i);
	}

	public String itemTo64(ItemStack stack) throws IllegalStateException {
        if(stack == null) {
        	return "0";
        } else {
			try {
	            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	            dataOutput.writeObject(stack);
	
	            // Serialize that array
	            dataOutput.close();
	            String base64Str = Base64Coder.encodeLines(outputStream.toByteArray());
	            base64Str = base64Str.replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");
	            return base64Str;
	        }
	        catch (Exception e) {
	            throw new IllegalStateException("Unable to save item stack.", e);
	        }
        }
    }
   
    public ItemStack itemFrom64(String data) throws IOException {
        if(data.equals("0")) {
        	return new ItemStack(Material.AIR);
        } else {
        	try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                try {
                    return (ItemStack) dataInput.readObject();
                } finally {
                    dataInput.close();
                }
            }
            catch (ClassNotFoundException e) {
                throw new IOException("Unable to decode class type.", e);
            }
        }
    }
    
    public void loadFurnaceFromDisk(String pName) {
		File file = new File(this.getDataFolder().getAbsolutePath() + File.separator + "furnaces" + File.separator + pName + ".yml");
		if(file.exists()) {
			try {
				FileConfiguration fFile = new YamlConfiguration();
				fFile.load(file);
				
				Inventory furnace = Bukkit.createInventory(null, InventoryType.FURNACE, "InstantFurnace");
				
				ItemStack result = itemFrom64((String) fFile.get("result"));
				ItemStack smelting = itemFrom64((String) fFile.get("smelting"));
				ItemStack fuel = itemFrom64((String) fFile.get("fuel"));
				setFuel(furnace, fuel);
				setSmelting(furnace, smelting);
				setResult(furnace, result);
				
				furnaces.put(pName, furnace);
			} catch(Exception e) {
				log.info("[VirtualFurnace] Error encountered while loading a virtual furnace file from disk:");
				e.printStackTrace();
			}
		}
    }
	
	// TODO: Save furnace files when the plugin is disabled
	// TODO: Load the relevant furnace file when the player logs in
}
