package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteCommand implements CommandExecutor {
    private final VotePlugin plugin;
    private final String message;

    public VoteCommand(VotePlugin plugin) {
        this.plugin = plugin;

        StringBuilder str = new StringBuilder();
        for (String line : plugin.getConfig().getStringList("voteCommand")) {
            str.append(line).append("\n");
        }
        message = str.toString();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Chat.format(message
                .replace("{voteparty_current}", plugin.getVotePartyHandler().getCurrentVotes() + "")
                .replace("{voteparty_required}", plugin.getVotePartyHandler().getVotesRequired() + "")
                .replace("{voteparty_missing}", plugin.getVotePartyHandler().getVotesMissing() + ""))
        );
        return true;
    }
}
