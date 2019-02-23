package com.exloki.foruxd.commands;

import com.exloki.core_foruxd.commands.LCommand;
import com.exloki.foruxd.ForuxDrop;
import com.exloki.foruxd.TL;
import com.exloki.foruxd.gui.GUICreator;
import com.exloki.foruxd.gui.GUIListener;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class Commanddrop extends LCommand implements Listener {

    public Commanddrop() {
        super("drop");
    }

    @Override
    protected void perform(final String commandLabel, final Command cmd) throws Exception {
        if(argSet(0) && argStr(0).equalsIgnoreCase("reload")) {
            if(!player.hasPermission("foruxdrop.reload")) {
                msg(TL.ER_PERMS.toString());
                return;
            }

            ForuxDrop.i.reload();
            msg(TL.SUCCESS + "Reload successful");
            return;
        }

        if(argSet(0) && argStr(0).equalsIgnoreCase("force")) {
            if(!player.hasPermission("foruxdrop.force")) {
                msg(TL.ER_PERMS.toString());
                return;
            }

            ForuxDrop.getAirDropManager().callRandomAirDrop(0);
            msg(TL.SUCCESS + "Forced random air drop successfully");
            return;
        }

        // Open GUI for player
        player.openInventory(GUICreator.getCurrentInventory());

        // Tag player for tracking
        player.setMetadata(GUIListener.TRACKING_META, new FixedMetadataValue(ForuxDrop.i, 1));

        // Message
        msg(TL.OPENING_GUI.toString());
    }
}