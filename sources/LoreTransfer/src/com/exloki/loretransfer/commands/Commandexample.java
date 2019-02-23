package com.exloki.loretransfer.commands;

import com.exloki.loretransfer.core.commands.LCommand;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;

public class Commandexample extends LCommand implements Listener {

    public Commandexample() {
        super("example", "example.permission");
    }

    @Override
    protected void perform(final String commandLabel, final Command cmd) throws Exception {

    }
}