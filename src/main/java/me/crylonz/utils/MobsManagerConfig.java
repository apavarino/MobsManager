package me.crylonz.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class MobsManagerConfig {

    private final Plugin plugin;
    private static final HashMap<String, Object> configData = new HashMap<>();
    private final FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File("config.yml"));

    public MobsManagerConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(String key, Object defaultValue) {
        configData.put(key, getFromConfig(key, defaultValue));

    }

    public Boolean getBoolean(String key) {
        return (Boolean) configData.get(key);
    }

    public double getDouble(String key) {
        return (double) configData.get(key);
    }

    public int getInt(String key) {return (int) configData.get(key);}

    private Object getFromConfig(String paramName, Object defaultValue) {
        Object param = plugin.getConfig().get(paramName);
        if (param != null) {
            return param;
        } else {
            return defaultValue;
        }
    }

    private boolean detectMissingConfigs() {
        plugin.reloadConfig();
        return configData.keySet()
                .stream()
                .anyMatch(key -> !plugin.getConfig().getKeys(true).contains(key));
    }

    public void updateConfig() {
        if (detectMissingConfigs()) {
            plugin.getLogger().warning("Missing configuration found");
            plugin.getLogger().warning("Updating config.yml with missing parameters");

            File file = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml");
            file.delete();
            plugin.saveDefaultConfig();

            configData.entrySet()
                    .stream()
                    .filter(config -> plugin.getConfig().get(config.getKey()) != null)
                    .forEach(config -> {
                        plugin.getConfig().set(config.getKey(), config.getValue());
                    });

            plugin.saveConfig();
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
