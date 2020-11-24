package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.voteparty.MainAccount;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class KeyAllCommand implements CommandExecutor {
    private final VotePlugin plugin;
    private final String cmd;

    public KeyAllCommand(VotePlugin plugin) {
        this.plugin = plugin;
        cmd = plugin.getConfig().getString("keyall");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voteplugin.keyall")) {
            sender.sendMessage(Chat.format("&cNo permission"));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Chat.format("&cInvalid command. Usage: &f/keyall <key> <amount>"));
            return true;
        }

        String key = args[0];

        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ignore) {
            sender.sendMessage(Chat.format("&cInvalid amount specified."));
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Chat.format("&4&l(!) &4A " + key + " KeyAll is happening in 30 seconds, please make space in your inventories for the key!"));
        }

        int finalAmount = amount;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Set<String> ipsRewarded = new HashSet<>();
            Set<String> ignsRewarded = new HashSet<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String ign = player.getName();

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

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", ign).replace("%keyname%", key).replace("%amount%", finalAmount + ""));

                        ipsRewarded.add(ip);
                        ignsRewarded.add(ign);
                        if (!player.getName().equals(ign)) {
                            player.sendMessage(Chat.format("&cAn account on your IP has already received the keyall reward. To set which account receives" +
                                    " the rewards, please use &f/vpmain &con the account."));
                        }
                        continue;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", ign).replace("%keyname%", key).replace("%amount%", finalAmount + ""));

                    ipsRewarded.add(ip);
                    ignsRewarded.add(ign);
                } catch (Exception ignore) {}
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(Chat.format("&4&l(!) &4The " + key + " keyall has concluded!"));
            }
        }, 30 * 20);

        return true;
    }
}
