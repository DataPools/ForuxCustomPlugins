package com.exloki.forux.ecorewards.itemdata;

public class DataException extends Exception {
    private static final long serialVersionUID = -9036202694165168984L;
    protected static final String DEFAULT_MESSAGE = "Invalid value passed to ItemData parser!";

    public DataException() {
        super(DEFAULT_MESSAGE);
    }

    public DataException(final Throwable throwable) {
        super(DEFAULT_MESSAGE, throwable);
    }

    public DataException(final String message) {
        super(message);
    }

    public DataException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
