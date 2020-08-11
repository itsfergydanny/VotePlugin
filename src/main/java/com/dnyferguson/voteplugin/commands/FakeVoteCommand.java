package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.utils.Chat;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Instant;

public class FakeVoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voteplugin.fakevote")) {
            sender.sendMessage(Chat.format("&cNo permission."));
            return true;
        }

        Vote vote = new Vote();
        vote.setTimeStamp(Instant.now().toString());

        if (args.length != 3) {
            sender.sendMessage(Chat.format("&cPlease use /fakevote <username> <list name> <ip>"));
            return true;
        }

        vote.setUsername(args[0]);
        vote.setServiceName(args[1]);
        vote.setAddress(args[2]);

        VotifierEvent event = new VotifierEvent(vote);
        Bukkit.getPluginManager().callEvent(event);
        return true;
    }
}
