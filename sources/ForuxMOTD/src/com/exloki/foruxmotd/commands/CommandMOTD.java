package com.exloki.foruxmotd.commands;

import com.exloki.foruxmotd.core.commands.CommandException;
import com.exloki.foruxmotd.core.commands.CommandMeta;
import com.exloki.foruxmotd.core.commands.CommandPermission;
import com.exloki.foruxmotd.core.commands.CoreCommand;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "MOTD command", aliases = {"messageoftheday"}, usage = "/motd")
@CommandPermission("foruxmotd.use")
public class CommandMOTD extends CoreCommand {

    public CommandMOTD() {
        super("motd");

        registerSubCommand(new MOTDSubReload());
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        getPlugin().getMotdManager().sendMotd(sender);
    }
}