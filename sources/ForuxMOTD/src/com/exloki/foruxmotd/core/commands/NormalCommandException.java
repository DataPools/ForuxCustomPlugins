package com.exloki.foruxmotd.core.commands;

import com.exloki.foruxmotd.Msg;

public final class NormalCommandException extends CommandException implements FriendlyException {
    public NormalCommandException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_ERROR.with(getMessage());
    }
}
