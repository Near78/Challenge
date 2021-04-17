package DamageByEntityListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.near.challenges.utils.SettingsItems;
import de.near.challenges.utils.SettingsModes;
import de.near.challenges.utils.Timer;
import de.near.challenges.utils.Utils;

public class DamageByEntityListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (SettingsModes.challenge.get(SettingsItems.ItemType.DAMAGEMIRROR) == SettingsItems.ItemState.ENABLED) {
                if (Timer.state == Timer.TimerState.RUNNING || SettingsModes.settings.get(SettingsItems.ItemType.TIMER) == SettingsItems.ItemState.DISABLED) {
                    if (!(e.getEntity() instanceof Player)) {
                        int number = Utils.getRandomInt(1, 100);
                        if (number <= SettingsModes.probabilityToMirrorDamage) {
                            p.damage(e.getDamage());
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

}
