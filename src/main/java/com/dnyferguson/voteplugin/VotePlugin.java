package com.dnyferguson.voteplugin;

import com.dnyferguson.momentousercache.MomentoUserCache;
import com.dnyferguson.voteplugin.commands.FakeVoteCommand;
import com.dnyferguson.voteplugin.commands.VoteCommand;
import com.dnyferguson.voteplugin.commands.VotePartyCommand;
import com.dnyferguson.voteplugin.hooks.VotePluginExpansion;
import com.dnyferguson.voteplugin.listeners.PlayerVoteListener;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.mysql.MySQL;
import com.dnyferguson.voteplugin.voteparty.VotePartyHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class VotePlugin extends JavaPlugin {
    private MySQL sql;
    private VotePartyHandler votePartyHandler;
    private MomentoUserCache userCache;
    private Map<UUID, Integer> voteTokens = new HashMap<>();

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

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerVoteListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new VotePluginExpansion(this).register();
        }

        /*

        TODO:
            * then u can trade tokens for things in a GUI (/voteshop)
            * Add lastvoted table and remind people as soon as they can vote again per link
            * Replace whatever momento used for voteminders for this
            * replace momento voteparty + vote listener for this

         */
    }

    @Override
    public void onDisable() {
        votePartyHandler.saveConfig(false);
        if (sql != null) {
            sql.close();
        }
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
}
