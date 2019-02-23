package com.exloki.forux.ecorewards.managers;

import com.exloki.forux.ecorewards.EcoRewards;
import com.exloki.forux.ecorewards.core.Reloadable;
import com.exloki.forux.ecorewards.core.utils.Txt;
import com.exloki.forux.ecorewards.hooks.VaultHook;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TierManager implements Reloadable {

    private EcoRewards plugin;

    private Map<Double, TierData> tierDataMap;

    public TierManager(EcoRewards plugin) {
        this.plugin = plugin;
        this.plugin.registerReloadable(this);
        reload();
    }

    public TierData getTierData(double key) {
        return tierDataMap.get(key);
    }

    public TierDataResult findTierData(double previousBalance, double newBalance) {
        List<TierData> list = new ArrayList<>();
        double minValue = Math.min(previousBalance, newBalance);
        double maxValue = Math.max(previousBalance, newBalance);
        boolean positive = minValue == previousBalance;

        for(Map.Entry<Double, TierData> entry : tierDataMap.entrySet()) {
            if(entry.getKey() > minValue && entry.getKey() <= maxValue) {
                list.add(entry.getValue());
            }
        }

        return new TierDataResult(list, positive);
    }

    public TierData findNextTierData(double amount, boolean positive) {
        TierData closest = null;
        for(Map.Entry<Double, TierData> entry : tierDataMap.entrySet()) {
            if(positive) {
                if(entry.getKey() > amount) {
                    if(closest == null || closest.getAmount() > entry.getKey()) {
                        closest = entry.getValue();
                    }
                }
            } else {
                if(entry.getKey() < amount) {
                    if(closest == null || closest.getAmount() < entry.getKey()) {
                        closest = entry.getValue();
                    }
                }
            }
        }

        return closest;
    }

    public ImmutableMap<Double, TierData> getTierDataMap() {
        return ImmutableMap.copyOf(tierDataMap);
    }

    @Override
    public void reload() {
        this.tierDataMap = plugin.getSettings().loadTierDataMap();
    }

    @Getter
    public static class TierDataResult {
        private List<TierData> foundData;
        private boolean positive;

        public TierDataResult(List<TierData> foundData, boolean positive) {
            this.foundData = foundData;
            this.positive = positive;
        }
    }

    @Getter
    public static class TierData {
        private Double amount;
        private String grantedMessage;
        private String revokedMessage;

        private ItemStack displayItem;
        private List<String> permissions;

        public TierData(Double amount, String grantedMessage, String revokedMessage, ItemStack displayItem, List<String> permissions) {
            this.amount = amount;
            this.grantedMessage = Txt.parseColor(grantedMessage);
            this.revokedMessage = Txt.parseColor(revokedMessage);
            this.displayItem = displayItem;
            this.permissions = permissions;
        }

        public void grant(Player player) {
            for(String permission : permissions) {
                VaultHook.getPermissions().playerAdd(null, player, permission);
            }

            player.sendMessage(grantedMessage);
        }

        public void revoke(Player player) {
            for(String permission : permissions) {
                VaultHook.getPermissions().playerRemove(null, player, permission);
            }

            player.sendMessage(revokedMessage);
        }
    }
}
