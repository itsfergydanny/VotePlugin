package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.utils.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class RaffleResetCommand implements CommandExecutor {
    private final VotePlugin plugin;
    private String webhookUrl;

    public RaffleResetCommand(VotePlugin plugin) {
        this.plugin = plugin;
        webhookUrl = plugin.getConfig().getString("discord-webhook");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voteplugin.rafflereset")) {
            sender.sendMessage(Chat.format("&cNo permission."));
            return true;
        }
        if (sender instanceof Player) {
            sender.sendMessage(Chat.format("&cOnly console can do this."));
            return true;
        }

        plugin.getLogger().info("[Raffle] Calculating todays raffle winner..");

        plugin.getSql().getResultAsync("SELECT * FROM `raffle_entries`", new FindResultCallback() {
            @Override
            public void onQueryDone(ResultSet result) throws SQLException {
                List<RaffleEntry> entries = new ArrayList<>();
                while (result.next()) {
                    entries.add(new RaffleEntry(UUID.fromString(result.getString("uuid")), result.getString("username")));
                }

                if (entries.size() < 1) {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
                            try {
                                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                        .setTitle(":tickets: Daily Raffle Winner :tickets:")
                                        .setDescription("Todays raffle has no winner because nobody entered! Use `/raffle` in-game for more information about how to enter and win prizes!")
                                        .setColor(Color.RED)
                                        .addField("Next Raffle", "in 24 hours.", false));
                                webhook.execute(); //Handle exception
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }

                RaffleEntry winner = getRandomEntry(entries);
                plugin.getLogger().info("[Raffle] Winner chosen: " + winner.getIgn() + "(" + winner.getUuid() + "). Sending out reward..");

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
                        try {
                            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                    .setTitle(":tickets: Daily Raffle Winner :tickets:")
                                    .setDescription("Todays raffle winner has been chosen! Use `/raffle` in-game for more information about how to enter and win prizes!")
                                    .setColor(Color.GREEN)
                                    .addField("Winner", "" + winner.getIgn(), true)
                                    .addField("Prize", "$10 Store Coupon (/coupon)", true)
                                    .addField("Next Raffle", "in 24 hours.", false)
                                    .addField("Total Entries", entries.size() + "", false));
                            webhook.execute(); //Handle exception
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "createcoupon " + winner.getIgn() + " 10");
                        plugin.getLogger().info("[Raffle] Command dispatched!");
                    }
                });

                plugin.getSql().executeStatementAsync("DELETE FROM `raffle_entries`");
            }
        });
        return true;
    }

    private RaffleEntry getRandomEntry(List<RaffleEntry> items) {
        return items.get(new Random().nextInt(items.size()));
    }
}

class RaffleEntry {
    private final UUID uuid;
    private final String ign;

    public RaffleEntry(UUID uuid, String ign) {
        this.uuid = uuid;
        this.ign = ign;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIgn() {
        return ign;
    }
}
