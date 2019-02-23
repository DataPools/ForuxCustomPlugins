package com.exloki.foruxi.core.exceptions;

import com.exloki.foruxi.Msg;

public class PlayerNotFoundException extends LException {
    private static final long serialVersionUID = 2L;
    protected static final String DEFAULT_MESSAGE = Msg.ER_PLAYER.toString();

    public PlayerNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public PlayerNotFoundException(final Throwable throwable) {
        super(DEFAULT_MESSAGE, throwable);
    }

    public PlayerNotFoundException(final String message) {
        super(message);
    }

    public PlayerNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
