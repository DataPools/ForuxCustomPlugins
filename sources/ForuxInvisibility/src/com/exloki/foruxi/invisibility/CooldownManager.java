package com.exloki.foruxi.invisibility;

import com.exloki.foruxi.ForuxInvisibility;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    // Map { Player ID -> Next Use in Millis }
    private static Map<UUID, Long> cooldownMap = new HashMap<>();

    public static boolean isCooldowned(Player player) {
        if(cooldownMap.containsKey(player.getUniqueId())) {
            long time = cooldownMap.get(player.getUniqueId());
            if(System.currentTimeMillis() < time) {
                return true;
            }
        }

        return false;
    }

    public static void cooldown(Player player) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + (ForuxInvisibility.getSettings().getCooldownDurationInSeconds() * 1000));
    }

    public static long getTimeout(Player player) {
        return cooldownMap.get(player.getUniqueId());
    }
}
