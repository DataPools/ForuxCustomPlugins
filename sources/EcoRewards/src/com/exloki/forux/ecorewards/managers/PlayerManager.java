package com.exloki.forux.ecorewards.managers;

import com.exloki.forux.ecorewards.EcoRewards;
import com.exloki.forux.ecorewards.core.Reloadable;
import com.exloki.forux.ecorewards.core.transform.DoubleStringTransformer;
import com.exloki.forux.ecorewards.core.transform.StringDoubleTransformer;
import com.exloki.forux.ecorewards.core.utils.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PlayerManager implements Reloadable {

    private static final String PLAYER_DATA_FOLDER = "player_data";

    private EcoRewards plugin;

    private Path playerDataFolder;
    private Map<UUID, PlayerData> playerDataMap;

    public PlayerManager(EcoRewards plugin) {
        this.plugin = plugin;
        this.plugin.registerReloadable(this);

        this.playerDataFolder = plugin.getDataFolder().toPath().resolve(PLAYER_DATA_FOLDER);
        reload();
    }

    @Override @SneakyThrows
    public void reload() {
        this.playerDataMap = new HashMap<>();

        if(!Files.exists(playerDataFolder)) {
            Files.createDirectory(playerDataFolder);
            return;
        }

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(playerDataFolder)) {
            for (Path path : directoryStream) {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.load(path.toFile());

                UUID playerId = UUID.fromString(yaml.getString("uuid"));

                PlayerData data = new PlayerData();
                data.setLastRewardTier(yaml.getDouble("last-reward-tier"));
                data.setRewardedTiers(Util.transformList(yaml.getStringList("rewarded-tiers"), StringDoubleTransformer.get()));

                this.playerDataMap.put(playerId, data);
            }
        }
    }

    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId());
    }

    public void savePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        if(data != null) {
            savePlayerData(player.getUniqueId(), data);
        }
    }

    public void savePlayerData(Player player, PlayerData data) {
        if(data != null) {
            playerDataMap.put(player.getUniqueId(), data);
            savePlayerData(player.getUniqueId(), data);
        }
    }

    @SneakyThrows
    private void savePlayerData(UUID owner, PlayerData data) {
        Path savePath = playerDataFolder.resolve(owner.toString() + ".yml");
        if(Files.exists(savePath)) {
            Files.delete(savePath);
        }

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("uuid", owner.toString());
        yaml.set("last-reward-tier", data.getLastRewardTier());
        yaml.set("rewarded-tiers", Util.transformList(data.getRewardedTiers(), DoubleStringTransformer.get()));

        Files.createFile(savePath);
        yaml.save(savePath.toFile());
    }

    @Getter @Setter
    public static class PlayerData {
        private List<Double> rewardedTiers;
        private double lastRewardTier = 0;
        private transient double lastKnownBalance = 0;

        public PlayerData() {
            this.rewardedTiers = new ArrayList<>();
        }
    }
}
