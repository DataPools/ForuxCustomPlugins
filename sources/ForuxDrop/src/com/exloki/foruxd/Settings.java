package com.exloki.foruxd;

import com.exloki.core_foruxd.LPlugin;
import com.exloki.core_foruxd.config.LokiConfig;
import com.exloki.core_foruxd.utils.Txt;
import com.exloki.foruxd.airdrop.AirDropPackage;
import com.exloki.foruxd.itemdata.ItemStringParser;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Settings {
    private final transient LokiConfig config;
    protected final transient LPlugin pl;

    public Settings(LPlugin pl) {
        this.pl = pl;
        config = new LokiConfig(new File(pl.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", ForuxDrop.class);
        reloadConfig();
    }

    public void reloadConfig() {
        config.load();
        debug = _isDebug();
        prefix_message = _getPrefixMessage();
        broadcast_message = _getBroadcastMessage();
        minXDrop = _getMinXDrop();
        maxXDrop = _getMaxXDrop();
        minZDrop = _getMinZDrop();
        maxZDrop = _getMaxZDrop();
        dropInteveral = _getDropInterval();
        worldNames = _getWorldNames();
        dropMenuTitle = _getDropMenuTitle();
    }

    private boolean debug = false;

    private boolean _isDebug() {
        return config.getBoolean("debug", false);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    private String prefix_message = "";

    private String _getPrefixMessage() {
        return config.getString("prefix_message", "[PREFIX] ");
    }

    public String getPrefixMessage() {
        return prefix_message;
    }

    public void setPrefixMessage(final String message) {
        this.prefix_message = message;
    }

    private String broadcast_message = "";

    private String _getBroadcastMessage() {
        return config.getString("random_air_drops.broadcast_message");
    }

    public String getBroadcastMessage() {
        return broadcast_message; // Unformatted
    }

    private int minXDrop = -1000;
    private int maxXDrop = 1000;

    private int _getMinXDrop() {
        return config.getInt("random_air_drops.minimum_x");
    }

    private int _getMaxXDrop() {
        return config.getInt("random_air_drops.maximum_x");
    }

    public int getMinXDrop() {
        return minXDrop;
    }

    public int getMaxXDrop() {
        return maxXDrop;
    }

    private int minZDrop = -1000;
    private int maxZDrop = 1000;

    private int _getMinZDrop() {
        return config.getInt("random_air_drops.minimum_z");
    }

    private int _getMaxZDrop() {
        return config.getInt("random_air_drops.maximum_z");
    }

    public int getMinZDrop() {
        return minZDrop;
    }

    public int getMaxZDrop() {
        return maxZDrop;
    }

    private int dropInteveral = 3600;

    private int _getDropInterval() {
        return config.getInt("random_air_drops.interval_in_seconds");
    }

    public int getDropInteveral() {
        return dropInteveral;
    }

    private List<String> worldNames;

    private List<String> _getWorldNames() {
        return config.getStringList("random_air_drops.worlds");
    }

    public List<String> getWorldNames() {
        return worldNames;
    }

    private String dropMenuTitle = "Air Drops";

    private String _getDropMenuTitle() {
        return Txt.parseColor(config.getString("drop_menu.title"));
    }

    public String getDropMenuTitle() {
        return dropMenuTitle;
    }

    // LOAD FUNCTIONS

    public List<AirDropPackage> loadAirDropPackages() {
        List<AirDropPackage> list = new ArrayList<>();

        for(String packageName : config.getConfigurationSection("air_drop_packages").getKeys(false)) {
            AirDropPackage pack = new AirDropPackage(packageName);

            ItemStringParser parser = new ItemStringParser(config.getString("air_drop_packages." + packageName + ".display_item"));
            if(parser.parse() != null) {
                pack.setDisplayItem(parser.getLastResult());
            }

            List<ItemStack> drops = new ArrayList<>();
            for(String itemString : config.getStringList("air_drop_packages." + packageName + ".items")) {
                parser.setNewInput(itemString);
                if(parser.parse() != null) {
                    drops.add(parser.getLastResult());
                }
            }

            if(drops.size() > 0) {
                pack.setItems(drops);
            }

            double price = config.getDouble("air_drop_packages." + packageName + ".drop_menu_price");
            if(price > 0) {
                pack.setPrice(price);
            }

            list.add(pack);
        }

        return list;
    }
}