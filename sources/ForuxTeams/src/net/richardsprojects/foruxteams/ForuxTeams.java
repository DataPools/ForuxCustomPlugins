package net.richardsprojects.foruxteams;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ForuxTeams extends JavaPlugin {

	private PluginManager pm;
	
	public Logger log;
	
	private FileConfiguration config;
	
	private HashMap<String, Boolean> friendlyFire = new HashMap<String, Boolean>();
	
	public HashMap<UUID, String> uuids = new HashMap<UUID, String>();
	
	public boolean uuidsNeedsUpdate = false;
	
	public HashMap<Integer, Team> teams = new HashMap<Integer, Team>();
	
	private SaveDataTask saveDataTask;
	
	public void onEnable() {
		pm = getServer().getPluginManager();
		log = Logger.getLogger("Minecraft");
	
		boolean dataFolderExists = true;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
			File teamFolder = new File(getDataFolder().toString() + File.separator + "teams");
			teamFolder.mkdirs();
			dataFolderExists = false;
		}
		
		// load data from config files
		if (!dataFolderExists) {
			setupInitialConfig();
		}
		loadConfig();
		loadUUIDs();
		loadTeams();
		
		// events
		pm.registerEvents(new LoginEvent(this), this); 
		pm.registerEvents(new PlayerDamagePlayerEvent(this), this);
		
		// commands
		getCommand("team").setExecutor(new TeamCommand(this));
		
		// tasks
		saveDataTask = new SaveDataTask(this, false);
		saveDataTask.runTaskTimerAsynchronously(this, 600, 600);
		new UpdateTeamPendingMembers(this).runTaskTimerAsynchronously(this, 20, 20);
		
		log.info("[ForuxTeams] Developed by Dev_Richard as a commissioned plugin on 01/19/2017");
	}

	public void onDisable() {
		log.info("[ForuxTeams] Saving data to disk...");
		saveDataTask.cancel();
		saveDataTask = new SaveDataTask(this, true);
		saveDataTask.run();
	}
	
	private void setupInitialConfig() {
		try {
			// create UUID's file
			File uuidsFile = new File(getDataFolder() + File.separator + "uuids.yml");
			uuidsFile.createNewFile();	
			
			// create config file
			File configFile = new File(getDataFolder() + File.separator + "config.yml");
			configFile.createNewFile();
			config = new YamlConfiguration();
			config.load(configFile);
			
			// loop through the worlds and set default settings
			for (World world : getServer().getWorlds()) {
				String wName = world.getName();
				config.set(wName + ".allowFriendlyFire", false);
			}
			
			config.save(configFile); //save config
		} catch (Exception e) {
			log.info(" There was an error setting up the config files...");
			log.info(" Please check your permissions on the \"plugins\" folder. ");
			log.info(" Disabling plugin...");
			pm.disablePlugin(this);
			return;
		}
	}
	
	/**
	 * loads config data for all the different worlds. If a world does not have
	 * a config setting yet it is added to the file.
	 */
	private void loadConfig() {
		try {
			File configFile = new File(getDataFolder() + File.separator + "config.yml");
			config = new YamlConfiguration();
			config.load(configFile);
			
			// loop through all worlds and load relevant settings
			for (World world : getServer().getWorlds()) {
				String wName = world.getName();
				boolean value = false;
				
				if (config.contains(wName + ".allowFriendlyFire")) {
					value = config.getBoolean(wName + ".allowFriendlyFire");
				} else {
					config.set(wName + ".allowFriendlyFire", false);
				}
				
				friendlyFire.put(wName, value); // add settings to HashMap
			}
			
			config.save(configFile); // save config file
		} catch (Exception e) {
			log.info(" There was an error loading settings from the config files...");
			log.info(" Please check your permissions on the \"plugins\" folder. ");
			log.info(" Disabling plugin...");
			pm.disablePlugin(this);
			return;
		}
	}
	
	/**
	 * Loads data from UUIDs yml file.
	 */
	private void loadUUIDs() {
		try {
			// load data from uuids file
			File uuidsFile = new File(getDataFolder() + File.separator + "uuids.yml");
			if (uuidsFile.exists()) {
				FileConfiguration uuidsConfig = new YamlConfiguration();
				uuidsConfig.load(uuidsFile.getAbsolutePath());
				
				for (String key : uuidsConfig.getKeys(false)) {
					UUID uuidKey = UUID.fromString(key);
					String value = uuidsConfig.getString(key);
					uuids.put(uuidKey, value);
				}
			}
		} catch (Exception e) {
			log.info(" There was a problem loading the data from uuids.yml");
			log.info(" Check to make sure they are not corrupted and try again.");
			log.info(" Disabling plugin...");
			pm.disablePlugin(this);
			return;
		}
	}
	
	/**
	 * Loads team data from yml files.
	 */
	public void loadTeams() {
		File[] teamsDataFiles = new File(getDataFolder().getAbsolutePath() + File.separator + "teams").listFiles();
		for(File teamFile : teamsDataFiles) {
			try {
				if (teamFile.getName().endsWith(".yml")) {
					FileConfiguration teamConfig = new YamlConfiguration();
					teamConfig.load(teamFile);
					
					UUID leader = UUID.fromString((String) teamConfig.get("leader"));
					List<UUID> members = Utils.convertStringToList((String) teamConfig.get("members"));
					String strId = teamFile.getName();
					strId = strId.replace(".yml", "");
					int id = Integer.parseInt(strId);
					
					Team loadedTeam = new Team (id, leader, members);
					teams.put(id, loadedTeam);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("[ForuxTeams] There was a problem reading " + teamFile.getName() + ". Skipping...");
			}
		}
	}
	
	/**
	 * Determines the next available id and creates a team object with that id. 
	 * Returns zero if there was a problem while attempting to create the team.
	 * 
	 * @return the id of the new team
	 */
	public int createNewTeam(UUID leader) {
		// determine highest id
		int highestId = 0;
		String[] teamsDataFiles = new File(getDataFolder().getAbsolutePath() + File.separator + "teams").list();
		for(String teamFile : teamsDataFiles) {
			String teamIdStr = teamFile.replace(".yml", "");
			try {
				int teamId = Integer.parseInt(teamIdStr);
				if(teamId > highestId) {
					highestId = teamId;
				}
			} catch(Exception e) {
				return 0;
			}
		}
		highestId++;
		
		Team newTeam = new Team(highestId, leader, this);
		teams.put(highestId, newTeam);
		return highestId;
	}
	
	public UUID getPlayerUUID(String username) {
		if (uuids.containsValue(username)) {
		    for (Entry<UUID, String> entry : uuids.entrySet()) {
		        if (username.equals(entry.getValue())) {
		            return entry.getKey();
		        }
		    }
		    return null;
		} else {
			return null;
		}
	}
	
	public String getPlayerName(UUID player) {
		if (uuids.containsKey(player)) {
		    for (Entry<UUID, String> entry : uuids.entrySet()) {
		        if (player.equals(entry.getKey())) {
		            return entry.getValue();
		        }
		    }
		    return null;
		} else {
			return null;
		}
	}
	
	public void updateUUID(String name, UUID uuid) {
		if (uuids.containsKey(uuid)) {
			uuids.remove(uuid);
			uuids.put(uuid, name);
		} else {
			uuids.put(uuid, name);
		}
		uuidsNeedsUpdate = true;
	}
	
	public Team getTeam(int id) {
		if (teams.containsKey(id)) {
			return teams.get(id);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the team the player is in. If they are not in a team it returns 
	 * null.
	 * 
	 * @return the team they are a member of or null
	 */
	public Team getPlayersTeam(UUID player) {
		Team team = null;
		
		for (Object id : teams.keySet().toArray()) {
			Team cTeam = getTeam((Integer) id);
			if (cTeam != null) {
				if (cTeam.getLeader().equals(player)) {
					team = cTeam;
				}
				if (cTeam.hasMember(player)) team = cTeam;
			}
		}
		
		return team;
	}
	
	/**
	 * Returns the team has a pending invite for. If there is no pending invite 
	 * the method returns null.
	 * 
	 * @return the team they have a pending invite for or null
	 */
	public Team getPlayersTeamInvite(UUID player) {
		Team team = null;
		
		for (Object id : teams.keySet().toArray()) {
			Team cTeam = getTeam((Integer) id);
			if (cTeam != null) {
				if (cTeam.hasPendingInvite(player)) {
					team = cTeam;
				}
			}
		}
		
		return team;
	}
	
	public boolean disbandTeam(int teamId) {
		boolean result = false;
		
		if (teams.containsKey(teamId)) {
			Team team = teams.get(teamId);
			team.needsUpdate = false;
			result = team.deleteTeam(this);
			if (result) teams.remove(teamId);
		}
		
		return result;
	}
	
	public boolean isFriendlyFireEnabled(String wName) {
		boolean result = false;
		
		if (friendlyFire.containsKey(wName)) {
			result = friendlyFire.get(wName);
		}
		
		return result;
	}
}