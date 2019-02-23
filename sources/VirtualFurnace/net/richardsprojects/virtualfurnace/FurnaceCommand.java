package net.richardsprojects.virtualfurnace;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class FurnaceCommand implements CommandExecutor {

	private VirtualFurnace plugin;

	public FurnaceCommand(VirtualFurnace plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			Inventory furnace = plugin.getFurnace(player.getName());
			if(furnace == null) {
				plugin.createVirtualFurnace(player);
				furnace = plugin.getFurnace(player.getName());
			}
			
			player.openInventory(furnace);
			
			return true;
		} else {
			sender.sendMessage("You must be a player in order to use the furnace command");
			return true;
		}
	}

}
