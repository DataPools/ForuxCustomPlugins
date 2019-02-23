package net.richardsprojects.foruxteams;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Team {

	private List<UUID> members = new ArrayList<UUID>();
	
	private UUID leader = null;
	
	private List<PendingMember> pendingMembers = new ArrayList<PendingMember>(); 
	
	private int id = 0;
	
	public boolean needsUpdate = false;
	
	/**
	 * Constructor for creating a completely new team.
	 * 
	 * @param id
	 * @param leader
	 */
	public Team (int id, UUID leader, ForuxTeams plugin) {
		this.id = id;
		this.leader = leader;
		
		this.save(plugin);
	}
	
	/**
	 * Constructor for creating a team object that is from loaded data.
	 */
	public Team (int id, UUID leader, List<UUID> members) {
		this.id = id;
		this.leader = leader;
		this.members = members;
	}
	
	public int getId() {
		return this.id;
	}
	
	public UUID getMember(int number) {
		if (number < members.size() && number > -1) {
			return members.get(number);
		} else {
			return null;
		}
	}
	
	public void removeMember(int number) {
		if (number < members.size() && number > -1) {
			members.remove(number);
		}
		
		this.needsUpdate = true;
	}
	
	public UUID getLeader() {
		return this.leader;
	}

	public void addPendingMember(UUID newMember) {
		PendingMember pending = new PendingMember(newMember);
		pendingMembers.add(pending);
	}
	
	public void removePendingMember(UUID member) {
		ArrayList toRemove = new ArrayList();
		
		for (PendingMember pending : pendingMembers) {
			if (pending.getMember().equals(member)) {
				toRemove.add(pending);
			}
		}
		
		pendingMembers.removeAll(toRemove);
	}
	
	/**
	 * Meant to be run once a second and will remove any pending members whose
	 * time to accept has run out.
	 */
	public void updatePendingMembers() {
		for (Object member : pendingMembers.toArray()) {
			PendingMember pending = (PendingMember) member;
			if (pending.getTimeLeft() > 0) {
				pending.setTimeLeft(pending.getTimeLeft() - 1);
			} else {
				pendingMembers.remove(member);
				String msg = ChatColor.RED + "Your team invite has expired.";
				Bukkit.getPlayer(pending.getMember()).sendMessage(msg);
			}
		}
	}
	
	public boolean hasMember(UUID member) {
		return members.contains(member);
	}
	
	public boolean hasPendingInvite(UUID member) {
		boolean result = false;
		
		for (PendingMember pending : pendingMembers) {
			if (pending.getMember().equals(member)) result = true;
		}
		
		return result;
	}
	
	public boolean save(ForuxTeams plugin) {
		try {
			// load or create new file
			FileConfiguration teamFile = new YamlConfiguration();
			File file = new File(plugin.getDataFolder().toString() + File.separator + "teams" + File.separator + id + ".yml");
			if (!file.exists()) {
				boolean result = file.createNewFile();
				if (!result) return false;
			}
			teamFile.load(file);
			
			// save data
			teamFile.set("leader", leader.toString());
			teamFile.set("members", Utils.convertListToString(members));
			teamFile.save(file);
			
			this.needsUpdate = false;			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void sendMessage(ForuxTeams plugin, String msg) {
		msg = ChatColor.DARK_GREEN + "[Team] " + ChatColor.RESET + msg;
		Player leader = plugin.getServer().getPlayer(this.getLeader());
		
		if (leader != null) leader.sendMessage(msg);
		for (UUID member : members) {
			Player tmp = plugin.getServer().getPlayer(member);
			if (tmp != null) {
				tmp.sendMessage(msg);
			}
		}
	}
	
	/**
	 * Returns the total members, not counting the leader.
	 * 
	 * @return member count
	 */
	public int getTotalMembers() {
		return this.members.size();
	}
	
	/**
	 * Adds a member
	 * 
	 * @param member UUID of member to be added
	 * @return whether or not it was successful
	 */
	public boolean addMember(UUID newMember) {
		boolean result = false;
		
		if (members.size() < 3) {
			result = members.add(newMember);
		} else {
			result = false;
		}
		if (result) this.needsUpdate = true;
		
		return result;
	}
	
	/**
	 * remove a member
	 * 
	 * @param member UUID of member to be added
	 * @return whether or not it was successful
	 */
	public boolean removeMember(UUID member) {
		boolean result = false;
		
		if (members.contains(member)) {
			result = members.remove(member);
		} else {
			result = false;
		}
		if (result) this.needsUpdate = true;
		
		return result;
	}
	
	public boolean deleteTeam(ForuxTeams plugin) {
		File file = new File(plugin.getDataFolder().toString() + File.separator + "teams" + File.separator + id + ".yml");
		return file.delete();
	}
}
