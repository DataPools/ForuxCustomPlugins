package com.exloki.foruxd.hooks;

import com.massivecraft.factions.engine.EngineMain;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsHook {
    public static MPlayer getMPlayer(Player player) {
        return MPlayer.get(player);
    }

    public static String getFactionId(Player player) {
        MPlayer p1 = getMPlayer(player);
        return p1.hasFaction() ? p1.getFactionId() : "";
    }

    public static String getFactionId(Location location) {
        Faction found = BoardColl.get().getFactionAt(PS.valueOf(location));
        return found != null ? found.getId() : "";
    }

    public static boolean isInOwnTerritory(Player player) {
        MPlayer p1 = getMPlayer(player);
        return p1.isInOwnTerritory();
    }

    public static boolean isInNoTerritory(Player player) {
        Faction at = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
        if (at == null) return true;

        return at.isNone();
    }

    public static boolean isInFactionTerritory(Location location) {
        Faction at = BoardColl.get().getFactionAt(PS.valueOf(location));
        return at != null && !at.isNone();
    }

    public static boolean playerCanBuild(Player player, Location location) {
        return EngineMain.canPlayerBuildAt(player, PS.valueOf(location), false);
    }
}