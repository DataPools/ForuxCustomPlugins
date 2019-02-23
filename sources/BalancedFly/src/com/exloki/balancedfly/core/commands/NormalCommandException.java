package com.exloki.balancedfly.core.commands;

import com.exloki.balancedfly.Msg;

public final class NormalCommandException extends CommandException implements FriendlyException {
    public NormalCommandException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_ERROR.with(getMessage());
    }
}
