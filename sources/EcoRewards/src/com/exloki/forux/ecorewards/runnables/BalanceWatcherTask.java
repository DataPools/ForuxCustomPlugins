package com.exloki.forux.ecorewards.runnables;

import com.exloki.forux.ecorewards.EcoRewards;
import com.exloki.forux.ecorewards.hooks.VaultHook;
import com.exloki.forux.ecorewards.managers.PlayerManager;
import com.exloki.forux.ecorewards.managers.TierManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BalanceWatcherTask extends BukkitRunnable {

    private EcoRewards plugin;
    private Player host;

    public BalanceWatcherTask(EcoRewards plugin, Player player) {
        this.plugin = plugin;
        this.host = player;
    }

    @Override
    public void run() {
        if(host == null || !host.isOnline()) {
            this.plugin.getTaskManager().unregisterTask(host.getUniqueId());
            this.cancel();
            return;
        }

        PlayerManager.PlayerData data = plugin.getPlayerManager().getPlayerData(host);
        if(data == null) {
            data = new PlayerManager.PlayerData();
        }

        double currentBalance = VaultHook.getEconomy().getBalance(host);
        if(currentBalance == data.getLastKnownBalance()) {
            return; // No changes
        }

        TierManager.TierDataResult result = plugin.getTierManager().findTierData(data.getLastKnownBalance(), currentBalance);

        if(result.getFoundData().size() > 0) {
            double lowestTier = Double.MAX_VALUE;
            double highestTier = 0;
            for(TierManager.TierData foundData : result.getFoundData()) {
                if(result.isPositive()) {
                    foundData.grant(host);
                    data.getRewardedTiers().add(foundData.getAmount());
                } else {
                    foundData.revoke(host);
                    data.getRewardedTiers().remove(foundData.getAmount());
                }

                if(foundData.getAmount() > highestTier) {
                    highestTier = foundData.getAmount();
                }
                if(foundData.getAmount() < lowestTier) {
                    lowestTier = foundData.getAmount();
                }
            }

            if(result.isPositive()) {
                data.setLastRewardTier(highestTier);
            } else {
                TierManager.TierData nextTier = plugin.getTierManager().findNextTierData(lowestTier, false);
                data.setLastRewardTier(nextTier == null ? 0 : nextTier.getAmount());
            }
        }

        data.setLastKnownBalance(currentBalance);
        plugin.getPlayerManager().savePlayerData(host, data);
    }
}
