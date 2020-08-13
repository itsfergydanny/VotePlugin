package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.utils.Item;
import com.dnyferguson.voteplugin.voteshop.Shop;
import com.dnyferguson.voteplugin.voteshop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class VoteShopCommand implements CommandExecutor {
    private VotePlugin plugin;
    private boolean enabled;
    private int balanceItemSlot = -1;

    public VoteShopCommand(VotePlugin plugin) {
        this.plugin = plugin;
        enabled = plugin.getConfig().getBoolean("voteShop.enabled");
        balanceItemSlot = plugin.getConfig().getInt("voteShop.balanceItemSlot");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!enabled) {
            sender.sendMessage(Chat.format("&cThe Vote Shop is closed."));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.format("&cOnly players can do this."));
            return true;
        }

        if (plugin.getShop() == null) {
            sender.sendMessage(Chat.format("&cThe shop isnt loaded."));
            return true;
        }

        Player player = (Player) sender;

        Shop shop = plugin.getShop();
        Inventory inv = Bukkit.createInventory(null, shop.getInvSize(), shop.getInvName());

        for (ShopItem shopItem : plugin.getShopItems().values()) {
            inv.setItem(shopItem.getSlot(), shopItem.getDisplayItem());
        }

        if (balanceItemSlot != -1) {
            List<String> lore = new ArrayList<>();
            lore.add("&eYou have &6" + plugin.getVoteTokens().getOrDefault(player.getUniqueId(), 0));
            lore.add("&eVote Tokens");
            inv.setItem(balanceItemSlot, Item.createGuiItem("&aBalance", lore, Material.BOOK, 1, 0));
        }

        if (shop.isFill()) {
            Item.populateEmptySlots(inv, shop.getFillItem());
        }

        player.openInventory(inv);

        return true;
    }
}
