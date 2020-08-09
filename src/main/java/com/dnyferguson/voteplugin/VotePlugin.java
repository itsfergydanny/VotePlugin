package com.dnyferguson.voteplugin;

import com.dnyferguson.voteplugin.commands.VoteCommand;
import com.dnyferguson.voteplugin.commands.VotePartyCommand;
import com.dnyferguson.voteplugin.mysql.MySQL;
import com.dnyferguson.voteplugin.utils.Config;
import com.dnyferguson.voteplugin.voteparty.VotePartyHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class VotePlugin extends JavaPlugin {
    private MySQL sql;
    private VotePartyHandler votePartyHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
//        sql = new MySQL(this);

        votePartyHandler = new VotePartyHandler(this);

        getCommand("vote").setExecutor(new VoteCommand(this));
        getCommand("voteparty").setExecutor(new VotePartyCommand(this));
        // add a /fakevote command
    }

    @Override
    public void onDisable() {
        votePartyHandler.saveConfig(false);
        if (sql != null) {
            sql.close();
        }
    }

    public MySQL getSql() {
        return sql;
    }

    public VotePartyHandler getVotePartyHandler() {
        return votePartyHandler;
    }
}
