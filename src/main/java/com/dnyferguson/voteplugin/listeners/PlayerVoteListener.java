package com.dnyferguson.voteplugin.listeners;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.logging.Level;

public class PlayerVoteListener implements Listener {
    private VotePlugin plugin;
    private final boolean onlineOnly;
    private final List<String> commands;

    public PlayerVoteListener(VotePlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        onlineOnly = config.getBoolean("voteRewards.online-only");
        commands = config.getStringList("voteRewards.rewards");
    }

    @EventHandler
    public void onPlayerVoteReceived(VotifierEvent e) {
        Vote vote = e.getVote();
        String address = vote.getAddress();
        String ign = vote.getUsername();
        String service = vote.getServiceName();

        if (onlineOnly) {
            if (Bukkit.getPlayerExact(ign) == null) {
                return;
            }

            Player player = Bukkit.getPlayerExact(ign);

            for (String command : commands) {
                if (command.startsWith("[msg]")) {
                    player.sendMessage(Chat.format(command.split("\\[msg]")[1]));
                    continue;
                }

                if (command.startsWith("[broadcast]")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(Chat.format(command.split("\\[broadcast]")[1]));
                    }
                    continue;
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()).replace("%player_uuid%", player.getUniqueId().toString()));
            }

            plugin.getLogger().log(Level.INFO, "[Online] Player " + ign + "(" + address + ") has just voted on " + service + "!");
            return;
        }

        for (String command : commands) {
            if (command.startsWith("[msg]")) {
                continue;
            }

            if (command.startsWith("[broadcast]")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(Chat.format(command.split("\\[broadcast]")[1]));
                }
                continue;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", ign));
        }

        plugin.getLogger().log(Level.INFO, "[Offline] Player " + ign + "(" + address + ") has just voted on " + service + "!");
    }
}
