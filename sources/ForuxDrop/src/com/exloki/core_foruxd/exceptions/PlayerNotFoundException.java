package com.exloki.core_foruxd.exceptions;

import com.exloki.foruxd.TL;

public class PlayerNotFoundException extends LException {
    private static final long serialVersionUID = 2L;
    protected static final String DEFAULT_MESSAGE = TL.ER_PLAYER.toString();

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
