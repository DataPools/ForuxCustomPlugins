package com.exloki.foruxmotd.core.commands;

import com.exloki.foruxmotd.Msg;

public final class PermissionException extends CommandException implements FriendlyException {
    public PermissionException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_PERMS.get();
    }
}
