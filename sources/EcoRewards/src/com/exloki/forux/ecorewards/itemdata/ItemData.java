package com.exloki.forux.ecorewards.itemdata;

public abstract class ItemData implements IItemData {
    private ItemPart key;

    public ItemPart getDataKey() {
        return key;
    }

    private String[] matchPrefixes;

    public String[] getMatcherPrefix() {
        return matchPrefixes;
    }

    private boolean applyToMeta = false;

    public boolean canApplyToMeta() {
        return applyToMeta;
    }

    protected void setApplyToMeta(boolean newVal) {
        this.applyToMeta = newVal;
    }

    public ItemData(ItemPart key, String[] matchPrefixes) {
        this.key = key;
        this.matchPrefixes = matchPrefixes;
    }
}
