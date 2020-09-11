package com.dnyferguson.voteplugin.commands;

import com.dnyferguson.voteplugin.VotePlugin;
import com.dnyferguson.voteplugin.mysql.FindResultCallback;
import com.dnyferguson.voteplugin.utils.Chat;
import com.dnyferguson.voteplugin.voteparty.MainAccount;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class VPMainCommand implements CommandExecutor {
    private final VotePlugin plugin;

    public VPMainCommand(VotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        try {
            String ip = Objects.requireNonNull(player.getAddress()).getAddress().toString();
            plugin.getVotepartyMainAccounts().put(ip, new MainAccount(player.getUniqueId(), player.getName()));
            plugin.getSql().getResultAsync("SELECT * FROM `votepartyMainAccounts` WHERE `ip` = '" + ip + "'", new FindResultCallback() {
                @Override
                public void onQueryDone(ResultSet result) throws SQLException {
                    if (result.next()) {
                        plugin.getSql().executeStatementAsync("UPDATE `votepartyMainAccounts` SET `uuid`='" + player.getUniqueId() + "',`ign`='" + player.getName() + "' WHERE `ip` = '" + ip + "'");
                    } else {
                        plugin.getSql().executeStatementAsync("INSERT INTO `votepartyMainAccounts` (`ip`, `uuid`, `ign`) VALUES ('" + ip + "', '" + player.getUniqueId() + "', '" + player.getName() + "')");
                    }
                }
            });
            player.sendMessage(Chat.format("&aYou have successfully set this account to be the receiver of voteparty rewards!"));
        } catch (Exception ignore) {}

        return true;
    }
}