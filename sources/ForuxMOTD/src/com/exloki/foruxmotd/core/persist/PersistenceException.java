package com.exloki.foruxmotd.core.persist;

public class PersistenceException extends Exception {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable ex) {
        super(message, ex);
    }
}
