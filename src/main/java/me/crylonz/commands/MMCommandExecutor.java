package me.crylonz.commands;

import me.crylonz.MobsData;
import me.crylonz.MobsManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static me.crylonz.MobsManager.*;

public class MMCommandExecutor implements CommandExecutor {

    private static final String errorMsg = "[MobsManager] Invalid command usage : /mm enable|disable <MOBS_ENTITY> <WORLD> <SpawnReason>";
    private static final String errorMsgReason = "[MobsManager] Valid reasons are : ALL|CUSTOM|NATURAL|SPAWNER|EGG|BREEDING";
    private final Plugin plugin;

    public MMCommandExecutor(Plugin p) {
        this.plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // RELOAD
        checkCommand("mm reload", sender, "mobsmanager.reload", args, () -> {
            plugin.reloadConfig();
            enableList = (ArrayList<MobsData>) plugin.getConfig().get("mobs");
            sender.sendMessage(ChatColor.GREEN + "[MobsManager] Plugin reload successfully");
        }, () -> {
            plugin.reloadConfig();
            enableList = (ArrayList<MobsData>) plugin.getConfig().get("mobs");
            log.info(ChatColor.GREEN + "[MobsManager] Plugin reload successfully");
        });

        if (args.length == 3) {
            // INFO
            checkCommand(MessageFormat.format("mm info {0} {1}", args[1], args[2]), sender, "mobsmanager.reload", args, () -> {
                enableList.stream()
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
            }, () -> {
                enableList.stream()
                        .filter(mobData -> mobData.getName().equalsIgnoreCase(args[1]))
                        .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(args[2]))
                        .forEach(data -> {
                            log.info("[MobsManager] Detail of " + data.getName());
                            log.info("All spawn type" + (data.isAllSpawn() ? "ON" : "OFF"));
                            log.info("Custom spawn type" + (data.isCustomSpawn() ? "ON" : "OFF"));
                            log.info("Egg spawn type" + (data.isEggSpawn() ? "ON" : "OFF"));
                            log.info("Natural spawn type" + (data.isNaturalSpawn() ? "ON" : "OFF"));
                            log.info("Spawner spawn type" + (data.isSpawnerSpawn() ? "ON" : "OFF"));
                            log.info("Breeding spawn type" + (data.isBreedingSpawn() ? "ON" : "OFF"));
                            log.info("Iron Golem spawn type" + (data.isIronGolemSpawn() ? "ON" : "OFF"));
                        });
            });
        }

        if (args.length == 4) {
            // DISABLE
            checkCommand(MessageFormat.format("mm disable {0} {1} {2}", args[1], args[2], args[3]), sender, "mobsmanager.manageEntity", args, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandDisable(spawnType, (Player) sender, args);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + errorMsgReason);
                }
            }, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandDisable(spawnType, null, args);
                } catch (IllegalArgumentException e) {
                    log.info(errorMsgReason);
                }
            });

            // ENABLE
            checkCommand(MessageFormat.format("mm enable {0} {1} {2}", args[1], args[2], args[3]), sender, "mobsmanager.manageEntity", args, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandEnable(spawnType, (Player) sender, args);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + errorMsgReason);
                }
            }, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandEnable(spawnType, null, args);
                } catch (IllegalArgumentException e) {
                    log.info(errorMsgReason);
                }
            });
        }
        return true;
    }

    private boolean enableOrDisableMob(String mobs, boolean state, MMSpawnType type, String worldName) {
        return Arrays.stream(EntityType.values())
                .filter(MobsManager::isUsefullEntity)
                .filter(entity -> entity.name().equalsIgnoreCase(mobs))
                .map(entity -> enableList
                        .stream()
                        .filter(mobData -> mobData.getName().equalsIgnoreCase(entity.name()))
                        .filter(mobData -> mobData.getWorldName().equalsIgnoreCase(worldName))
                        .findFirst()
                        .map(mobData -> {
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
                            }
                            plugin.getConfig().set("mobs", enableList);
                            plugin.saveConfig();
                            return true;
                        }).orElse(false)).findFirst().orElse(false);
    }

    private void commandDisable(MMSpawnType spawnType, Player p, String[] args) {
        if (enableOrDisableMob(args[1], false, spawnType, args[3])) {
            if (p != null)
                p.sendMessage(ChatColor.GREEN + "[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is disable on " + args[3].toUpperCase());
            else
                log.info("[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is disable on " + args[3].toUpperCase());
        } else {
            if (p != null)
                p.sendMessage(ChatColor.RED + errorMsg);
            else
                log.info(errorMsg);
        }
    }

    private void commandEnable(MMSpawnType spawnType, Player p, String[] args) {
        if (enableOrDisableMob(args[1], true, spawnType, args[3])) {
            if (p != null)
                p.sendMessage(ChatColor.GREEN + "[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is enable on " + args[3].toUpperCase());
            else
                log.info("[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is enable on " + args[3].toUpperCase());
        } else {
            if (p != null)
                p.sendMessage(ChatColor.RED + errorMsg);
            else
                log.info(errorMsg);
        }
    }

    private void checkCommand(String command, CommandSender sender, String permission, String[] args, Runnable playerRunnable, Runnable serverRunnable) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player == null || player.hasPermission(permission)) {
            String[] commandsPart = command.split(" ");

            if (args.length == commandsPart.length - 1) {
                for (int i = 0; i < args.length; ++i) {
                    if (!args[i].equalsIgnoreCase(commandsPart[i + 1])) {
                        return;
                    }
                }
                if (player != null) {
                    playerRunnable.run();
                } else {
                    serverRunnable.run();
                }
            }
        }
    }
}
