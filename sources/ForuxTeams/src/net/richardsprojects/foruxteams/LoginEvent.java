package net.richardsprojects.foruxteams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginEvent implements Listener {

	private ForuxTeams plugin;

	public LoginEvent(ForuxTeams plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void loginEvent(final PlayerJoinEvent e) {
		// update the UUID's file every time a player joins
		Player player = e.getPlayer();
		plugin.updateUUID(player.getName(), player.getUniqueId());
	}
}
