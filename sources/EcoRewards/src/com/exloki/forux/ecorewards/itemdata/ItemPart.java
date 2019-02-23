package com.exloki.forux.ecorewards.itemdata;

public enum ItemPart {
    TYPE(ItemType.get()),
    AMOUNT(ItemAmount.get()),
    DAMAGE(ItemDamage.get()),
    ENCHANTS(ItemEnchants.get()),
    NAME(ItemName.get()),
    LORES(ItemLore.get()),
    COLOURS(ItemColour.get())

    // End
    ;

    private ItemData attached;
    private String[] prefixes;

    ItemPart(ItemData attached) {
        this.attached = attached;
        this.prefixes = attached.getMatcherPrefix();
    }

    public ItemData getAttachedData() {
        return this.attached;
    }

    public String[] getPrefixes() {
        return this.prefixes;
    }

    public static ItemPart getPartFromPrefix(String input) {
        if (input.length() < 2) return null;

        for (ItemPart part : ItemPart.values()) {
            for (String prefix : part.getPrefixes()) {
                if (prefix.endsWith(".")) {
                    if (input.equalsIgnoreCase(prefix.substring(0, prefix.length() - 1)))
                        return part;
                } else if (input.equalsIgnoreCase(prefix))
                    return part;
            }
        }

        return null;
    }
}
