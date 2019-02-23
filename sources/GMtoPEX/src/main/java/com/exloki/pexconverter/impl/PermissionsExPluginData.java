package com.exloki.pexconverter.impl;

import com.exloki.pexconverter.model.PermissionsPluginData;
import com.exloki.pexconverter.model.PermissionsPluginExporter;
import com.exloki.pexconverter.model.PermissionsPluginPortException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PermissionsExPluginData extends PermissionsPluginData implements PermissionsPluginExporter {
    public PermissionsExPluginData() {
        super("PermissionsEx");
    }

    @Override
    public void exportGroups(Path pluginFolder) throws PermissionsPluginPortException {
        Path permissionsFile = loadOrCreate(pluginFolder);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(permissionsFile.toFile());
        groups.forEach(groupData -> {
            String key = "groups." + groupData.getName() + ".";
            yaml.set(key + "permissions", groupData.getPermissions());
            yaml.set(key + "options.prefix", groupData.getPrefix());
            yaml.set(key + "options.suffix", groupData.getSuffix());
            yaml.set(key + "options.default", groupData.isDefaultGroup());
            yaml.set(key + "inheritance", groupData.getInheritedGroups());
        });

        try {
            yaml.save(permissionsFile.toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void exportPlayers(Path pluginFolder) throws PermissionsPluginPortException {
        Path permissionsFile = loadOrCreate(pluginFolder);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(permissionsFile.toFile());
        players.forEach(playerData -> {
            String key = "users." + playerData.getUniqueId() + ".";
            yaml.set(key + "permissions", playerData.getPermissions());
            yaml.set(key + "options.name", playerData.getLastKnownName());
            yaml.set(key + "group", playerData.getGroups());
            yaml.set(key + "options.prefix", playerData.getPrefix());
            yaml.set(key + "options.suffix", playerData.getSuffix());
        });

        try {
            yaml.save(permissionsFile.toFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Path loadOrCreate(Path pluginFolder) {
        Path permissionsFile = pluginFolder.resolve("permissions.yml");
        if(!Files.exists(permissionsFile)) {
            try {
                Files.createFile(permissionsFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return permissionsFile;
    }
}
