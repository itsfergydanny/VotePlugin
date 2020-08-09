package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VotePartyCommand implements CommandExecutor {
    private VotePlugin plugin;

    public VotePartyCommand(VotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("voteplugin.admin")) {
            sender.sendMessage(Chat.format("&aA voteparty will occur in " + plugin.getVotePartyHandler().getVotesMissing() + " votes!"));
            return true;
        }

        if (args.length < 1) {
            StringBuilder str = new StringBuilder("&aA voteparty will occur in ").append(plugin.getVotePartyHandler().getVotesMissing()).append(" votes!").append("\n \n");
            str.append("&cAdmin Commands: \n");
            str.append("&c/vp start &f- Start a voteparty immediately\n");
            str.append("&c/vp set <amount> &f- Set the current votes\n");
            str.append("&c/vp clear &f- Reset the counter to 0\n");
            str.append("&c/vp increment &f- Increase the counter by 1\n");
            sender.sendMessage(Chat.format(str.toString()));
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "start":
                plugin.getVotePartyHandler().start();
                sender.sendMessage(Chat.format("&aYou have successfully force started a voteparty!"));
                break;
            case "set":
                if (args.length != 2) {
                    sender.sendMessage(Chat.format("&cInvalid command. Format: /vp set <amount>"));
                    break;
                }
                try {
                    int amount = Integer.parseInt(args[1]);
                    plugin.getVotePartyHandler().set(amount);
                    sender.sendMessage(Chat.format("&aYou have successfully set the voteparty counter to " + amount + "!"));
                } catch (NumberFormatException ignore) {
                    sender.sendMessage(Chat.format("&cInvalid amount entered."));
                }
                break;
            case "clear":
                plugin.getVotePartyHandler().clear();
                sender.sendMessage(Chat.format("&aYou have successfully reset the voteparty counter!"));
                break;
            case "increment":
                plugin.getVotePartyHandler().increment();
                sender.sendMessage(Chat.format("&aYou have successfully increased the voteparty counter by 1! New count is " + plugin.getVotePartyHandler().getCurrentVotes() + "."));
        }

        return true;
    }
}
