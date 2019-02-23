package com.exloki.forux.ecorewards.core.transform;

public class StringDoubleTransformer extends Transformer<String, Double> {

    private static StringDoubleTransformer instance;
    static { new StringDoubleTransformer(); }
    public static StringDoubleTransformer get() { return instance; }

    public StringDoubleTransformer() {
        instance = this;
    }

    @Override
    public Double transform(String in) {
        try {
            return Double.parseDouble(in);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
