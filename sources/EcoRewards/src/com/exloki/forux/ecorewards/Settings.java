package com.exloki.forux.ecorewards;

import com.exloki.forux.ecorewards.core.Reloadable;
import com.exloki.forux.ecorewards.core.config.LokiConfig;
import com.exloki.forux.ecorewards.core.utils.Txt;
import com.exloki.forux.ecorewards.core.utils.Util;
import com.exloki.forux.ecorewards.itemdata.ItemStringParser;
import com.exloki.forux.ecorewards.managers.TierManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Settings implements Reloadable {
    private final transient LokiConfig config;
    private final transient EcoRewards pl;

    Settings(EcoRewards pl) {
        this.pl = pl;
        this.pl.registerReloadable(this);

        config = new LokiConfig(new File(pl.getDataFolder(), "config.yml"));
        config.setTemplateName("/config.yml", EcoRewards.class);
        reload();
    }

    public void reload() {
        config.load();
        prefixMessage = _getPrefixMessage();
        guiTitle = _getGuiTitle();
    }

    @Getter @Setter
    private String prefixMessage = "";

    private String _getPrefixMessage() {
        return config.getString("prefix", "[PREFIX] ");
    }

    @Getter
    private String guiTitle = "";

    private String _getGuiTitle() {
        return Txt.parseColor(config.getString("gui-title"));
    }

    // Loading

    public Map<Double, TierManager.TierData> loadTierDataMap() {
        Map<Double, TierManager.TierData> map = new TreeMap<>();

        for(String tierAmountStr : config.getConfigurationSection("tiers").getKeys(false)) {
            double tierAmount = Util.getDouble(tierAmountStr, -1);

            if(tierAmount > -1) {
                if(map.containsKey(tierAmount)) {
                    pl.log("Duplicate tier amount found for '" + tierAmount + "'! Disregarding duplicate information set(s)...");
                    continue;
                }

                String grantedMessage = config.getString("tiers." + tierAmountStr + ".granted-message");
                String revokedMessage = config.getString("tiers." + tierAmountStr + ".revoked-message");
                List<String> permissions = config.getStringList("tiers." + tierAmountStr + ".permissions");

                ItemStringParser parser = new ItemStringParser(pl, config.getString("tiers." + tierAmountStr + ".display-item-data"));
                ItemStack stack = parser.parse();
                if(stack == null) {
                    pl.log("Unable to load display item for tier '" + tierAmountStr + "' - reverting to Stone block!");
                    stack = new ItemStack(Material.STONE);
                }

                map.put(tierAmount, new TierManager.TierData(tierAmount, grantedMessage, revokedMessage, stack, permissions));
            }
        }

        return map;
    }
}