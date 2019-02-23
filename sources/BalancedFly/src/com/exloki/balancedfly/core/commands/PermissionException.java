package com.exloki.balancedfly.core.commands;

import com.exloki.balancedfly.Msg;

public final class PermissionException extends CommandException implements FriendlyException {
    public PermissionException(String message) {
        super(message);
    }

    @Override
    public String getFriendlyMessage(CoreCommand command) {
        return Msg.ER_PERMS.get();
    }
}
