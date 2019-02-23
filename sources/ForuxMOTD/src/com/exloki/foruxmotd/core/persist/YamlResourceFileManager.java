package com.exloki.foruxmotd.core.persist;

import com.exloki.foruxmotd.core.LPlugin;
import com.exloki.foruxmotd.core.persist.mappers.IFieldMapper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Concept & code from Redemptive-Core by Twister915
 * https://github.com/Twister915/redemptive
 */

public final class YamlResourceFileManager {
    private final LPlugin plugin;
    private final Set<RegisteredResourceFile> registeredResources = new HashSet<>();

    public YamlResourceFileManager(LPlugin plugin) {
        this.plugin = plugin;
    }

    public void addObject(Object object) {
        hookToObject(object);
        writeDefaultsFor(object);
        loadFor(object);
    }

    public void hookToObject(Object object) {
        hookToObject(object, object.getClass());
    }

    public void hookToObject(Object object, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            YamlResourceFile annotation = field.getAnnotation(YamlResourceFile.class);
            if (annotation == null) continue;
            try {
                File file = new File(plugin.getDataFolder(), annotation.filename());
                registeredResources.add(new RegisteredResourceFile(annotation, file, object, field));
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("Could not register resource from " + type.getSimpleName() + "'s " + field.getName() + " field of type " + field.getType().getName());
            }
        }
        Class<?> superclass = type.getSuperclass();
        if (superclass == Object.class || superclass == JavaPlugin.class)
            return;

