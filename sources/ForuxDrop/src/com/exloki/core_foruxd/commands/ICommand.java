package com.exloki.core_foruxd.commands;

import com.exloki.core_foruxd.LPlugin;
import com.exloki.core_foruxd.utils.StringPair;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {
    String getName();

    void run(Server server, CommandSender sender, String commandLabel, Command cmd, String[] args)
            throws Exception;

    void setPlugin(final LPlugin plugin);

    String getDescription();

    String getUsage();

    public boolean hasHelpMessages();

    public void addHelpMessage(String string);

    List<StringPair> getHelpMessages();
}
