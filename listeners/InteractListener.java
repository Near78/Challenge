package de.near.challenges.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.near.challenges.utils.SettingsItems;
import de.near.challenges.utils.SettingsModes;
import de.near.challenges.utils.Timer;

public class InteractListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.CRAFTING_TABLE && SettingsModes.challenge.get(SettingsItems.ItemType.NO_CRAFTING) == SettingsItems.ItemState.ENABLED) {
                if (Timer.state == Timer.TimerState.RUNNING || SettingsModes.settings.get(SettingsItems.ItemType.TIMER) == SettingsItems.ItemState.DISABLED) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
