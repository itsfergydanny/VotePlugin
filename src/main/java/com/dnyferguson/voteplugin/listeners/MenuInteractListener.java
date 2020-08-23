package com.dnyferguson.voteplugin.listeners;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.voteshop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class MenuInteractListener implements Listener {

    private final VotePlugin plugin;
    private final List<String> inventoriesToHandle = new ArrayList<>();

    public MenuInteractListener(VotePlugin plugin) {
        this.plugin = plugin;
        inventoriesToHandle.add(plugin.getShop().getInvName());
    }

    @EventHandler
    public void onPlayerInteractWithJobMenu(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();

        String invName = e.getInventory().getName();
        if (!inventoriesToHandle.contains(invName)) {
            return;
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        if (!clickedItem.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clickedItem.getItemMeta();

        if (!meta.hasDisplayName()) {
            return;
        }

        if (plugin.getShopItems().containsKey(meta.getDisplayName())) {
            ShopItem shopItem = plugin.getShopItems().get(meta.getDisplayName());
            int userTokens = plugin.getVoteTokens().getOrDefault(player.getUniqueId(), 0);
            int cost = shopItem.getCost();
            if (cost > userTokens) {
                player.sendMessage(Chat.format("&cYou do not have enough for this. You are missing " + (cost - userTokens) + " Vote Tokens. Vote now for more tokens (/vote)"));
                player.closeInventory();
                return;
            }

            int newBalance = userTokens - cost;
            plugin.getVoteTokens().put(player.getUniqueId(), newBalance);

            for (String command : shopItem.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()).replace("%player_uui%", player.getUniqueId().toString()));
            }

            plugin.getSql().getResultAsync("SELECT * FROM `tokens` WHERE `uuid` = '" + player.getUniqueId() + "'", new FindResultCallback() {
                @Override
                public void onQueryDone(ResultSet result) throws SQLException {
                    if (result.next()) {
                        plugin.getSql().executeStatementAsync("UPDATE `tokens` SET `tokens`='" + newBalance+ "' WHERE `uuid` = '" + player.getUniqueId() + "'");
                    }
                }
            });

            player.closeInventory();
            player.sendMessage(Chat.format("&aYou have purchased: &f" + shopItem.getName() + "&a for " + cost + " vote tokens. Your new balance is " + newBalance + " tokens."));
            plugin.getLogger().log(Level.INFO, "Player " + player.getName() + " has purchased " + shopItem.getName() + "&a for " + cost + " vote tokens. Their new balance is " + newBalance + " tokens.");
        }
    }
}
