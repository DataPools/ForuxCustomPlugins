package com.exloki.forux.ecorewards.commands;

import com.exloki.forux.ecorewards.EcoRewards;
import com.exloki.forux.ecorewards.Msg;
import com.exloki.forux.ecorewards.core.commands.LCommand;
import com.exloki.forux.ecorewards.managers.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class Commandecorewards extends LCommand implements Listener {

    public Commandecorewards() {
        super("ecorewards", "ecorewards.use");
    }

    @Override
    protected void perform(final String commandLabel, final Command cmd) throws Exception {
        if(argSet(0) && arg(0).equalsIgnoreCase("reload") && sender.hasPermission("ecorewards.reload")) {
            plugin.reload();
            msg(Msg.SUCCESS + "Reload successful!");
            return;
        }

        player.setMetadata(GUIManager.IN_USE_META, new FixedMetadataValue(plugin, 1));
        player.openInventory(GUIManager.getCurrentInventory((EcoRewards)plugin));
        msg(Msg.SUCCESS + "Opening interface...");
    }
}