        hookToObject(object, superclass);
    }

    public void writeDefaults() {
        for (RegisteredResourceFile registeredResource : registeredResources)
            writeDefault(registeredResource);
    }

    private void writeDefault(RegisteredResourceFile resourceFile) {
        try {
            if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
                throw new IOException("Could not write default resource to data directory!");
            }
            try (InputStream input = plugin.getResource(resourceFile.getAnnotation().filename())) {
                if (input == null) {
                    if(!resourceFile.getAnnotation().raw()) {
                        generateDefault(resourceFile);
                    }
                    return;
                }
                File file = resourceFile.getFile();
                if (file.exists())
                    return;

                if (!file.createNewFile())
                    throw new IOException("Could not create new file!");

                try (OutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = input.read(buffer)) != -1)
                        output.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("Could not write default for " + resourceFile.getAnnotation().filename());
        }
    }

    private void generateDefault(RegisteredResourceFile resourceFile) throws Exception {
        File file = resourceFile.getFile();
        if (file.exists())
            return;

        if (!file.createNewFile())
            throw new IOException("Could not create new file!");
        try (OutputStream stream = Files.newOutputStream(file.toPath())) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            Field resourceField = resourceFile.getField();
            resourceField.setAccessible(true);

            // Generate Header
            GeneratedHeader header = resourceField.getType().getAnnotation(GeneratedHeader.class);
            if(header != null) {
                if(!header.resourcePath().isEmpty()) {
                    try (InputStream input = plugin.getResource(header.resourcePath())) {
                        if (input != null) {
                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = input.read(buffer)) != -1)
                                stream.write(buffer, 0, len);
                            stream.flush();
                        }
                    }
                } else {
                    for (String line : header.value()) {
                        writer.write("# " + line);
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            // Generate Fields
            Object caller = resourceField.get(resourceFile.getInstanceBound());
            if(caller == null) {
                caller = resourceField.getType().getConstructor().newInstance();
            }

            Map<Class<? extends IFieldMapper>, IFieldMapper> buffer = new HashMap<>();
            for(Field field : resourceField.getType().getDeclaredFields()) {
                PersistedField persistedField = field.getAnnotation(PersistedField.class);
                if(persistedField != null) {
                    field.setAccessible(true);

                    // Comments
                    for(String comment : persistedField.comments()) {
                        if(comment.isEmpty()) continue;
                        writer.write("# " + comment);
                        writer.newLine();
                    }

                    // Mapper
                    IFieldMapper mapper;
                    if(buffer.containsKey(persistedField.mapper())) {
                        mapper = buffer.get(persistedField.mapper());
                    } else {
                        mapper = persistedField.mapper().newInstance();
                        buffer.put(persistedField.mapper(), mapper);
                    }

                    // Write
                    String path = persistedField.path().isEmpty() ? field.getName() : persistedField.path();
                    writer.write(path + ": ");
                    Object value = field.get(caller);
                    mapper.write(writer, value);
                    writer.newLine();
                    writer.newLine();
                }
            }
            writer.flush();
        }
    }

    public void loadAll() {
        for (RegisteredResourceFile registeredResource : registeredResources)
            load(registeredResource);
    }

    private void load(RegisteredResourceFile registeredResource) {
        YamlResourceFile annotation = registeredResource.getAnnotation();
        try {
            Object object = registeredResource.getInstanceBound();
            Field field = registeredResource.getField();
            File file = registeredResource.getFile();
            if (!file.exists())
                file.createNewFile();

            Object read;
            if(annotation.raw()) {
                read = new YamlConfigurationFile(plugin, file);
            } else {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                read = field.getType().getConstructor().newInstance();
                loadPersistedFieldsClass(yamlConfiguration, new HashMap<Class<? extends IFieldMapper>, IFieldMapper>(), field.getType(), read);
            }

            field.setAccessible(true);
            field.set(object, read);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not read/load resource " + annotation.filename());
        }
    }

    public <T> void loadPersistedFieldsClass(Path path, Class<?> clazz, T instance) throws PersistenceException, IOException, InvalidConfigurationException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(path.toFile());
        loadPersistedFieldsClass(yaml, clazz, instance);
    }

    public <T> void loadPersistedFieldsClass(ConfigurationSection configurationSection, Class<?> clazz, T instance) throws PersistenceException {
        loadPersistedFieldsClass(configurationSection, new HashMap<Class<? extends IFieldMapper>, IFieldMapper>(), clazz, instance);
    }

    public <T> void loadPersistedFieldsClass(ConfigurationSection configurationSection, Map<Class<? extends IFieldMapper>, IFieldMapper> mapperBuffer, Class<?> clazz, T instance) throws PersistenceException {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            PersistedField persistedField = declaredField.getAnnotation(PersistedField.class);
            if (persistedField != null) {
                Class<? extends IFieldMapper> mapperClass = persistedField.mapper();
                IFieldMapper<?> mapper = mapperBuffer.get(mapperClass);
                try {
                    if (mapper == null) {
                        mapper = mapperClass.newInstance();
                        mapperBuffer.put(mapperClass, mapper);
                    }

                    String path = persistedField.path().isEmpty() ? declaredField.getName() : persistedField.path();
                    Object value = mapper.read(configurationSection, path);

                    if (value != null) {
                        declaredField.setAccessible(true);
                        declaredField.set(instance, value);
                    }
                } catch (IllegalAccessException | InstantiationException | IllegalArgumentException e) {
                    throw new PersistenceException("Unable to load instance of " + instance.toString() + " from Yaml", e);
                }
            }
        }
    }

    public void saveAll() {
        for (RegisteredResourceFile registeredResource : registeredResources)
            save(registeredResource);
    }

    public void save(Object instance, String fieldName) {
        for (RegisteredResourceFile registeredResource : registeredResources)
            if (registeredResource.getInstanceBound() == instance && registeredResource.getField().getName().equals(fieldName)) {
                save(registeredResource);
                return;
            }
    }

    public void saveAll(Object o) {
        for (RegisteredResourceFile registeredResource : registeredResources)
            if (registeredResource.getInstanceBound() == o)
                save(registeredResource);
    }

    private void save(RegisteredResourceFile registeredResource) {
        try {
            Field field = registeredResource.getField();
            field.setAccessible(true);
            if (field.isAnnotationPresent(ReadOnlyResource.class))
                return;

            Object value = field.get(registeredResource.getInstanceBound());
            File file = registeredResource.getFile();
            if (registeredResource.getAnnotation().raw() && value instanceof YamlConfigurationFile) {
                ((YamlConfigurationFile) value).saveConfig();
            } else {
                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                savePersistedFieldsClass(yamlConfiguration,  new HashMap<Class<? extends IFieldMapper>, IFieldMapper>(), field.getType(), value);
                yamlConfiguration.save(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Field field = registeredResource.getField();
            plugin.getLogger().severe("Could not write resource from " + field.getType().getSimpleName() + " field named " + field.getName());
        }
        plugin.getLogger().info("Wrote resource " + registeredResource.getFile().getName() + " for " + registeredResource.getField().getName() +" on " + registeredResource.getInstanceBound().getClass().getSimpleName() + "!");
    }

    public <T> void savePersistedFieldsClass(Path path, Class<?> clazz, T instance) throws PersistenceException, IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        savePersistedFieldsClass(yaml, clazz, instance);
        yaml.save(path.toFile());
    }

    public <T> void savePersistedFieldsClass(ConfigurationSection configurationSection, Class<?> clazz, T instance) throws PersistenceException {
        savePersistedFieldsClass(configurationSection, new HashMap<Class<? extends IFieldMapper>, IFieldMapper>(), clazz, instance);
    }

    public <T> void savePersistedFieldsClass(ConfigurationSection configurationSection, Map<Class<? extends IFieldMapper>, IFieldMapper> mapperBuffer, Class<?> clazz, T instance) throws PersistenceException {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            PersistedField persistedField = declaredField.getAnnotation(PersistedField.class);
            if (persistedField != null) {
                declaredField.setAccessible(true);
                Class<? extends IFieldMapper> mapperClass = persistedField.mapper();
                IFieldMapper<?> mapper = mapperBuffer.get(mapperClass);
                try {
                    if (mapper == null) {
                        mapper = mapperClass.newInstance();
                        mapperBuffer.put(mapperClass, mapper);
                    }

                    String path = persistedField.path().isEmpty() ? declaredField.getName() : persistedField.path();
                    if(declaredField.get(instance) != null) {
                        Method setValueMethod = mapper.getClass().getDeclaredMethod("write", ConfigurationSection.class, String.class, Object.class);
                        setValueMethod.invoke(mapper, configurationSection, path, declaredField.get(instance));
                    } else {
                        configurationSection.set(path, "NULL");
                    }
                } catch (Exception ex) {
                    throw new PersistenceException("Unable to save instance of " + instance.toString() + " to Yaml", ex);
                }
            }
        }
    }

    public <T> void savePersistedFieldsClass(Writer writer, Map<Class<? extends IFieldMapper>, IFieldMapper> mapperBuffer, Class<?> clazz, T instance) throws PersistenceException {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            PersistedField persistedField = declaredField.getAnnotation(PersistedField.class);
            if (persistedField != null) {
                declaredField.setAccessible(true);
                Class<? extends IFieldMapper> mapperClass = persistedField.mapper();
                IFieldMapper<?> mapper = mapperBuffer.get(mapperClass);
                try {
                    if (mapper == null) {
                        mapper = mapperClass.newInstance();
                        mapperBuffer.put(mapperClass, mapper);
                    }

                    if(declaredField.get(instance) != null) {
                        String path = persistedField.path().isEmpty() ? declaredField.getName() : persistedField.path();
                        Method setValueMethod = mapper.getClass().getDeclaredMethod("write", Writer.class, String.class, Object.class);
                        setValueMethod.invoke(mapper, writer, path, declaredField.get(instance));
                    } else {
                        writer.write("NULL");
                    }
                } catch (Exception ex) {
                    throw new PersistenceException("Unable to save instance of " + instance.toString() + " to Yaml", ex);
                }
            }
        }
    }

    public void loadFor(Object object) {
        for (RegisteredResourceFile registeredResource : registeredResources)
            if (registeredResource.getInstanceBound() == object)
                load(registeredResource);
    }

    public void writeDefaultsFor(Object object) {
        for (RegisteredResourceFile registeredResource : registeredResources)
            if (registeredResource.getInstanceBound() == object)
                writeDefault(registeredResource);
    }
}
