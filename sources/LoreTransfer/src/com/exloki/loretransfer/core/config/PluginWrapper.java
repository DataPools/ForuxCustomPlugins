package com.exloki.loretransfer.core.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PluginWrapper extends org.bukkit.plugin.java.JavaPlugin {

    private LokiConfig config;
    private HashMap<String, LokiConfig> fileMap;

    public LokiConfig getConfig() {
        if (config == null) {
            config = new LokiConfig(new File(super.getDataFolder() + "/config.yml"));
            config.setTemplateName("/config.yml");
            config.load();
        }

        return config;
    }

    public File _getFile(String filePath, boolean createIfNull) {
        File f = new File(filePath);
        if (!f.exists()) {
            if (createIfNull) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return f;
    }

    public LokiConfig getFile(String filePath, boolean createIfNull) {
        if (fileMap == null) {
            fileMap = new HashMap<>();
        }
        if (!fileMap.containsKey(filePath)) {
            File f = new File(filePath);
            if (!f.exists()) {
                if (createIfNull) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    return null;
                }
            }
            fileMap.put(filePath, new LokiConfig(f));
            return getFile(filePath, false);
        }
        return fileMap.get(filePath);
    }

    public File getDir(String folderPath, boolean createIfNull) {
        File f = new File(folderPath);
        if (!f.exists()) {
            if (createIfNull) {
                f.mkdirs();
            } else {
                return null;
            }
        } else {
            if (!f.isDirectory()) return null;
        }
        return f;
    }


    public void reloadFile(String filePath) {
        if (fileMap == null) {
            fileMap = new HashMap<>();
            return;
        }
        if (!fileMap.containsKey(filePath)) return;
        fileMap.get(filePath).load();
    }

    public void saveFile(String filePath) {
        if (fileMap == null) {
            fileMap = new HashMap<>();
            return;
        }
        if (!fileMap.containsKey(filePath)) return;
        fileMap.get(filePath).save();
    }

    public void reloadConfig() {
        getConfig().load();
    }

    public void saveConfig() {
        getConfig().save();
    }
}
