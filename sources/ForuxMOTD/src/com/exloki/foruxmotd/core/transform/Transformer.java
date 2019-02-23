package com.exloki.foruxmotd.core.transform;

public abstract class Transformer<T, R> {
    public abstract R transform(T in);
}
