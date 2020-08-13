package com.dnyferguson.voteplugin.listeners;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginListener implements Listener {
    private VotePlugin plugin;

    public LoginListener(VotePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        plugin.getSql().getResultAsync("SELECT * FROM `tokens` WHERE `uuid` = '" + player.getUniqueId() + "'", new FindResultCallback() {
            @Override
            public void onQueryDone(ResultSet result) throws SQLException {
                if (result.next()) {
                    int tokens = result.getInt("tokens");
                    plugin.getVoteTokens().put(player.getUniqueId(), tokens);
                }
            }
        });
    }
}
