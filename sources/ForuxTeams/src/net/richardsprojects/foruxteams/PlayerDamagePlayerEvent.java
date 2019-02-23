package net.richardsprojects.foruxteams;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamagePlayerEvent implements Listener {

	private ForuxTeams plugin;

	public PlayerDamagePlayerEvent(ForuxTeams plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDamagePlayer(final EntityDamageByEntityEvent e) {
		Entity e1 = e.getDamager();
		Entity e2 = e.getEntity();
		
		// prevent the player from shooting their teammate
		if (e1 instanceof Arrow) {
			if (((Arrow) e1).getShooter() instanceof Player) {
				e1 = (Entity) ((Arrow) e1).getShooter();
			}
		}
		
		if (e1 instanceof Player && e2 instanceof Player) {
			Player p1 = (Player) e1;
			Player p2 = (Player) e2;
			
			Team p1Team = plugin.getPlayersTeam(p1.getUniqueId());
			Team p2Team = plugin.getPlayersTeam(p2.getUniqueId());
			
			if (p1Team != null && p2Team != null) {
				if (p1Team.getId() == p2Team.getId()) {
					String wName = p1.getWorld().getName();
					
					if (!plugin.isFriendlyFireEnabled(wName)) {
						String msg = ChatColor.RED + "You cannot injure your teammates in this world.";
						p1.sendMessage(msg);
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
