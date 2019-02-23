package com.exloki.forux.ecorewards.core.utils;

public class Pair<T, R> {
    private T zero;
    private R one;

    public Pair(T zero, R one) {
        this.zero = zero;
        this.one = one;
    }

    public void setZero(T zero) {
        this.zero = zero;
    }

    public void setOne(R one) {
        this.one = one;
    }

    public boolean isZeroSet() {
        return this.zero != null;
    }

    public boolean isOneSet() {
        return this.one != null;
    }

    public T getZero() {
        return zero;
    }

    public R getOne() {
        return one;
    }
}
