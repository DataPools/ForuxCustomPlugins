package com.exloki.pexconverter;

import com.exloki.pexconverter.impl.GroupManagerPluginData;
import com.exloki.pexconverter.impl.PermissionsExPluginData;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;
import java.nio.file.Path;

public class GMtoPEX extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            log("Searching for GroupManager plugin folder...");
            Path gmPath = getDataFolder().toPath().getParent().resolve("GroupManager");
            if(!Files.exists(gmPath) || !Files.isDirectory(gmPath)) {
                log("Unable to locate GroupManager plugin folder at ./plugins/GroupManager !");
                log("Disabling...");
                setEnabled(false);
                return;
            }
            log("GroupManager plugin folder located");

            log("Searching for, or creating a new, PermissionsEx plugin folder...");
            Path pexPath = getDataFolder().toPath().getParent().resolve("PermissionsEx");
            if(!Files.exists(pexPath) || !Files.isDirectory(pexPath)) {
                Files.createDirectory(pexPath);
            }
            log("PermissionsEx plugin folder located or created");

            GroupManagerPluginData gmData = new GroupManagerPluginData();
            gmData.importGroups(gmPath);
            gmData.importPlayers(gmPath);

            PermissionsExPluginData pexData = new PermissionsExPluginData();
            pexData.setGroups(gmData.getGroups());
            pexData.setPlayers(gmData.getPlayers());

            pexData.exportGroups(pexPath);
            pexData.exportPlayers(pexPath);

            log("Successfully completed conversion of GroupManager data to PermissionsEx!");
            log("Please restart your server with GroupManager disabled and PermissionsEx enabled.");
        } catch (Exception ex) {
            log("Unable to complete conversion! The following error occurred:");
            log(ex.getMessage());
            log("Disabling...");
            setEnabled(false);
        }
    }

    private void log(String message) {
        getLogger().info(message);
    }
}
