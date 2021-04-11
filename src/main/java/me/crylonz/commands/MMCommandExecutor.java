package me.crylonz.commands;

import me.crylonz.MobsData;
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

import static me.crylonz.MobsManager.*;

public class MMCommandExecutor implements CommandExecutor {

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

        if (args.length == 2) {
            // INFO
            checkCommand("mm info " + args[1], sender, "mobsmanager.reload", args, () -> {
                for (MobsData data : enableList) {
                    if (data.getName().equalsIgnoreCase(args[1])) {
                        sender.sendMessage(ChatColor.GREEN + "[MobsManager] Detail of " + data.getName() + " options :");
                        sender.sendMessage(ChatColor.WHITE + "All spawn : " + ChatColor.GOLD + (data.isAllSpawn() ? "On" : "Off"));
                        sender.sendMessage(ChatColor.WHITE + "Custom spawn : " + ChatColor.GOLD + (data.isCustomSpawn() ? "On" : "Off"));
                        sender.sendMessage(ChatColor.WHITE + "Egg spawn : " + ChatColor.GOLD + (data.isEggSpawn() ? "On" : "Off"));
                        sender.sendMessage(ChatColor.WHITE + "Natural spawn : " + ChatColor.GOLD + (data.isNaturalSpawn() ? "On" : "Off"));
                        sender.sendMessage(ChatColor.WHITE + "Spawner spawn : " + ChatColor.GOLD + (data.isSpawnerSpawn() ? "On" : "Off"));
                        sender.sendMessage(ChatColor.WHITE + "Breeding spawn : " + ChatColor.GOLD + (data.isBreedingSpawn() ? "On" : "Off"));
                    }
                }
            }, () -> {
                for (MobsData data : enableList) {
                    if (data.getName().equalsIgnoreCase(args[1])) {
                        log.info("[MobsManager] Detail of " + data.getName());
                        log.info("All spawn " + (data.isAllSpawn() ? "On" : "Off"));
                        log.info("Custom spawn " + (data.isCustomSpawn() ? "On" : "Off"));
                        log.info("Egg spawn " + (data.isEggSpawn() ? "On" : "Off"));
                        log.info("Natural spawn " + (data.isNaturalSpawn() ? "On" : "Off"));
                        log.info("Spawner spawn " + (data.isSpawnerSpawn() ? "On" : "Off"));
                        log.info("Breeding spawn " + (data.isBreedingSpawn() ? "On" : "Off"));
                    }
                }
            });

            // DISABLE
            checkCommand("mm disable " + args[1], sender, "mobsmanager.manageEntity", args,
                    () -> commandDisable(MMSpawnType.ALL, (Player) sender, args),
                    () -> commandDisable(MMSpawnType.ALL, null, args));

            // ENABLE
            checkCommand("mm enable " + args[1], sender, "mobsmanager.manageEntity", args,
                    () -> commandEnable(MMSpawnType.ALL, (Player) sender, args),
                    () -> commandEnable(MMSpawnType.ALL, null, args));
        }

        if (args.length == 3) {
            // DISABLE
            checkCommand(MessageFormat.format("mm disable {0} {1}", args[1], args[2]), sender, "mobsmanager.manageEntity", args, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandDisable(spawnType, (Player) sender, args);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "[MobsManager] Valid reasons are : ALL|CUSTOM|NATURAL|SPAWNER|EGG|BREEDING");
                }
            }, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandDisable(spawnType,null, args);
                } catch (IllegalArgumentException e) {
                    log.info("[MobsManager] Valid reasons are : ALL|CUSTOM|NATURAL|SPAWNER|EGG");
                }
            });

            // ENABLE
            checkCommand(MessageFormat.format("mm enable {0} {1}", args[1], args[2]), sender, "mobsmanager.manageEntity", args, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandEnable(spawnType, (Player) sender, args);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "[MobsManager] Valid reasons are : ALL|CUSTOM|NATURAL|SPAWNER|EGG|BREEDING");
                }
            }, () -> {
                try {
                    MMSpawnType spawnType = MMSpawnType.valueOf(args[2].toUpperCase());
                    commandEnable(spawnType, null, args);
                } catch (IllegalArgumentException e) {
                    log.info("[MobsManager] Valid reasons are : ALL|CUSTOM|NATURAL|SPAWNER|EGG");
                }
            });
        }

        return true;
    }

    private boolean enableOrDisableMob(String mobs, boolean state, MMSpawnType type) {
        for (EntityType entity : EntityType.values()) {
            if (isUsefullEntity(entity)) {
                if (mobs.equalsIgnoreCase(entity.name())) {
                    for (MobsData md : enableList) {
                        if (md.getName().equalsIgnoreCase(entity.name())) {
                            switch (type) {
                                case ALL:
                                    md.setAllSpawn(state);
                                    break;
                                case CUSTOM:
                                    md.setCustomSpawn(state);
                                    break;
                                case NATURAL:
                                    md.setNaturalSpawn(state);
                                    break;
                                case SPAWNER:
                                    md.setSpawnerSpawn(state);
                                    break;
                                case EGG:
                                    md.setEggSpawn(state);
                                    break;
                                case BREEDING:
                                    md.setBreedingSpawn(state);
                                    break;
                            }
                            plugin.getConfig().set("mobs", enableList);
                            plugin.saveConfig();
                            break;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void commandDisable(MMSpawnType spawnType, Player p, String[] args) {
        if (enableOrDisableMob(args[1], false, spawnType)) {
            if (p != null)
                p.sendMessage(ChatColor.GREEN + "[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is disable");
            else
                log.info("[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is disable");
        } else {
            if (p != null)
                p.sendMessage(ChatColor.RED + "[MobsManager] Invalid command usage : /mm disable <MOBS_ENTITY> [SpawnReason]");
            else
                log.info("[MobsManager] Invalid command usage : /mm disable <MOBS_ENTITY> [SpawnReason]");
        }
    }

    private void commandEnable(MMSpawnType spawnType, Player p, String[] args) {
        if (enableOrDisableMob(args[1], true, spawnType)) {
            if (p != null)
                p.sendMessage(ChatColor.GREEN + "[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is enable");
            else
                log.info("[MobsManager] " + StringUtils.capitalize(spawnType.name().toLowerCase())
                        + " " + args[1].toLowerCase() + " spawning is enable");
        } else {
            if (p != null)
                p.sendMessage(ChatColor.RED + "[MobsManager] Invalid command usage : /mm enable <MOBS_ENTITY> [SpawnReason]");
            else
                log.info("[MobsManager] Invalid command usage : /mm enable <MOBS_ENTITY> [SpawnReason]");
        }
    }

    private void checkCommand(String command, CommandSender sender, String permission, String[] args, Runnable playerRunnable, Runnable serverRunnable) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (player == null || player.hasPermission(permission)) {
            String[] commandsPart = command.split(" ");

            if (args.length == commandsPart.length-1) {
                for (int i = 0; i < args.length; ++i) {
                    if (!args[i].equalsIgnoreCase(commandsPart[i+1])) {
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
