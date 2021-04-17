package de.near.challenges.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.near.challenges.main.Main;

public class NvCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cKein Konsolenbefehl");
            return false;
        }
        Player p = (Player) sender;
        if (p.hasPermission("challenges.nv")) {
            p.addPotionEffect((new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 255)));
        } else {
            p.sendMessage(Main.getPrefix("Night-Vision", "Du hast hierfür §ckeine Berechtigung"));
        }
        return false;
    }

}
