package me.crylonz;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

import static me.crylonz.MobsManager.log;
import static me.crylonz.MobsManager.mobsData;

public class FileManager {

    private final File configFile;

    private static FileConfiguration mobsDataConfig = null;
    private static File mobsDataFile = null;
    private static final String mobsDataFileName = "mobsData.yml";

    private final Plugin p;

    // Constructor
    public FileManager(Plugin p) {
        this.p = p;
        configFile = new File(p.getDataFolder(), "config.yml");
        mobsDataFile = new File(p.getDataFolder(), mobsDataFileName);
    }

    // Default config
    public File getConfigFile() {
        return configFile;
    }

    // Config 2
    public File getMobsDataFile() {
        return mobsDataFile;
    }

    public void reloadMobsDataConfig() {
        if (mobsDataFile == null) {
            mobsDataFile = new File(p.getDataFolder(), mobsDataFileName);
        }
        mobsDataConfig = YamlConfiguration.loadConfiguration(mobsDataFile);
    }

    public FileConfiguration getMobsDataConfig() {
        if (mobsDataConfig == null) {
            reloadMobsDataConfig();
        }
        return mobsDataConfig;
    }

    public void saveMobsDataConfig() {
        if (mobsDataConfig == null || mobsDataFile == null) {
            return;
        }
        try {
            getMobsDataConfig().save(mobsDataFile);
        } catch (IOException ex) {
            log.severe("Could not save config to " + mobsDataFile + ex);
        }
    }

    // custom func
    public void saveModification() {
        getMobsDataConfig().set("MobsData", mobsData);
        saveMobsDataConfig();
    }
}
