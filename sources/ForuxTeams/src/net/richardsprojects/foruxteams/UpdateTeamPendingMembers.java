package net.richardsprojects.foruxteams;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateTeamPendingMembers extends BukkitRunnable {

	private ForuxTeams plugin;
	
	public UpdateTeamPendingMembers(ForuxTeams plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		for (Object id : plugin.teams.keySet().toArray()) {
		    Team team = plugin.getTeam((Integer) id);
		    if (team != null) team.updatePendingMembers();
		}
	}
}
