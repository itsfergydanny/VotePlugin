package com.dnyferguson.voteplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {
    public static ItemStack createGuiItem(String name, List<String> lore, Material material, int amount, int data) {
        ItemStack i = new ItemStack(material, 1, (short) data);
        ItemMeta iMeta = i.getItemMeta();
        iMeta.setDisplayName(color(name));
        List<String> newLore = new ArrayList<>();
        for (String line : lore) {
            newLore.add(color(line));
        }
        iMeta.setLore(newLore);
        i.setItemMeta(iMeta);
        i.setAmount(amount);
        return i;
    }

    public static void populateEmptySlots(Inventory menu, ItemStack i) {
        for(int slot = 0; slot < menu.getSize(); slot++) {
            if(menu.getItem(slot) == null) {
                menu.setItem(slot, i);
            }
        }
    }


    private static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}