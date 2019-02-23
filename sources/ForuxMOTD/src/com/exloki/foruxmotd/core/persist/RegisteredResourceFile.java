package com.exloki.foruxmotd.core.persist;

import lombok.Data;

import java.io.File;
import java.lang.reflect.Field;

@Data final class RegisteredResourceFile {
    private final YamlResourceFile annotation;
    private final File file;
    private final Object instanceBound;
    private final Field field;
}
