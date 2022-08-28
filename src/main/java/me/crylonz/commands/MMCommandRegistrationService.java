package me.crylonz.commands;

import me.crylonz.MobsData;
import me.crylonz.MobsManager;
import me.crylonz.MobsManager.MMSpawnType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.crylonz.MobsManager.fileManager;
import static me.crylonz.MobsManager.mobsData;

public class MMCommandRegistrationService extends MMCommandRegistration {

    private static final String errorMsg = ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " World not found or entity name is incorrect";
    private static final String errorMsgSpawnReason = ChatColor.WHITE + "[MobsManager]" + ChatColor.RED + " Valid reasons are : ALL CUSTOM NATURAL SPAWNER EGG BREEDING";

    public MMCommandRegistrationService(Plugin plugin) {
        super(plugin);
    }

    public void registerReload() {
        registerCommand("mobsmanager reload", "mobsmanager.reload", () -> {
            this.plugin.reloadConfig();
            mobsData = (ArrayList<MobsData>) plugin.getConfig().get("mobs");
            sender.sendMessage(ChatColor.GREEN + "[MobsManager] Plugin reload successfully");
        });
    }

    public void registerHelp() {
        registerCommand("mobsmanager help", "mobsmanager.help", () -> {
            sender.sendMessage("[MobsManager]" + ChatColor.GREEN + " List of command");
            sender.sendMessage(ChatColor.GOLD + "/mm reload" + ChatColor.WHITE + " Reload the plugin");
            sender.sendMessage(ChatColor.GOLD + "/mm enable <Entity> <SpawnReason> <World> " + ChatColor.WHITE + "Enable spawning for a mob");
            sender.sendMessage(ChatColor.GOLD + "/mm disable <Entity> <SpawnReason> <World> " + ChatColor.WHITE + "Disable spawning for a mob");
            sender.sendMessage(ChatColor.GOLD + "/mm info <Entity> <World> " + ChatColor.WHITE + "Display spawning info of a mob");
        });
    }

    public void registerInfo() {
        registerCommand("mobsmanager info {0} {1}", "mobsmanager.info", () -> {
            mobsData.stream()
                    .filter(mobData -> mobData.getName().equalsIgnoreCase(args[1]))
                    .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(args[2]))
                    .forEach(data -> {
                        sender.sendMessage(ChatColor.WHITE + "[MobsManager] Details of " + ChatColor.GOLD + data.getName() + ChatColor.WHITE + " spawning options on " + ChatColor.GOLD + data.getWorldName());
                        sender.sendMessage(ChatColor.WHITE + "-------------------");
                        sender.sendMessage(ChatColor.WHITE + "All spawn type : " + (data.isAllSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Custom spawn type : " + (data.isCustomSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Egg spawn type : " + (data.isEggSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Natural spawn type : " + (data.isNaturalSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Spawner spawn type : " + (data.isSpawnerSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Breeding spawn type : " + (data.isBreedingSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "Iron Golem spawn type : " + (data.isIronGolemSpawn() ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
                        sender.sendMessage(ChatColor.WHITE + "-------------------");
                    });
        });
    }

    public void registerDisable() {
        registerCommand("mobsmanager disable {0} {1} {2}", "mobsmanager.manageEntity", () -> {
            try {
                MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                commandDisable(spawnType, args);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(errorMsgSpawnReason);
            }
        });
    }

    public void registerEnable() {
        registerCommand("mobsmanager enable {0} {1} {2}", "mobsmanager.manageEntity", () -> {
            try {
                MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                commandEnable(spawnType, args);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(errorMsgSpawnReason);
            }
        });

    }

    private void commandDisable(MMSpawnType spawnType, String[] args) {
        if (enableOrDisableMob(args[1], false, spawnType, args[3])) {
            sender.sendMessage((ChatColor.WHITE + "[MobsManager] " + ChatColor.GREEN + StringUtils.capitalize(spawnType.name().toLowerCase())
                    + " " + args[1].toLowerCase() + " spawning is disable on " + args[3].toUpperCase()));
        } else {
            sender.sendMessage(ChatColor.RED + errorMsg);
        }
    }

    private void commandEnable(MMSpawnType spawnType, String[] args) {
        if (enableOrDisableMob(args[1], true, spawnType, args[3])) {
            sender.sendMessage(ChatColor.WHITE + "[MobsManager] " + ChatColor.GREEN + StringUtils.capitalize(spawnType.name().toLowerCase())
                    + " " + args[1].toLowerCase() + " spawning is enable on " + args[3].toUpperCase());
        } else {
            sender.sendMessage(ChatColor.RED + errorMsg);
        }
    }

    private boolean enableOrDisableMob(String mobs, boolean state, MMSpawnType type, String worldName) {
        AtomicBoolean updated = new AtomicBoolean(false);
        Arrays.stream(EntityType.values())
                .filter(MobsManager::isUsefulEntity)
                .filter(entity -> entity.name().equalsIgnoreCase(mobs))
                .forEach(entity -> mobsData
                        .stream()
                        .filter(mobData -> mobData.getName().equalsIgnoreCase(entity.name()))
                        .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(worldName) || worldName.equalsIgnoreCase("*"))
                        .forEach(mobData -> {
                                    updated.set(true);
                                    switch (type) {
                                        case ALL:
                                            mobData.setAllSpawn(state);
                                            break;
                                        case CUSTOM:
                                            mobData.setCustomSpawn(state);
                                            break;
                                        case NATURAL:
                                            mobData.setNaturalSpawn(state);
                                            break;
                                        case SPAWNER:
                                            mobData.setSpawnerSpawn(state);
                                            break;
                                        case EGG:
                                            mobData.setEggSpawn(state);
                                            break;
                                        case BREEDING:
                                            mobData.setBreedingSpawn(state);
                                            break;
                                        case IRON_GOLEM:
                                            mobData.setIronGolemSpawn(state);
                                            break;
                                        default:
                                            updated.set(false);
                                            break;
                                    }
                                    fileManager.getMobsDataConfig().set("mobs", mobsData);
                                    fileManager.saveMobsDataConfig();
                                }
                        ));

        return updated.get();
    }
}