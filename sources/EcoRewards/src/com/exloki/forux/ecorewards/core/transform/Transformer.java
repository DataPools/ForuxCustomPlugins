package com.exloki.forux.ecorewards.core.transform;

public abstract class Transformer<T, R> {
    public abstract R transform(T in);
}
