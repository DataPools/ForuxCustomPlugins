package com.exloki.forux.ecorewards.core.commands;

import com.exloki.forux.ecorewards.core.LPlugin;
import com.exloki.forux.ecorewards.core.utils.Pair;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {
    String getName();

    String getPermission();

    void run(Server server, CommandSender sender, String commandLabel, Command cmd, String[] args)
            throws Exception;

    void setPlugin(final LPlugin plugin);

    String getDescription();

    String getUsage();

    boolean hasHelpMessages();

    List<Pair<String, String>> getHelpMessages();
}
