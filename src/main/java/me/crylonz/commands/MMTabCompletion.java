package me.crylonz.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.crylonz.MobsManager.MMSpawnType;
import static me.crylonz.MobsManager.isUsefullEntity;

public class MMTabCompletion implements TabCompleter {

    private final List<String> list = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        list.clear();
        if (cmd.getName().equalsIgnoreCase("mm")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 1) {
                    if (player.hasPermission("mobsmanager.reload")) {
                        list.add("reload");
                    }

                    if (player.hasPermission("mobsmanager.manageEntity")) {
                        list.add("disable");
                        list.add("enable");
                        list.add("info");
                    }
                }

                if (args.length == 2) {
                    if (args[0].equals("disable") || args[0].equals("enable") || args[0].equals("info")) {
                        if (player.hasPermission("mobsmanager.manageEntity")) {
                            for (EntityType entity : EntityType.values()) {
                                if (isUsefullEntity(entity)) {
                                    list.add(entity.name());
                                }
                            }
                        }
                    }
                }

                if (args.length == 3) {
                    if (args[0].equals("disable") || args[0].equals("enable")) {
                        if (player.hasPermission("mobsmanager.manageEntity")) {
                            for (MMSpawnType spawnType : MMSpawnType.values()) {
                                list.add(spawnType.name());
                            }
                        }
                    }
                    if (args[0].equals("info")) {
                        if (player.hasPermission("mobsmanager.manageEntity")) {
                            Bukkit.getWorlds().forEach(world -> list.add(world.getName()));
                        }
                    }
                }

                if (args.length == 4) {
                    if (!args[0].equals("info")) {
                        if (player.hasPermission("mobsmanager.manageEntity")) {
                            Bukkit.getWorlds().forEach(world -> list.add(world.getName()));
                            list.add("*");
                        }
                    }
                }
            }
        }
        return list;
    }
}