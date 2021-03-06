package com.exloki.foruxmotd.core.transform;

public class DoubleStringTransformer extends Transformer<Double, String> {

    private static DoubleStringTransformer instance;
    static { new DoubleStringTransformer(); }
    public static DoubleStringTransformer get() { return instance; }

    public DoubleStringTransformer() {
        instance = this;
    }

    @Override
    public String transform(Double in) {
        return String.valueOf(in);
    }
}
