package com.dnyferguson.voteplugin.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {
    public static FileConfiguration createCustomConfig(Plugin plugin, String path) {
        File file;
        FileConfiguration config;

        file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(path, false);
        }

        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }

        return config;
    }

    public static void saveCustomConfig(Plugin plugin, String path, FileConfiguration config) {
        try {
            File file = new File(plugin.getDataFolder(), path);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
