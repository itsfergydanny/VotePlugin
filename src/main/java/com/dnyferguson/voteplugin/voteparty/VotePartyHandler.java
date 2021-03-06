package com.dnyferguson.voteplugin.voteparty;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VotePartyHandler {
    private VotePlugin plugin;
    private FileConfiguration config;
    private int votesRequired;
    private int currentVotes;
    private int votesMissing;
    private String announcementMessage;
    private long delay;
    private List<String> globalCommands;
    private List<String> perPlayerCommands;
    private String concludedMessage;

    public VotePartyHandler(VotePlugin plugin) {
        this.plugin = plugin;

        config = Config.createCustomConfig(plugin, "data.yml");

        votesRequired = plugin.getConfig().getInt("voteParty.votesRequired");
        currentVotes = config.getInt("currentVotes");
        votesMissing = votesRequired - currentVotes;

        StringBuilder ann = new StringBuilder();
        for (String line : plugin.getConfig().getStringList("voteParty.announcement")) {
            ann.append(Chat.format(line)).append("\n");
        }
        announcementMessage = ann.toString();

        delay = (long) plugin.getConfig().getDouble("voteParty.delay");

        globalCommands = plugin.getConfig().getStringList("voteParty.global-commands");
        perPlayerCommands = plugin.getConfig().getStringList("voteParty.per-player-commands");

        StringBuilder con = new StringBuilder();
        for (String line : plugin.getConfig().getStringList("voteParty.concluded")) {
            con.append(Chat.format(line)).append("\n");
        }
        concludedMessage = con.toString();
    }

    public void increment() {
        currentVotes++;
        votesMissing--;
        if (votesMissing < 0) {
            votesMissing = 0;
        }

        if (currentVotes < votesRequired) {
            saveConfig(true);
            return;
        }

        start();
    }

    public void set(int value) {
        currentVotes = value;
        votesMissing = votesRequired - currentVotes;
        saveConfig(true);
    }

    public void clear() {
        currentVotes = 0;
        votesMissing = votesRequired;
        saveConfig(true);
    }

    public void start() {
        currentVotes = 0;
        votesMissing = votesRequired;
        saveConfig(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Chat.format(announcementMessage));
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (String cmd : globalCommands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }

                Set<String> ipsRewarded = new HashSet<>();
                Set<String> ignsRewarded = new HashSet<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String ign = player.getName();
                    String uuid = player.getUniqueId().toString();

                    try {
                        String ip = Objects.requireNonNull(player.getAddress()).getAddress().toString();

                        if (ipsRewarded.contains(ip)) {
                            if (!ignsRewarded.contains(ign)) {
                                player.sendMessage(Chat.format("&cAn account on your IP has already received the voteparty reward. To set which account receives" +
                                        " the rewards, please use &f/vpmain &con the account."));
                            }
                            continue;
                        }

                        if (plugin.getVotepartyMainAccounts().containsKey(ip)) {
                            MainAccount main = plugin.getVotepartyMainAccounts().get(ip);
                            ign = main.getIgn();
                            uuid = main.getUuid().toString();
                            for (String cmd : perPlayerCommands) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", ign).replace("%player_uuid%", uuid));
                            }
                            ipsRewarded.add(ip);
                            ignsRewarded.add(ign);
                            if (!player.getName().equals(ign)) {
                                player.sendMessage(Chat.format("&cAn account on your IP has already received the voteparty reward. To set which account receives" +
                                        " the rewards, please use &f/vpmain &con the account."));
                            }
                            continue;
                        }

                        for (String cmd : perPlayerCommands) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", ign).replace("%player_uuid%", uuid));
                        }

                        ipsRewarded.add(ip);
                        ignsRewarded.add(ign);
                    } catch (Exception ignore) {}
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(Chat.format(concludedMessage));
                }
            }
        }, delay * 20);
    }

    public void saveConfig(boolean async) {
        if (async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    config.set("currentVotes", currentVotes);
                    Config.saveCustomConfig(plugin, "data.yml", config);
                }
            });
            return;
        }

        config.set("currentVotes", currentVotes);
        Config.saveCustomConfig(plugin, "data.yml", config);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

    public int getCurrentVotes() {
        return currentVotes;
    }

    public int getVotesMissing() {
        return votesMissing;
    }
}