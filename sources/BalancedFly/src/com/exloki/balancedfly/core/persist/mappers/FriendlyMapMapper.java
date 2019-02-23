package com.exloki.balancedfly.core.persist.mappers;

import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FriendlyMapMapper implements IFieldMapper<Map<?, ?>> {

    @Override
    public Map<?, ?> read(ConfigurationSection section, String originPath) {
        Map<Object, Object> map = new HashMap<>();

        for (String key : section.getConfigurationSection(originPath).getKeys(false)) {
            map.put(key, section.get(originPath + "." + key));
        }

        return map;
    }

    @Override
    public void write(ConfigurationSection section, String originPath, Map<?, ?> object) {
        section.set(originPath, null); // Clear previous mappings
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            section.set(originPath + "." + String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
    }

    @Override
    public void write(Writer writer, Map<?, ?> object) throws IOException {
        writer.write("\n");
        int k = 1;
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            writer.write("  '" + String.valueOf(entry.getKey()) + "': " + String.valueOf(entry.getValue()));
            if(k++ != object.size()) writer.write("\n");
        }
    }
}
