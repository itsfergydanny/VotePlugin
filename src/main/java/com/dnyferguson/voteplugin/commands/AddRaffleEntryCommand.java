package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddRaffleEntryCommand implements CommandExecutor {
    private final VotePlugin plugin;

    public AddRaffleEntryCommand(VotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voteplugin.addraffleentry")) {
            sender.sendMessage(Chat.format("&cNo permission."));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Chat.format("&cInvalid usage: /addraffleentry <name> <amount>."));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignore) {
            sender.sendMessage(Chat.format("&cInvalid amount specified."));
            return true;
        }

        String target = args[0];
        UUID uuid = plugin.getUserCache().getApi().getUUID(target);
        if (uuid == null) {
            sender.sendMessage(Chat.format("&cPlayer not found."));
            return true;
        }

        for (int i = 0; i < amount; i++) {
            plugin.getSql().executeStatementAsync("INSERT INTO `raffle_entries` (`id`, `uuid`, `username`, `time`) VALUES (NULL, '" + uuid + "', '" + target + "', CURRENT_TIMESTAMP)");
        }

        sender.sendMessage(Chat.format("&aSuccessfully gave " + target + " " + amount + "x raffle entries!"));
        return true;
    }
}
