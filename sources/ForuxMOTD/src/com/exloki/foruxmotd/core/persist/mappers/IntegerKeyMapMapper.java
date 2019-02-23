package com.exloki.foruxmotd.core.persist.mappers;

import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class IntegerKeyMapMapper implements IFieldMapper<Map<Integer, ?>> {

    @Override
    public Map<Integer, ?> read(ConfigurationSection section, String originPath) {
        Map<Integer, Object> map = new HashMap<>();
        for(String key : section.getConfigurationSection(originPath).getKeys(false)) {
            map.put(Integer.valueOf(key), section.get(originPath + "." + key));
        }

        return map;
    }

    @Override
    public void write(ConfigurationSection section, String originPath, Map<Integer, ?> object) {
        for(Map.Entry<Integer, ?> entry : object.entrySet()) {
            section.set(originPath + "." + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void write(Writer writer, Map<Integer, ?> object) throws IOException {
        writer.write("\n");
        int k = 1;
        for (Map.Entry<Integer, ?> entry : object.entrySet()) {
            writer.write("  '" + entry.getKey() + "': ");
            if(entry.getValue() instanceof String) {
                writer.write("'" + entry.getValue().toString() + "'");
            } else {
                writer.write(String.valueOf(entry.getValue()));
            }
            if(k++ != object.size()) writer.write("\n");
        }
    }
}