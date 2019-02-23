package com.exloki.foruxmotd.model;

import com.exloki.foruxmotd.ForuxMOTD;
import com.exloki.foruxmotd.core.Reloadable;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MotdManager implements Reloadable, Listener {
    private ForuxMOTD plugin;
    private List<String> motd;

    public MotdManager(ForuxMOTD plugin) {
        this.plugin = plugin;
        this.plugin.registerReloadable(this);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
    }

    @Override
    public void reload() {
        motd = new ArrayList<>();
        Path path = plugin.getDataFolder().toPath().resolve("motd.txt");
        try {
            if(!Files.exists(path)) {
                List<String> lines = Arrays.asList("&4Welcome to this MOTD",
                        "&6You should change it in ./plugins/ForuxMOTD/motd.txt",
                        "&7&oor you could leave it like this...");
                Files.write(path, lines, Charset.defaultCharset(), StandardOpenOption.CREATE_NEW);
                for (String str : lines) {
                    motd.add(ChatColor.translateAlternateColorCodes('&', str));
                }
                return;
            }

            List<String> lines = Files.readAllLines(path, Charset.defaultCharset());
            for (String str : lines) {
                motd.add(ChatColor.translateAlternateColorCodes('&', str));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendMotd(CommandSender player) {
        for (String line : motd) {
            player.sendMessage(line.replaceAll("<player>", player.getName()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                sendMotd(player);
            }
        }.runTaskLater(plugin, plugin.getSettings().getMotdDelayInSeconds() * 20);
    }
}
