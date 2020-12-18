package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class RaffleCommand implements CommandExecutor {
    private final VotePlugin plugin;

    public RaffleCommand(VotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Chat.format("&cOnly players can do this!"));
            return true;
        }

        Player player = (Player) sender;

        plugin.getSql().getResultAsync("SELECT * FROM `raffle_entries`", new FindResultCallback() {
            @Override
            public void onQueryDone(ResultSet result) throws SQLException {
                float count = 0;
                float total = 0;
                while (result.next()) {
                    if (result.getString("uuid").equals(player.getUniqueId().toString())) {
                        count++;
                    }
                    total++;
                }

                String percentStr = "0%";
                if (total > 0) {
                    percentStr = new DecimalFormat("#.##").format(count * 100 / total);
                }

                player.sendMessage(Chat.format("&eEvery day, one voter will be chosen at random to receive a $10 coupon (/coupon).\n \n" +
                        "&eYour votes today: " + count + "\n" +
                        "&fYour odds: " + count + "/" + total + " (" + percentStr + "%)\n \n" +
                        "&eEvery vote increases your odds for the day!\n \n" +
                        "&cResets and rewards automatically every day at 00:00 EST"));
            }
        });
        return true;
    }
}
