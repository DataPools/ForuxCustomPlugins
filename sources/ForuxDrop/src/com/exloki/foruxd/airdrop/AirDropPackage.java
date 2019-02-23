package com.exloki.foruxd.airdrop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AirDropPackage {

    private String name;
    private double price;

    private ItemStack displayItem = new ItemStack(Material.STONE);
    private List<ItemStack> items = new ArrayList<>();

    public AirDropPackage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
