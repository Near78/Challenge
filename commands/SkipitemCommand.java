package de.near.challenges.commands;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.near.challenges.main.Main;
import de.near.challenges.projects.AllItems;
import de.near.challenges.utils.Permissions;
import de.near.challenges.utils.SettingsItems;
import de.near.challenges.utils.SettingsModes;

public class SkipitemCommand implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || Permissions.hasPermission((Player) sender, Permissions.Rank.OP)) {
            if (SettingsModes.projects.get(SettingsItems.ItemType.ALL_ITEMS) == SettingsItems.ItemState.ENABLED) {
                AllItems.next();
            } else {
                sender.sendMessage(Main.getPrefix("Skip-Item", "Dieser Command kann nur verwendet werden, wenn das Projekt §9Alle Items sammeln §7aktiviert ist."));
            }
        } else {
             sender.sendMessage(Main.getPrefix("Skip-Item", "Du hast §ckeine Berechtigung §7um diesen Befehl auszuführen."));
        }
        return false;
    }

}
