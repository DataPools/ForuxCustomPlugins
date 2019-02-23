package com.exloki.foruxe;

import com.exloki.core_foruxe.LPlugin;
import com.exloki.foruxe.listeners.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;

public class ForuxEffects extends LPlugin
{
    public static ForuxEffects i;
    private static transient Settings settings;

	/*
	 * CONSTRUCTION / ENABLE / DISABLE
	 */

    public ForuxEffects() {
        super("[Forux Effects] ", "foruxeffects", ForuxEffects.class.getPackage().getName() + ".commands.Command");
        i = this;
    }

    @Override
    public void onEnable() {
        if (!preEnable())
            return;

        logIfDebug("Enabling plugin...", false);

        settings = new Settings(this);
        messagePrefix = settings.getPrefixMessage();

        registerListeners(pm);

        startMainLoop();

        log(String.format("%s enabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        logIfDebug("Disabling plugin...", false);

        stopMainLoop();

        log(String.format("%s disabled (version %s) - Author: Exloki", this.getDescription().getName(), this.getDescription().getVersion()));
    }

	/*
	 * LISTENERS
	 */

    private void registerListeners(PluginManager pm) {
        HandlerList.unregisterAll(this);

        logIfDebug("Registering listeners", false);

        final PlayerListener playerListener = new PlayerListener();
        pm.registerEvents(playerListener, this);

        logIfDebug("All listeners successfully registered", false);
    }

    /*
     * MAIN FEATURES
     */

    public void applyEffects(Player player) {
        if(player.isOp()) return;
        Map<String, List<PotionEffect>> effectsMap = settings.getPotionEffectsMap();

        for(Map.Entry<String, List<PotionEffect>> entry : effectsMap.entrySet()) {
            if(player.hasPermission(entry.getKey())) {
                for(PotionEffect effect : entry.getValue()) {
                    if(!player.hasPotionEffect(effect.getType())) {
                        player.addPotionEffect(effect);
                    }
                }
            }
        }
    }

    public void removeEffects(Player player) {
        if(player.isOp()) return;
        Map<String, List<PotionEffect>> effectsMap = settings.getPotionEffectsMap();

        for(Map.Entry<String, List<PotionEffect>> entry : effectsMap.entrySet()) {
            for(PotionEffect effect : entry.getValue()) {
                for(PotionEffect activeEffect : player.getActivePotionEffects()) {
                    if(activeEffect.getType() == effect.getType() && activeEffect.getDuration() > 9600) { // 9600 ticks = 8m, longest potion duration
                        player.removePotionEffect(effect.getType());
                    }
                }
            }
        }
    }

    private BukkitTask mainLoopTask;

    private void startMainLoop() {
        mainLoopTask = new BukkitRunnable() {
            public void run() {
                for(Player player : getServer().getOnlinePlayers()) {
                    if(ForuxEffects.getSettings().getDisabledWorlds().contains(player.getWorld().getName())) {
                        continue;
                    }
                    removeEffects(player);
                    applyEffects(player);
                }
            }
        }.runTaskTimer(this, 100L, 200L);
    }

    private void stopMainLoop() {
        mainLoopTask.cancel();
    }

	/*
	 * UTILS / GETTERS
	 */

    @Override
    public void reload() {
        super.reload();

        settings.reloadConfig();
    }

    public static Settings getSettings()
    {
        return settings;
    }
}
