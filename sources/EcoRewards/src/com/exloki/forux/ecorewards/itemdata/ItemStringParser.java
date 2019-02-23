package com.exloki.forux.ecorewards.itemdata;

import com.exloki.forux.ecorewards.EcoRewards;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemStringParser {
    private EcoRewards plugin;

    private boolean parseCalled = false;
    private String input;
    private ItemStack result;
    private boolean errors;

    public ItemStringParser(EcoRewards loggingPlugin, String input) {
        this.plugin = loggingPlugin;
        this.input = input;
    }

    public void setNewInput(String input) {
        this.input = input;
        this.parseCalled = false;
        this.result = null;
    }

    public String getInput() {
        return this.input;
    }

    public ItemStack getLastResult() {
        if (!parseCalled)
            throw new IllegalAccessError("getResult() cannot be called before parse()!");

        return this.result;
    }

    public ItemStack parse() {
        return parse(false);
    }

    public ItemStack parse(boolean deepLog) {
        parseCalled = true;
        errors = false;
        HashMap<ItemPart, List<String>> dataMap = createPartMap(false);
        if (dataMap.isEmpty()) {
            plugin.log("Disregarding invalid item string: '" + input + "'");
            errors = true;
            return null;
        }

        ItemStack stack;
        try {
            if (!dataMap.containsKey(ItemPart.TYPE) || dataMap.get(ItemPart.TYPE).size() < 1)
                throw new DataException("No type (id) can be found!");
            if (dataMap.get(ItemPart.TYPE).size() > 1) throw new DataException("Too many types (ids) were found!");

            stack = ItemPart.TYPE.getAttachedData().applyValue(null, dataMap.get(ItemPart.TYPE).iterator().next());
            if (stack == null) throw new DataException("No type (id) can be found!");
        } catch (DataException ex) {
            plugin.log("Disregarding invalid item string: '" + input + "' - " + ex.getMessage());
            errors = true;
            return null;
        }

        ItemMeta meta = stack.getItemMeta();
        for (ItemPart rel : dataMap.keySet()) {
            ItemData data = rel.getAttachedData();
            for (String value : dataMap.get(rel)) {
                try {
                    if (data.canApplyToMeta())
                        meta = data.applyMetaValue(meta, value);
                    else stack = data.applyValue(stack, value);
                } catch (DataException ex) {
                    plugin.log("Disregarding invalid ItemPart with value '" + value + "' - " + ex.getMessage());
                    errors = true;
                }
            }
        }

        stack.setItemMeta(meta);
        result = stack;
        return stack;
    }

    public boolean encounteredErrors() {
        return errors;
    }

    private static final String DELIMITER = " ", EQUALIZER = ".";

    protected HashMap<ItemPart, List<String>> createPartMap(boolean deepLog) {
        HashMap<ItemPart, List<String>> map = new HashMap<>();
        if (input.isEmpty()) return map;

        if (!input.contains(DELIMITER)) {
            //Possibly 1 input
            if (!input.contains(EQUALIZER) || input.indexOf(EQUALIZER) == input.length()) return map;

            String key = input.substring(0, input.indexOf(EQUALIZER));
            ItemPart part = ItemPart.getPartFromPrefix(key);
            if (part == null) return map;

            String value = input.substring(input.indexOf(EQUALIZER) + 1, input.length());
            List<String> values = new ArrayList<>();
            values.add(value);

            map.put(part, values);
            return map;
        }
        String[] set = input.split(DELIMITER);

        for (String str : set) {
            if (!str.contains(EQUALIZER) || input.indexOf(EQUALIZER) == input.length()) {
                plugin.log("Disregarding invalid ItemPart '" + str + "' as no value can be found");
                errors = true;
                continue;
            }

            String key = str.substring(0, str.indexOf(EQUALIZER));
            ItemPart part = ItemPart.getPartFromPrefix(key);
            if (part == null) {
                if (deepLog)
                    plugin.log("Disregarding invalid ItemPart definition: '" + key + "'");
                continue;
            }

            String value = str.substring(str.indexOf(EQUALIZER) + 1, str.length());

            if (map.containsKey(part)) {
                List<String> values = map.get(part);
                values.add(value);

                map.put(part, values);
            } else {
                List<String> values = new ArrayList<>();
                values.add(value);

                map.put(part, values);
            }
        }

        return map;
    }
}
