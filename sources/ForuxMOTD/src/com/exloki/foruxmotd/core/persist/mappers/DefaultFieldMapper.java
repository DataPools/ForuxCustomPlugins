package com.exloki.foruxmotd.core.persist.mappers;

import com.exloki.foruxmotd.core.utils.Txt;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class DefaultFieldMapper implements IFieldMapper<Object> {

    @Override
    public Object read(ConfigurationSection section, String originPath) {
        if(section.isString(originPath)) {
            return Txt.parseColor(section.getString(originPath));
        } else if(section.isBoolean(originPath)) {
            return section.getBoolean(originPath);
        } else if(section.isDouble(originPath)) {
            return section.getDouble(originPath);
        } else if(section.isInt(originPath)) {
            return section.getInt(originPath);
        } else if(section.isLong(originPath)) {
            return section.getLong(originPath);
        } else if(section.isList(originPath)) {
            return section.getList(originPath);
        } else if(section.isVector(originPath)) {
            return section.getVector(originPath);
        } else if(section.isOfflinePlayer(originPath)) {
            return section.getOfflinePlayer(originPath);
        } else if(section.isColor(originPath)) {
            return section.getColor(originPath);
        } else {
            return section.get(originPath);
        }
    }

    @Override
    public void write(ConfigurationSection section, String originPath, Object object) {
        section.set(originPath, object);
    }

    @Override
    public void write(Writer writer, Object object) throws IOException {
        if (object instanceof String) {
            writer.write("'" + object.toString() + "'");
        } else if(object instanceof Collection) {
            writer.write("\n");
            Collection<?> coll = (Collection<?>) object;
            int k = 1;
            for(Object obj : coll) {
                writer.write("  - ");
                if(obj instanceof String) {
                    writer.write("'" + obj.toString() + "'");
                } else {
                    writer.write(obj.toString());
                }
                if(k++ != coll.size()) writer.write("\n");
            }
        } else {
            writer.write(object.toString());
        }
    }
}
