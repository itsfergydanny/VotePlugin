package com.dnyferguson.voteplugin.tasks;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckLastVotedTask {
    private VotePlugin plugin;
    private String message;

    public CheckLastVotedTask(VotePlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("voteReminder.enabled")) {
            StringBuilder str = new StringBuilder();
            for (String line : config.getStringList("voteReminder.message")) {
                str.append(Chat.format(line)).append("\n");
            }
            message = str.toString();
            register();
        }
    }

    private void register() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getSql().getResultAsync("SELECT * FROM `lastVoted` WHERE `uuid` = '" + player.getUniqueId() +" '", new FindResultCallback() {
                        @Override
                        public void onQueryDone(ResultSet result) throws SQLException {
                            if (result.next()) {
                                long time = result.getLong("lastVoted");
                                if (time < (System.currentTimeMillis() - 86400000)) {
                                    player.sendMessage(message);
                                }
                            }
                        }
                    });
                }
            }
        }, 100, 6000);
    }
}
