package me.crylonz.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class MMCommandExecutor implements CommandExecutor {

    private final MMCommandRegistrationService commandRegistration;

    public MMCommandExecutor(Plugin plugin) {
        this.commandRegistration = new MMCommandRegistrationService(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        commandRegistration.register(sender, args);

        commandRegistration.registerReload();   // mm reload
        commandRegistration.registerHelp();     // mm help
        commandRegistration.registerInfo();     // mm info
        commandRegistration.registerDisable();  // mm disable
        commandRegistration.registerEnable();   // mm enable

        if (!commandRegistration.isCommandSucceed()) {
            sender.sendMessage("[MobsManager]" + ChatColor.RED + " Unrecognized Command");
        }
        return commandRegistration.isCommandSucceed();
    }
}
