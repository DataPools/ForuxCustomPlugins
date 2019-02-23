package com.exloki.core_foruxd.exceptions;

import org.bukkit.ChatColor;

public class InvalidArgumentsException extends LException {
    private static final long serialVersionUID = -3774506539346010993L;

    protected static final String DEFAULT_MESSAGE = ChatColor.RESET + "Invalid command arguments given!";
    private boolean showHelpMessages = false;

    public InvalidArgumentsException() {
        super("");
    }

    public InvalidArgumentsException(final boolean showHelpMessage) {
        super("");
        this.showHelpMessages = showHelpMessage;
    }

    public InvalidArgumentsException(final Throwable throwable) {
        super(DEFAULT_MESSAGE, throwable);
    }

    public InvalidArgumentsException(final String message) {
        super(ChatColor.RESET + message);
    }

    public InvalidArgumentsException(final String message, final boolean showHelpMessage) {
        super(ChatColor.RESET + message);
        this.showHelpMessages = showHelpMessage;
    }

    public InvalidArgumentsException(final String message, final Throwable throwable) {
        super(ChatColor.RESET + message, throwable);
    }

    public boolean showHelpMessages() {
        return showHelpMessages;
    }
}
