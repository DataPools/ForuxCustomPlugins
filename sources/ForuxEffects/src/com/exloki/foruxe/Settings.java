package com.exloki.foruxe;

import com.exloki.core_foruxe.LPlugin;
import com.exloki.core_foruxe.config.LokiConfig;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class Settings
{
    private final transient LokiConfig config;
    protected final transient LPlugin pl;

    public Settings(LPlugin pl) {
        this.pl = pl;
        config = new LokiConfig(new File(pl.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", ForuxEffects.class);
        reloadConfig();
    }

    public void reloadConfig() {
        config.load();
        debug = _isDebug();
        prefix_message = _getPrefixMessage();
        potionEffectsMap = _getPotionEffectsMap();
        disabled_worlds = _getDisabledWorlds();
    }

    private boolean debug = false;

    private boolean _isDebug()
    {
        return config.getBoolean("debug", false);
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(final boolean debug)
    {
        this.debug = debug;
    }

    private String prefix_message = "";

    private String _getPrefixMessage() {
        return config.getString("prefix_message", "[Forux Effects] ");
    }

    public String getPrefixMessage() {
        return prefix_message;
    }

    public void setPrefixMessage(final String message) {
        this.prefix_message = message;
    }

    private List<String> disabled_worlds = new ArrayList<>();

    private List<String> _getDisabledWorlds() {
        return config.getStringList("disabled_worlds");
    }

    public List<String> getDisabledWorlds() {
        return disabled_worlds;
    }

    // Static, no configuration option needed
    public final int INF_POTION_DURATION_TICKS = Integer.MAX_VALUE;
    private final String JSON_FILE_PATH = ForuxEffects.i.getDataFolder().getAbsolutePath() + File.separatorChar + "effects.json";

    private Map<String, List<PotionEffect>> potionEffectsMap;

    private Map<String, List<PotionEffect>> _getPotionEffectsMap() {
        HashMap<String, List<PotionEffect>> map = new HashMap<>();
        JSONParser parser = new JSONParser();

        try {
            File file = new File(JSON_FILE_PATH);
            if(!file.exists()) {
                Files.copy(ForuxEffects.i.getResource("effects.json"), file.toPath());
                return map;
            }

            JSONObject obj = (JSONObject) parser.parse(new FileReader(JSON_FILE_PATH));
            Map<String, List<String>> effectMap = (Map<String, List<String>>) obj.get("permissionToEffectsMap");

            for(Map.Entry<String, List<String>> entry : effectMap.entrySet()) {
                List<PotionEffect> effects = new ArrayList<>();
                for(String effectString : entry.getValue()) {
                    try {
                        String rawType = effectString.contains(" ") ? effectString.split(" ")[0] : effectString;
                        String rawAmp = effectString.contains(" ") ? effectString.split(" ")[1] : "1";

                        PotionEffectType effectType = PotionEffectType.getByName(rawType);
                        int amplifier = Integer.parseInt(rawAmp) - 1;

                        PotionEffect effect = new PotionEffect(effectType, INF_POTION_DURATION_TICKS, amplifier);
                        effects.add(effect);
                    } catch (Exception ex) {
                        pl.log("Unable to load effect '" + effectString + "' for permission node '" + entry.getKey() + "'");
                    }
                }
                if(effects.size() > 0) {
                    map.put(entry.getKey().replaceAll("@", "."), effects);
                }
            }
        } catch (ParseException | IOException ex) {
            pl.log("Unable to read effects.json file! Perhaps it is malformed?");
        }

        return map;
    }

    public Map<String, List<PotionEffect>> getPotionEffectsMap() {
        return potionEffectsMap;
    }
}