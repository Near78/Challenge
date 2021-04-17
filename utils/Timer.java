package de.near.challenges.utils;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.near.challenges.main.Main;
import de.near.challenges.utils.SettingsItems.ItemState;
import de.near.challenges.utils.SettingsItems.ItemType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Timer {
    public static int timerScheduler;
    public static TimerState state = TimerState.STOPPED;
    public static boolean firststart = true;
    public static int totalPlaytime;
    public static HashMap<Player, Integer> playtime = new HashMap<>();

    public static void run() {
        setCurrentTime(SettingsModes.currentTime);
        setStartTime(SettingsModes.startTime);
        Bukkit.broadcastMessage("�8[�6Timer�8] �aFortgesetzt");
        SettingsModes.timer.put(ItemType.RESUME, ItemState.ENABLED);
        state = TimerState.RUNNING;
        timerScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            if (Bukkit.getOnlinePlayers().size() >= 1) {
                totalPlaytime ++;
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    playtime.putIfAbsent(pl, 0);
                    int time = playtime.get(pl);
                    playtime.put(pl, time += 1);
                }
            }
            if (SettingsModes.settings.get(ItemType.TIMER) == ItemState.ENABLED) {
                sendTimer(getCurrentTime());
            if (Main.started) {
                Main.started = false;
                pause(false);
                if (Config.getString("timer.state").equalsIgnoreCase("RUNNING") && Bukkit.getOnlinePlayers().size() != 0) {
                    resume(false);
                }
            } else if (state == TimerState.RUNNING || SettingsModes.settings.get(ItemType.TIMER) == ItemState.DISABLED) {
                if (SettingsModes.challenge.get(ItemType.SPEED) == ItemState.ENABLED) {
                    for (World wl : Bukkit.getWorlds()) {
                        for (LivingEntity el : wl.getLivingEntities()) {
                            el.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, SettingsModes.speed - 1));
                        }
                    }
                }
                if (getCurrentTime() == 0 && SettingsModes.timer.get(ItemType.REVERSE) == ItemState.ENABLED) {
                    pause(true);
                    Bukkit.broadcastMessage("�8[�6Game�8] �cDie Zeit ist abgelaufen. Verloren");
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.setGameMode(GameMode.SPECTATOR);
                    }
                    SettingsModes.timer.put(ItemType.REVERSE, ItemState.DISABLED);
                } else {
                    if (SettingsModes.timer.get(ItemType.REVERSE) == ItemState.ENABLED) {
                        setCurrentTime(getCurrentTime() - 1);
                    } else {
                        setCurrentTime(getCurrentTime() + 1);
                    }
                }
            }
            }
        }, 0, 20);
    }

    public static void resume(boolean SendStateMessage) {
        if (firststart) {
            setCurrentTime(Config.getInt("timer.currenttime"));
            firststart = false;
        }
        SettingsModes.timer.put(ItemType.RESUME, ItemState.ENABLED);
        state = TimerState.RUNNING;
        if (SendStateMessage) {
            Bukkit.broadcastMessage("�8[�6Timer�8] �7�lDer Timer �a�lwurde fortgesetzt");
            Utils.sendChange("�7Timer �afortgesetzt", "");
        }
    }

    public static void pause(boolean SendStateMessage) {
        SettingsModes.timer.put(ItemType.RESUME, ItemState.DISABLED);
        state = TimerState.PAUSED;
        if (SendStateMessage) {
            Bukkit.broadcastMessage("�8[�6Timer�8] �7�lDer Timer �c�lwurde pausiert");
            Utils.sendChange("�7Timer �cpausiert", "");
        }
    }

    public static void reset() {
        Config conf = new Config();
        setCurrentTime(getStartTime());
        try {
            conf.set("timer.currenttime", getStartTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendTimer(int message) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (state == TimerState.RUNNING) {
                if (SettingsModes.challenge.get(ItemType.FORCE_HEIGHT) == ItemState.ENABLED) {
                    pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConvertTimerTime(message, "�6�l") + " �7| �8[�6Y�8] �6" + pl.getLocation().getBlockY()));
                } else if (SettingsModes.challenge.get(ItemType.FORCE_BIOME) == ItemState.ENABLED) {
                    pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConvertTimerTime(message, "�6�l") + " �7| �8[�6Biome�8] �6" + Utils.firstLatterCapitalized(pl.getLocation().getBlock().getBiome().toString().replace("_", " "))));
                } else {
                    pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ConvertTimerTime(message, "�6�l")));
                }
            } else if (state == TimerState.PAUSED) {
                pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("�6Der Timer ist pausiert."));
            }
        }
    }

    public static String ConvertTimerTime(int time, String format) {
        int seconds = time % 60;
        int minutes = time / 60 % 60;
        int hours = time / 3600 % 24;
        int days = time / 86400 % 30;
        int months = time / 2592000 % 12;
        int years = time / 31104000;

        StringBuilder timerTime = new StringBuilder(format);
        if (years != 0) {
            if (years != 1) {
                timerTime.append(years).append(" Jahre, ");
            } else {
                timerTime.append(years).append(" Jahr, ");
            }
        }
        if (months != 0) {
            if (months != 1) {
                timerTime.append(months).append(" Monate, ");
            } else {
                timerTime.append(months).append(" Monat, ");
            }
        }
        if (days != 0) {
            if (days != 1) {
                timerTime.append(days).append(" Tage, ");
            } else {
                timerTime.append(days).append(" Tag, ");
            }
        }
        if (hours != 0) {
            timerTime.append(String.format("%02d", hours)).append(":");
        }
        timerTime.append(String.format("%02d", minutes)).append(":");
        timerTime.append(String.format("%02d", seconds));

        return timerTime.toString();
    }

    public static int getCurrentTime() {
        return SettingsModes.currentTime;
    }

    public static void setCurrentTime(int time) {
        SettingsModes.currentTime = time;
    }

    public static int getStartTime() {
        return SettingsModes.startTime;
    }

    public static void setStartTime(int time) {
        SettingsModes.currentTime = time;
    }

    public enum TimerState {
        RUNNING,
        PAUSED,
        STOPPED;
    }

}
