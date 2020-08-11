package com.dnyferguson.voteplugin;

import com.dnyferguson.momentousercache.MomentoUserCache;
import com.dnyferguson.voteplugin.commands.FakeVoteCommand;
import com.dnyferguson.voteplugin.commands.VoteCommand;
import com.dnyferguson.voteplugin.commands.VotePartyCommand;
import com.dnyferguson.voteplugin.hooks.VotePluginExpansion;
import com.dnyferguson.voteplugin.listeners.MenuInteractListener;
import com.dnyferguson.voteplugin.listeners.PlayerVoteListener;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.mysql.MySQL;
import com.dnyferguson.voteplugin.tasks.CheckLastVotedTask;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.utils.Item;
import com.dnyferguson.voteplugin.voteparty.VotePartyHandler;
import com.dnyferguson.voteplugin.voteshop.Shop;
import com.dnyferguson.voteplugin.voteshop.ShopItem;
import com.dnyferguson.voteplugin.commands.VoteShopCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public final class VotePlugin extends JavaPlugin {
    private MySQL sql;
    private VotePartyHandler votePartyHandler;
    private MomentoUserCache userCache;
    private Map<UUID, Integer> voteTokens = new HashMap<>();
    private Map<String, ShopItem> shopItems = new HashMap<>();
    private Shop shop;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        sql = new MySQL(this);

        sql.getResultAsync("SELECT * FROM `tokens`", new FindResultCallback() {
            @Override
            public void onQueryDone(ResultSet result) throws SQLException {
                while (result.next()) {
                    voteTokens.put(UUID.fromString(result.getString("uuid")), result.getInt("tokens"));
                }
            }
        });

        userCache = (MomentoUserCache) Bukkit.getServer().getPluginManager().getPlugin("MomentoUserCache");

        if (getConfig().getBoolean("voteParty.enabled")) {
            votePartyHandler = new VotePartyHandler(this);
            getCommand("voteparty").setExecutor(new VotePartyCommand(this));
        }

        getCommand("vote").setExecutor(new VoteCommand(this));
        getCommand("fakevote").setExecutor(new FakeVoteCommand());

        loadShop();
        getCommand("voteshop").setExecutor(new VoteShopCommand(this));

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerVoteListener(this), this);
        pm.registerEvents(new MenuInteractListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new VotePluginExpansion(this).register();
        }

        new CheckLastVotedTask(this);
    }

    @Override
    public void onDisable() {
        votePartyHandler.saveConfig(false);
        if (sql != null) {
            sql.close();
        }
    }

    private void loadShop() {
        FileConfiguration config = getConfig();

        // load shop
        try {
            this.shop = new Shop(config.getInt("voteShop.invSize"), Chat.format(config.getString("voteShop.invName")),
                    config.getBoolean("voteShop.fill"), Item.createGuiItem(config.getString("voteShop.fillItem.name"), new ArrayList<>(),
                    Material.valueOf(config.getString("voteShop.fillItem.item")), config.getInt("voteShop.fillItem.amount"),
                    config.getInt("voteShop.fillItem.data")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // load items
        int count = 0;

        ConfigurationSection section = config.getConfigurationSection("voteShop.items");
        for (String key : section.getKeys(false)) {
            try {
                ShopItem shopItem = new ShopItem();
                shopItem.setCost(section.getInt(key + ".cost"));
                shopItem.setCommands(section.getStringList(key + ".commands"));
                shopItem.setSlot(section.getInt(key + ".slot"));

                ConfigurationSection displayItem = section.getConfigurationSection(key + ".displayItem");

//                System.out.println("NAME = " + displayItem.getString("name"));
//                System.out.println("LORE = " + Arrays.toString(displayItem.getStringList("lore").toArray()));
//                System.out.println("MATERIAL = " + displayItem.getString("item"));
//                System.out.println("AMOUNT = " + displayItem.getInt("amount"));
//                System.out.println("DATA = " + displayItem.getInt("data"));

                ItemStack item = Item.createGuiItem(displayItem.getString("name"), displayItem.getStringList("lore"),
                        Material.valueOf(displayItem.getString("item")), displayItem.getInt("amount"), displayItem.getInt("data"));

                shopItem.setName(Chat.format(displayItem.getString("name")));
                shopItem.setDisplayItem(item);
                shopItems.put(Chat.format(displayItem.getString("name")), shopItem);
                count++;
            } catch (Exception ignore) {
                ignore.printStackTrace();
                getLogger().log(Level.WARNING, "Invalid shop item " + key + ". Skipping..");
            }
        }

        getLogger().log(Level.INFO, "Loaded " + count + " shop items.");
    }

    public MySQL getSql() {
        return sql;
    }

    public VotePartyHandler getVotePartyHandler() {
        return votePartyHandler;
    }

    public MomentoUserCache getUserCache() {
        return userCache;
    }

    public Map<UUID, Integer> getVoteTokens() {
        return voteTokens;
    }

    public Map<String, ShopItem> getShopItems() {
        return shopItems;
    }

    public Shop getShop() {
        return shop;
    }
}
