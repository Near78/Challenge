package de.near.challenges.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.near.challenges.utils.Utils;

public class HubCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cKein Konsolenbefehl");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            Utils.SendToServer(p, "hub");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    Utils.SendToServer(pl, "hub");
                }
            }
        }
        return true;
    }

}
