package com.exloki.foruxmotd.core.persist.mappers;

import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.io.Writer;

public interface IFieldMapper<T> {
    T read(ConfigurationSection section, String originPath);

    void write(ConfigurationSection section, String originPath, T object);

    void write(Writer writer, T object) throws IOException;
}
