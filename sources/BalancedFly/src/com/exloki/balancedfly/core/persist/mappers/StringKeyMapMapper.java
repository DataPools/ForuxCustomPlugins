package com.exloki.balancedfly.core.persist.mappers;

import com.exloki.balancedfly.core.collections.StringKeyMap;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class StringKeyMapMapper implements IFieldMapper<StringKeyMap<?>> {

    @Override
    public StringKeyMap<?> read(ConfigurationSection section, String originPath) {
        StringKeyMap<Object> map = new StringKeyMap<>(new HashMap<String, Object>());
        for(String key : section.getConfigurationSection(originPath).getKeys(false)) {
            map.put(key, section.get(originPath + "." + key));
        }

        return map;
    }

    @Override
    public void write(ConfigurationSection section, String originPath, StringKeyMap<?> object) {
        for(Map.Entry<String, ?> entry : object.entrySet()) {
            section.set(originPath + "." + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void write(Writer writer, StringKeyMap<?> object) throws IOException {
        writer.write("\n");
        int k = 1;
        for (Map.Entry<String, ?> entry : object.entrySet()) {
            if(entry.getKey().isEmpty()) continue;
            writer.write("  '" + entry.getKey() + "': ");
            if(entry.getValue() instanceof String) {
                writer.write("'" + String.valueOf(entry.getValue()) + "'");
            } else {
                writer.write(String.valueOf(entry.getValue()));
            }
            if(k++ != object.size()) writer.write("\n");
        }
    }
}