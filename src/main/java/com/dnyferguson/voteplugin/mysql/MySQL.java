package com.dnyferguson.voteplugin.mysql;

import com.dnyferguson.voteplugin.VotePlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

    private final VotePlugin plugin;
    private final HikariDataSource ds;

    public MySQL(VotePlugin plugin) {
        this.plugin = plugin;
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("mysql");

        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl("jdbc:mysql://" + config.getString("ip") + ":" + config.getString("port") + "/" + config.getString("db"));
        hikari.setUsername(config.getString("user"));
        hikari.setPassword(config.getString("pass"));
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.setMaximumPoolSize(config.getInt("max-connections"));

        ds = new HikariDataSource(hikari);
        createTables(config.getString("db"));
    }

    private void createTables(String db) {
        executeStatementAsync("");
    }

    public void getResultAsync(String stmt, FindResultCallback callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection con = ds.getConnection()) {
                    PreparedStatement pst = con.prepareStatement(stmt);
                    ResultSet rs = pst.executeQuery();
                    callback.onQueryDone(rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getResultSync(String stmt, FindResultCallback callback) {
        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(stmt);
            ResultSet rs = pst.executeQuery();
            callback.onQueryDone(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeStatementSync(String stmt) {
        try (Connection con = ds.getConnection()) {
            PreparedStatement pst = con.prepareStatement(stmt);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeStatementAsync(String stmt) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try (Connection con = ds.getConnection()) {
                    PreparedStatement pst = con.prepareStatement(stmt);
                    pst.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public HikariDataSource getDs() {
        return ds;
    }

    public void close() {
        ds.close();
    }
}