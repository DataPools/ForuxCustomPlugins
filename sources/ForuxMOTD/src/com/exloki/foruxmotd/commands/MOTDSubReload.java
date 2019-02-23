package com.exloki.foruxmotd.commands;

import com.exloki.foruxmotd.core.commands.CommandException;
import com.exloki.foruxmotd.core.commands.CommandMeta;
import com.exloki.foruxmotd.core.commands.CommandPermission;
import com.exloki.foruxmotd.core.commands.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "MOTD reload command")
@CommandPermission("foruxmotd.reload")
public class MOTDSubReload extends CoreCommand {

    public MOTDSubReload() {
        super("reload");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        getPlugin().reload();
        sender.sendMessage(ChatColor.GREEN + "Reload successful");
    }
}