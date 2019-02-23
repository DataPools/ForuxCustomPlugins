package com.exloki.balancedfly.core.commands;

import com.exloki.balancedfly.Msg;

public final class PlayerNotFoundException extends CommandException implements FriendlyException {
    public PlayerNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_ERROR.with("No player found!");
    }
}
