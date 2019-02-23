package com.exloki.pexconverter.impl;

import com.exloki.pexconverter.GMtoPEX;
import com.exloki.pexconverter.model.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupManagerPluginData extends PermissionsPluginData implements PermissionsPluginImporter {
    public GroupManagerPluginData() {
        super("GroupManager");
    }

    @Override
    public void importGroups(Path pluginFolder) throws PermissionsPluginPortException {
        // Import global groups
        Path globalGroupsFile = pluginFolder.resolve("globalgroups.yml");
        if(!Files.exists(globalGroupsFile))
            throw new PermissionsPluginPortException("globalgroups.yml file does not exist!");
        importGlobalGroups(globalGroupsFile);

        // Import per-world groups
        Path worldsDir = pluginFolder.resolve("worlds");
        if(!Files.exists(worldsDir) || !Files.isDirectory(worldsDir))
            throw new PermissionsPluginPortException("unable to locate './worlds' directory");

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(worldsDir)) {
            for(Path worldDir : stream) {
                Path groupsFile = worldDir.resolve("groups.yml");
                if(Files.exists(groupsFile)) {
                    importWorldGroups(groupsFile);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void importGlobalGroups(Path file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
        yaml.getConfigurationSection("groups").getKeys(false).forEach(groupKey -> {
            String name = groupKey.startsWith("g:") ? groupKey.substring(2) : groupKey;
            List<String> permissions = yaml.getStringList("groups." + groupKey + ".permissions");
            GroupData groupData = new GroupData(name, false);
            groupData.setPermissions(permissions);
            groups.add(groupData);
        });
    }

    private void importWorldGroups(Path file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
        yaml.getConfigurationSection("groups").getKeys(false).forEach(groupKey -> {
            boolean isDefault = yaml.getBoolean("groups." + groupKey + ".default", false);
            List<String> permissions = yaml.getStringList("groups." + groupKey + ".permissions");
            List<String> inheritedGroups = yaml.getStringList("groups." + groupKey + ".inheritance")
                    .stream().map(str -> str.startsWith("g:") ? str.substring(2) : str).collect(Collectors.toList());
            String prefix = yaml.getString("groups." + groupKey + ".info.prefix", "");
            String suffix = yaml.getString("groups." + groupKey + ".info.suffix", "");

            GroupData groupData = new GroupData(groupKey, isDefault);
            groupData.setPermissions(permissions);
            groupData.setInheritedGroups(inheritedGroups);
            groupData.setPrefix(prefix);
            groupData.setSuffix(suffix);
            groups.add(groupData);
        });
    }

    @Override
    public void importPlayers(Path pluginFolder) throws PermissionsPluginPortException {
        Path worldsDir = pluginFolder.resolve("worlds");
        if(!Files.exists(worldsDir) || !Files.isDirectory(worldsDir))
            throw new PermissionsPluginPortException("unable to locate './worlds' directory");

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(worldsDir)) {
            for(Path worldDir : stream) {
                Path playersFile = worldDir.resolve("users.yml");
                if(Files.exists(playersFile)) {
                    importWorldPlayers(playersFile);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void importWorldPlayers(Path file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file.toFile());
        yaml.getConfigurationSection("users").getKeys(false).forEach(playerId -> {
            if(playerId.length() <= 32) {
                JavaPlugin.getPlugin(GMtoPEX.class).getLogger().info("Skipping user data for '" + playerId + "' as no UUID is provided...");
                return;
            }
            String lastKnownName = yaml.getString("users." + playerId + ".lastname", "");
            List<String> groups = new ArrayList<>();
            String primaryGroup = yaml.getString("users." + playerId + ".group", "");
            if(!primaryGroup.isEmpty()) {
                groups.add(primaryGroup);
            }
            List<String> subGroups = yaml.getStringList("users." + playerId + ".subgroups");
            if(subGroups != null && subGroups.size() > 0) {
                subGroups.forEach(groups::add);
            }
            List<String> permissions = yaml.getStringList("users." + playerId + ".permissions");
            if(permissions == null) permissions = new ArrayList<>();
            String prefix = yaml.getString("users." + playerId + ".info.prefix", "");
            String suffix = yaml.getString("users." + playerId + ".info.suffix", "");

            PlayerData playerData = new PlayerData(playerId, lastKnownName);
            playerData.setGroups(groups);
            playerData.setPermissions(permissions);
            playerData.setPrefix(prefix);
            playerData.setSuffix(suffix);
            players.add(playerData);
        });
    }
}
