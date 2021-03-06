package com.exloki.forux.ecorewards.core.config;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LokiConfig extends YamlConfiguration {
    protected static final Logger LOGGER = Logger.getLogger("Minecraft");
    protected final File configFile;
    protected String templateName = null;
    protected static final Charset UTF8 = Charset.forName("UTF-8");
    private Class<?> resourceClass = LokiConfig.class;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
    private final AtomicBoolean transaction = new AtomicBoolean(false);

    public LokiConfig(final File configFile) {
        super();
        this.configFile = configFile.getAbsoluteFile();
    }

    private final byte[] bytebuffer = new byte[1024];

    public synchronized void load() {
        if (pendingDiskWrites.get() != 0) {
            LOGGER.log(Level.INFO, "File {0} not read, because it''s not yet written to disk.", configFile);
            return;
        }
        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                LOGGER.log(Level.SEVERE, "Failed to create configuration file: " + configFile.toString());
            }
        }
        // This will delete files where the first character is 0. In most cases they are broken.
        if (configFile.exists() && configFile.length() != 0) {
            try {
                final InputStream input = new FileInputStream(configFile);
                try {
                    if (input.read() == 0) {
                        input.close();
                        configFile.delete();
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        input.close();
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        if (!configFile.exists()) {
            if (legacyFileExists()) {
                convertLegacyFile();
            } else if (altFileExists()) {
                convertAltFile();
            } else if (templateName != null) {
                LOGGER.log(Level.INFO, "Creating configuration from default template: " + configFile.toString());
                createFromTemplate();
            } else {
                return;
            }
        }


        try {
            final FileInputStream inputStream = new FileInputStream(configFile);
            try {
                long startSize = configFile.length();
                if (startSize > Integer.MAX_VALUE) {
                    throw new InvalidConfigurationException("File too big");
                }
                ByteBuffer buffer = ByteBuffer.allocate((int) startSize);
                int length;
                while ((length = inputStream.read(bytebuffer)) != -1) {
                    if (length > buffer.remaining()) {
                        ByteBuffer resize = ByteBuffer.allocate(buffer.capacity() + length - buffer.remaining());
                        int resizePosition = buffer.position();
                        buffer.rewind();
                        resize.put(buffer);
                        resize.position(resizePosition);
                        buffer = resize;
                    }
                    buffer.put(bytebuffer, 0, length);
                }
                buffer.rewind();
                final CharBuffer data = CharBuffer.allocate(buffer.capacity());
                CharsetDecoder decoder = UTF8.newDecoder();
                CoderResult result = decoder.decode(buffer, data, true);
                if (result.isError()) {
                    buffer.rewind();
                    data.clear();
                    LOGGER.log(Level.INFO, "File " + configFile.getAbsolutePath() + " is not utf-8 encoded, trying " + Charset.defaultCharset().displayName());
                    decoder = Charset.defaultCharset().newDecoder();
                    result = decoder.decode(buffer, data, true);
                    if (result.isError()) {
                        throw new InvalidConfigurationException("Invalid Characters in file " + configFile.getAbsolutePath());
                    }
                    decoder.flush(data);
                } else {
                    decoder.flush(data);
                }
                final int end = data.position();
                data.rewind();
                super.loadFromString(data.subSequence(0, end).toString());
            } finally {
                inputStream.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (InvalidConfigurationException ex) {
            File broken = new File(configFile.getAbsolutePath() + ".broken." + System.currentTimeMillis());
            configFile.renameTo(broken);
            LOGGER.log(Level.SEVERE, "The file " + configFile.toString() + " is broken, it has been renamed to " + broken.toString(), ex.getCause());
        }
    }

    public boolean legacyFileExists() {
        return false;
    }

    public void convertLegacyFile() {
        LOGGER.log(Level.SEVERE, "Unable to import legacy config file.");
    }

    public boolean altFileExists() {
        return false;
    }

    public void convertAltFile() {
        LOGGER.log(Level.SEVERE, "Unable to import alt config file.");
    }

    private void createFromTemplate() {
        InputStream istr = null;
        OutputStream ostr = null;
        try {
            istr = resourceClass.getResourceAsStream(templateName);
            if (istr == null) {
                LOGGER.log(Level.SEVERE, "Could not find configuration tempalte by the name: " + templateName);
                return;
            }
            ostr = new FileOutputStream(configFile);
            byte[] buffer = new byte[1024];
            int length = istr.read(buffer);
            while (length > 0) {
                ostr.write(buffer, 0, length);
                length = istr.read(buffer);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to write configuration file: " + configFile.toString(), ex);
        } finally {
            try {
                if (istr != null) {
                    istr.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LokiConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if (ostr != null) {
                    ostr.close();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to close IO stream to configuration: " + configFile.toString(), ex);
            }
        }
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public File getFile() {
        return configFile;
    }

    public void setTemplateName(final String templateName, final Class<?> resClass) {
        this.templateName = templateName;
        this.resourceClass = resClass;
    }

    public void startTransaction() {
        transaction.set(true);
    }

    public void stopTransaction() {
        transaction.set(false);
        save();
    }

    public void save() {
        try {
            save(configFile);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void saveWithError() throws IOException {
        save(configFile);
    }

    @Override
    public synchronized void save(final File file) throws IOException {
        if (!transaction.get()) {
            delayedSave(file);
        }
    }

    //This may be aborted if there are stagnant requests sitting in queue.
    //This needs fixed to discard outstanding save requests.
    public synchronized void forceSave() {
        try {
            Future<?> future = delayedSave(configFile);
            if (future != null) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized void cleanup() {
        forceSave();
    }

    private Future<?> delayedSave(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        final String data = saveToString();

        if (data.length() == 0) {
            return null;
        }

        Future<?> future = EXECUTOR_SERVICE.submit(new WriteRunner(configFile, data, pendingDiskWrites));

        return future;
    }

    public boolean hasProperty(final String path) {
        return isSet(path);
    }

    public Location getLocation(final String path, final Server server) throws Exception {
        String raw = getString(path, "");
        if (!raw.contains(",")) return null;

        String[] result = getString(path, "").split(",");

        try {
            return new Location(
                    server.getWorld(result[0]),
                    Double.parseDouble(result[1]),
                    Double.parseDouble(result[2]),
                    Double.parseDouble(result[3]),
                    result.length > 4 ? Float.parseFloat(result[4]) : 0,
                    result.length > 5 ? Float.parseFloat(result[5]) : 0);
        } catch (NumberFormatException ex) {
            server.getLogger().severe("Unable to get location from path '" + path + "' in file '" + this.getFile().getName() + "' as a NumberFormatException was thrown");
            return null;
        }
    }

    public Location getLocation(final String path, final Server server, final Location default_value) {
        try {
            Location match = getLocation(path, server);

            if (match != null) {
                return match;
            }
        } catch (Exception ex) {
        }

        return default_value;
    }


    public void setProperty(final String path, final Location loc) {
        String location_string =
                loc.getWorld().getName() + "," +
                        loc.getX() + "," +
                        loc.getY() + "," +
                        loc.getZ() + "," +
                        loc.getYaw() + "," +
                        loc.getPitch();

        if (path == null) {
            set("", location_string);
        } else {
            set(path, location_string);
        }
    }

	/* Requires LokiLib
	
	public ItemStack[] getItemStackList(String path)
	{
		List<String> list = this.getStringList(path);

		ItemStack[] stack = new ItemStack[list.size()];
		int index = 0;

		for(String str : list)
		{
			stack[index] = ItemUtils.getItemStackFromString(str);
			index++;
		}

		return stack;
	}

	@Override
	public ItemStack getItemStack(final String path)
	{
		return ItemUtils.getItemStackFromString(getString(path, ""));
	}

	public void setProperty(final String path, final ItemStack stack)
	{
		set(path, ItemUtils.getStringFromItemStack(stack));
	} */

    public void setProperty(String path, List<?> object) {
        set(path, new ArrayList<>(object));
    }

    public void setProperty(String path, Map<?, ?> object) {
        set(path, new LinkedHashMap<>(object));
    }

    public Object getProperty(String path) {
        return get(path);
    }

    public void setProperty(final String path, final BigDecimal bigDecimal) {
        set(path, bigDecimal.toString());
    }

    public void setProperty(String path, Object object) {
        set(path, object);
    }

    public void removeProperty(String path) {
        set(path, null);
    }

    @Override
    public synchronized Object get(String path) {
        return super.get(path);
    }

    @Override
    public synchronized Object get(String path, Object def) {
        return super.get(path, def);
    }

    public synchronized BigDecimal getBigDecimal(final String path, final BigDecimal def) {
        final String input = super.getString(path);
        return toBigDecimal(input, def);
    }

    public static BigDecimal toBigDecimal(final String input, final BigDecimal def) {
        if (input == null || input.isEmpty()) {
            return def;
        }
        try {
            return new BigDecimal(input, MathContext.DECIMAL128);
        } catch (NumberFormatException e) {
            return def;
        } catch (ArithmeticException e) {
            return def;
        }
    }

    @Override
    public synchronized boolean getBoolean(String path) {
        return super.getBoolean(path);
    }

    @Override
    public synchronized boolean getBoolean(String path, boolean def) {
        return super.getBoolean(path, def);
    }

    @Override
    public synchronized List<Boolean> getBooleanList(String path) {
        return super.getBooleanList(path);
    }

    @Override
    public synchronized List<Byte> getByteList(String path) {
        return super.getByteList(path);
    }

    @Override
    public synchronized List<Character> getCharacterList(String path) {
        return super.getCharacterList(path);
    }

    @Override
    public synchronized ConfigurationSection getConfigurationSection(String path) {
        return super.getConfigurationSection(path);
    }

    @Override
    public synchronized double getDouble(String path) {
        return super.getDouble(path);
    }

    @Override
    public synchronized double getDouble(final String path, final double def) {
        return super.getDouble(path, def);
    }

    @Override
    public synchronized List<Double> getDoubleList(String path) {
        return super.getDoubleList(path);
    }

    @Override
    public synchronized List<Float> getFloatList(String path) {
        return super.getFloatList(path);
    }

    @Override
    public synchronized int getInt(String path) {
        return super.getInt(path);
    }

    @Override
    public synchronized int getInt(String path, int def) {
        return super.getInt(path, def);
    }

    @Override
    public synchronized List<Integer> getIntegerList(String path) {
        return super.getIntegerList(path);
    }

    @Override
    public synchronized ItemStack getItemStack(String path, ItemStack def) {
        return super.getItemStack(path, def);
    }

    @Override
    public synchronized Set<String> getKeys(boolean deep) {
        return super.getKeys(deep);
    }

    @Override
    public synchronized List<?> getList(String path) {
        return super.getList(path);
    }

    @Override
    public synchronized List<?> getList(String path, List<?> def) {
        return super.getList(path, def);
    }

    @Override
    public synchronized long getLong(String path) {
        return super.getLong(path);
    }

    @Override
    public synchronized long getLong(final String path, final long def) {
        return super.getLong(path, def);
    }

    @Override
    public synchronized List<Long> getLongList(String path) {
        return super.getLongList(path);
    }

    public synchronized Map<String, Object> getMap() {
        return map;
    }

    @Override
    public synchronized List<Map<?, ?>> getMapList(String path) {
        return super.getMapList(path);
    }

    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path) {
        return super.getOfflinePlayer(path);
    }

    @Override
    public synchronized OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
        return super.getOfflinePlayer(path, def);
    }

    @Override
    public synchronized List<Short> getShortList(String path) {
        return super.getShortList(path);
    }

    @Override
    public synchronized String getString(String path) {
        return super.getString(path);
    }

    @Override
    public synchronized String getString(String path, String def) {
        return super.getString(path, def);
    }

    @Override
    public synchronized List<String> getStringList(String path) {
        return super.getStringList(path);
    }

    @Override
    public synchronized Map<String, Object> getValues(boolean deep) {
        return super.getValues(deep);
    }

    @Override
    public synchronized Vector getVector(String path) {
        return super.getVector(path);
    }

    @Override
    public synchronized Vector getVector(String path, Vector def) {
        return super.getVector(path, def);
    }

    @Override
    public synchronized boolean isBoolean(String path) {
        return super.isBoolean(path);
    }

    @Override
    public synchronized boolean isConfigurationSection(String path) {
        return super.isConfigurationSection(path);
    }

    @Override
    public synchronized boolean isDouble(String path) {
        return super.isDouble(path);
    }

    @Override
    public synchronized boolean isInt(String path) {
        return super.isInt(path);
    }

    @Override
    public synchronized boolean isItemStack(String path) {
        return super.isItemStack(path);
    }

    @Override
    public synchronized boolean isList(String path) {
        return super.isList(path);
    }

    @Override
    public synchronized boolean isLong(String path) {
        return super.isLong(path);
    }

    @Override
    public synchronized boolean isOfflinePlayer(String path) {
        return super.isOfflinePlayer(path);
    }

    @Override
    public synchronized boolean isSet(String path) {
        return super.isSet(path);
    }

    @Override
    public synchronized boolean isString(String path) {
        return super.isString(path);
    }

    @Override
    public synchronized boolean isVector(String path) {
        return super.isVector(path);
    }

    @Override
    public synchronized void set(String path, Object value) {
        super.set(path, value);
    }
}
