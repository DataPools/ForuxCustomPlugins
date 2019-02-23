package com.exloki.foruxi.commands;

import com.exloki.foruxi.ForuxInvisibility;
import com.exloki.foruxi.invisibility.CooldownManager;
import com.exloki.foruxi.Msg;
import com.exloki.foruxi.core.commands.LCommand;
import com.exloki.foruxi.utils.TimeUtils;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;

public class Commandinvisible extends LCommand implements Listener {

    public Commandinvisible() {
        super("invisible", "foruxinvisibility.use");
    }

    @Override
    protected void perform(final String commandLabel, final Command cmd) throws Exception {
        if(CooldownManager.isCooldowned(player)) {
            msg(Msg.COOLDOWN_IN_EFFECT.withVars(TimeUtils.getTimeRemaining(CooldownManager.getTimeout(player))));
            return;
        }

        if(ForuxInvisibility.i.getInvisibilityManager().isActivated(player)) {
            ForuxInvisibility.i.getInvisibilityManager().deactivate(player, false, false);
            msg(Msg.INVISIBILITY_REMOVED);
            return;
        }

        ForuxInvisibility.i.getInvisibilityManager().activate(player);
        msg(Msg.INVISIBILITY_APPLIED);
    }
}