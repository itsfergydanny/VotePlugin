package com.dnyferguson.voteplugin.voteshop;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopItem {
    private int cost;
    private String name;
    private ItemStack displayItem;
    private List<String> commands;
    private int slot;

    public ShopItem() {}

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
