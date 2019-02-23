package com.exloki.forux.ecorewards.core.commands;

import com.exloki.forux.ecorewards.Msg;

public class PermissionsException extends LException {
    private static final long serialVersionUID = 2L;
    protected static final String DEFAULT_MESSAGE = Msg.ER_PERMS.toString();

    public PermissionsException() {
        super(DEFAULT_MESSAGE);
    }

    public PermissionsException(final Throwable throwable) {
        super(DEFAULT_MESSAGE, throwable);
    }

    public PermissionsException(final String message) {
        super(message);
    }

    public PermissionsException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}