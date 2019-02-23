package net.richardsprojects.foruxteams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveDataTask extends BukkitRunnable {

	private ForuxTeams plugin;
	private boolean flag;
	
	public SaveDataTask(ForuxTeams plugin, boolean flag) {
		this.plugin = plugin;
		this.flag = flag;
	}
	
	public void run() {
		try {
			save(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void save(boolean flag) throws FileNotFoundException, IOException, InvalidConfigurationException {

		if (plugin.uuidsNeedsUpdate || flag) {
			File uuidsFile = new File(plugin.getDataFolder() + File.separator + "uuids.yml");
			if (uuidsFile.exists()) {
				FileConfiguration uuidsConfig = new YamlConfiguration();
				uuidsConfig.load(uuidsFile.getAbsolutePath());
				
				// loop through and save each UUID
				HashMap<UUID, String> clone = (HashMap<UUID, String>) plugin.uuids.clone();
				Iterator it = clone.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					uuidsConfig.set(pair.getKey().toString(), pair.getValue());
					it.remove();
				}
				uuidsConfig.save(uuidsFile);
			}
			plugin.uuidsNeedsUpdate = false;
		}
		
		// loop through all the teams
		for (Object teamId : plugin.teams.keySet().toArray()) {
		    Team team = plugin.getTeam((Integer) teamId);
		    if (team != null) {
		    	if (team.needsUpdate || flag) {
		    		team.save(plugin);
		    	}
		    }
		}		
	}

}
