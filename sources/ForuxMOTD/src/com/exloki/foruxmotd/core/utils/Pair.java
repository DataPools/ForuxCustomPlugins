package com.exloki.foruxmotd.core.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class Pair<L, R> {
    private L zero;
    private R one;

    public Pair() {
    }

    private Pair(L zero, R one) {
        this.zero = zero;
        this.one = one;
    }

    public static <L, R> Pair<L, R> of(L zero, R one) {
        return new Pair<>(zero, one);
    }

    public boolean isZeroSet() {
        return this.zero != null;
    }

    public boolean isOneSet() {
        return this.one != null;
    }
}
