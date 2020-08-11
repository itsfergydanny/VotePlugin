package com.dnyferguson.voteplugin.voteshop;

import org.bukkit.inventory.ItemStack;

public class Shop {
    private int invSize;
    private String invName;
    private boolean fill;
    private ItemStack fillItem;

    public Shop(int invSize, String invName, boolean fill, ItemStack fillItem) {
        this.invSize = invSize;
        this.invName = invName;
        this.fill = fill;
        this.fillItem = fillItem;
    }

    public int getInvSize() {
        return invSize;
    }

    public void setInvSize(int invSize) {
        this.invSize = invSize;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public ItemStack getFillItem() {
        return fillItem;
    }

    public void setFillItem(ItemStack fillItem) {
        this.fillItem = fillItem;
    }
}
