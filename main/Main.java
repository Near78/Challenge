package de.near.challenges.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import DamageByEntityListener.DamageByEntityListener;
import de.near.challenges.challenges.ForceBiom;
import de.near.challenges.challenges.ForceBlock;
import de.near.challenges.challenges.ForceHeight;
import de.near.challenges.challenges.ForceMob;
import de.near.challenges.challenges.MLG;
import de.near.challenges.challenges.RandomChunkGeneration;
import de.near.challenges.challenges.RandomDrops;
import de.near.challenges.challenges.Trafficlight;
import de.near.challenges.commands.BackpackCommands;
import de.near.challenges.commands.BackupCommand;
import de.near.challenges.commands.BanCommand;
import de.near.challenges.commands.DorfCommand;
import de.near.challenges.commands.FlyCommand;
import de.near.challenges.commands.GmCommand;
import de.near.challenges.commands.HealCommand;
import de.near.challenges.commands.HubCommand;
import de.near.challenges.commands.InvseeCommand;
import de.near.challenges.commands.MobsCommand;
import de.near.challenges.commands.NvCommand;
import de.near.challenges.commands.PlaytimeCommand;
import de.near.challenges.commands.PostionCommand;
import de.near.challenges.commands.RainCommand;
import de.near.challenges.commands.RankCommand;
import de.near.challenges.commands.ReloadCommand;
import de.near.challenges.commands.ResetCommand;
import de.near.challenges.commands.SaveCommand;
import de.near.challenges.commands.SeedCommand;
import de.near.challenges.commands.SettingsCommand;
import de.near.challenges.commands.SkipitemCommand;
import de.near.challenges.commands.SunCommand;
import de.near.challenges.commands.TempBanCommand;
import de.near.challenges.commands.ThunderCommand;
import de.near.challenges.commands.TimerCommand;
import de.near.challenges.commands.TpposCommand;
import de.near.challenges.commands.TrashCommand;
import de.near.challenges.commands.UnbanCommand;
import de.near.challenges.commands.WorldCommand;
import de.near.challenges.listeners.AdvancementsListener;
import de.near.challenges.listeners.BlockBreakListener;
import de.near.challenges.listeners.BucketListener;
import de.near.challenges.listeners.ChatListener;
import de.near.challenges.listeners.ChunkLoadListener;
import de.near.challenges.listeners.DamageListener;
import de.near.challenges.listeners.DamageRemover;
import de.near.challenges.listeners.DeathListener;
import de.near.challenges.listeners.EntityDeathListener;
import de.near.challenges.listeners.EntitySpawnListener;
import de.near.challenges.listeners.FoodListener;
import de.near.challenges.listeners.GamemodeChangeListener;
import de.near.challenges.listeners.HealListener;
import de.near.challenges.listeners.InteractEntityListener;
import de.near.challenges.listeners.InteractListener;
import de.near.challenges.listeners.InventoryClickListener;
import de.near.challenges.listeners.JoinListener;
import de.near.challenges.listeners.KickListener;
import de.near.challenges.listeners.MoveListener;
import de.near.challenges.listeners.PreLoginListener;
import de.near.challenges.listeners.QuitListener;
import de.near.challenges.listeners.TeleportListener;
import de.near.challenges.listeners.TriggerListener;
import de.near.challenges.projects.AllDeathMessages;
import de.near.challenges.projects.AllItems;
import de.near.challenges.projects.AllMobs;
import de.near.challenges.tabCompletes.GmTabCompleter;
import de.near.challenges.tabCompletes.HubTabCompleter;
import de.near.challenges.tabCompletes.MobsTabCompleter;
import de.near.challenges.tabCompletes.PositionTabCompleter;
import de.near.challenges.tabCompletes.RankTabCompleter;
import de.near.challenges.tabCompletes.TimerTabComplete;
import de.near.challenges.tabCompletes.WorldTabCompleter;
import de.near.challenges.utils.AFK;
import de.near.challenges.utils.Backup;
import de.near.challenges.utils.Config;
import de.near.challenges.utils.CustomChunkGenerator;
import de.near.challenges.utils.Permissions;
import de.near.challenges.utils.Permissions.Rank;
import de.near.challenges.utils.Position;
import de.near.challenges.utils.ScoreboardManager;
import de.near.challenges.utils.SettingsItems;
import de.near.challenges.utils.SettingsModes;
import de.near.challenges.utils.Timer;
import de.near.challenges.utils.UpdateChecker;
import de.near.challenges.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
    //todo:
    //multiworld-plugin
    //hub
    //test-command

    public static boolean debug = false;

    public static final String version = "2.7";

    public Trafficlight trafficlight;

    public static boolean started = false;
    public static Main plugin;
    public static boolean deletedFolders = false;

    @Override
    public void onLoad() {
        Position pos = new Position();
        if (Config.contains("reset.isReset") && Config.getBoolean("reset.isReset")) {
            deleteFolder("world");
            deleteFolder("world_nether");
            deleteFolder("world_the_end");
            pos.reset();
            Timer.reset();
        }
        deletedFolders = true;
        if(Config.contains("reset.isReset") && Config.getBoolean("reset.isReset")) {
            try {
                Config.set("reset.isReset", false);
                Config.set("project.allmobs.mobs", null);
                Config.set("project.allitems.items", null);
                Config.set("project.allitems.current", null);
                Config.set("positions.list", null);
                Config.set("random_drops.drops", null);
                Config.set("positions.list", null);
                Config.set("playtime.player", null);
                Config.set("playtime.total", 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }
    }

    @Override
    public void onEnable() {
        MLG.createMLGWorld();
        Timer.firststart = true;
        Config config = new Config();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Config.resetConfig();
        Bukkit.getLogger().info("§aDas Plugin §8[§6Challenges§8]§a wurde erfolgreich gestartet.");
        for (World wl : Bukkit.getWorlds()) {
            wl.setGameRule(GameRule.KEEP_INVENTORY, SettingsModes.gamerule.get(SettingsItems.ItemType.KEEP_INVENTORY) == SettingsItems.ItemState.ENABLED);
            wl.setPVP(SettingsModes.gamerule.get(SettingsItems.ItemType.PVP) == SettingsItems.ItemState.ENABLED);
        }
        plugin = this;
        started = true;
        Timer.setCurrentTime(Config.getInt("timer.currenttime"));
        Timer.run();

        listenerRegistration();
        commandRegistration();
        TabCompleterRegistration();

        if (SettingsModes.challenge.get(SettingsItems.ItemType.TRAFFICLIGHT) == SettingsItems.ItemState.ENABLED) {
            trafficlight = new Trafficlight(this);
            trafficlight.start();
        }

        if (SettingsModes.settings.get(SettingsItems.ItemType.HARDCORE) == SettingsItems.ItemState.ENABLED) {
            for (World wl : Bukkit.getWorlds()) {
                wl.setHardcore(true);
            }
        } else {
            for (World wl : Bukkit.getWorlds()) {
                wl.setHardcore(false);
            }
        }

        if (SettingsModes.challenge.get(SettingsItems.ItemType.FORCEBLOCK) == SettingsItems.ItemState.ENABLED) {
            ForceBlock forceBlock = new  ForceBlock(getInstance());
            forceBlock.start();
        }

        if (SettingsModes.challenge.get(SettingsItems.ItemType.FORCEMOB)  == SettingsItems.ItemState.ENABLED) {
            ForceMob forceMob = new ForceMob(getInstance());
            forceMob.start();
        }

        for (Player pl : Bukkit.getOnlinePlayers()) {
            Permissions.setRank(pl, Permissions.Rank.valueOf(Config.getString("permissions." + pl.getUniqueId())));
        }

        if (SettingsModes.settings.get(SettingsItems.ItemType.BACKUP) == SettingsItems.ItemState.ENABLED) {
            Backup backup = new Backup();
            backup.start();
        }
        if (SettingsModes.projects.get(SettingsItems.ItemType.ALL_ITEMS) == SettingsItems.ItemState.ENABLED) {
            AllItems.start();
        }
        if (SettingsModes.projects.get(SettingsItems.ItemType.ALL_MOBS) == SettingsItems.ItemState.ENABLED) {
            AllMobs.start();
        }
        if (SettingsModes.challenge.get(SettingsItems.ItemType.FORCE_HEIGHT) == SettingsItems.ItemState.ENABLED) {
            ForceHeight forceHeight = new ForceHeight();
            forceHeight.start();
        }
        if (SettingsModes.challenge.get(SettingsItems.ItemType.FORCE_BIOME) == SettingsItems.ItemState.ENABLED) {
            ForceBiom forceBiome = new ForceBiom();
            forceBiome.start();
        }
        BackpackCommands.getBackpack();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            MoveListener.lastMovement.put(pl, System.currentTimeMillis() * 1000);
            AFK.afk.clear();
            Utils.setNewRankPrefix(pl, Permissions.getRank(pl));
        }
        AFK.start();
        Permissions.start();
        MLG.start();

        TextComponent component = new TextComponent(" Download");
        component.setColor(ChatColor.BLUE);
        component.setBold(true);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/bastighg-challenge-plugin.86023/"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Du wirst zur Update-Seite weitergeleitet").color(ChatColor.WHITE).italic(true).create()));


        if (SettingsModes.settings.get(SettingsItems.ItemType.UPDATE_CHECKER) == SettingsItems.ItemState.ENABLED) {
            new UpdateChecker(this, 86023).Check(vers -> Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                if (!vers.equalsIgnoreCase(version) && SettingsModes.settings.get(SettingsItems.ItemType.UPDATE_CHECKER) == SettingsItems.ItemState.ENABLED) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.sendMessage(Main.getPrefix("Challenge-Plugin", "Es ist ein neues Update des Challenge-Plugins verfügbar §8(§9" + version + " §8» §9" + vers + "§8)§7. Bitte lade es hier herunter:"));
                        pl.spigot().sendMessage(component);
                    }
                    Bukkit.getLogger().info(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Challenge-Plugin" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Es ist ein neues Update des Challenge-Plugins verfügbar bitte lade es dir hier herunter: " + ChatColor.BLUE + "https://www.spigotmc.org/resources/bastighg-challenge-plugin.86023/");
                } else if (SettingsModes.settings.get(SettingsItems.ItemType.UPDATE_CHECKER) == SettingsItems.ItemState.DISABLED && !vers.equalsIgnoreCase(version)){
                    Bukkit.getLogger().info(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Challenge-Plugin" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " Es ist ein neues Update des Challenge-Plugins verfügbar bitte lade es dir hier herunter: " + ChatColor.BLUE + "https://www.spigotmc.org/resources/bastighg-challenge-plugin.86023/");
                }
            }, 0, Utils.TimeToTicks(0, 30, 0)));
        }

        if (SettingsModes.challenge.get(SettingsItems.ItemType.RANDOM_DROPS) == SettingsItems.ItemState.ENABLED) {
            RandomDrops.start();
        }

        for (Player pl : Bukkit.getOnlinePlayers()) {
            ScoreboardManager.createScoreboard(pl);
        }

        if (SettingsModes.projects.get(SettingsItems.ItemType.ALL_DEATHS) == SettingsItems.ItemState.ENABLED) {
            AllDeathMessages.start();
        }

        if (SettingsModes.challenge.get(SettingsItems.ItemType.RANDOM_CHUNK_GENERATION) == SettingsItems.ItemState.ENABLED) {
            RandomChunkGeneration.start();
        }
    }

    @Override
    public void onDisable() {
        Config.saveConfig();
        for (Player pl : Bukkit.getOnlinePlayers()) {
            try {
                Config.set("permissions." + pl.getUniqueId(), ranks.get(pl).name());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new CustomChunkGenerator();
    }


    private void listenerRegistration() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new QuitListener(), this);
        pm.registerEvents(new DamageListener(), this);
        pm.registerEvents(new DeathListener(), this);
        pm.registerEvents(new TeleportListener(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new InventoryClickListener(), this);
        pm.registerEvents(new TriggerListener(), this);
        pm.registerEvents(new EntityDeathListener(), this);
        pm.registerEvents(new KickListener(), this);
        pm.registerEvents(new DamageRemover(), this);
        pm.registerEvents(new HealListener(), this);
        pm.registerEvents(new MoveListener(), this);
        pm.registerEvents(new FoodListener(), this);
        pm.registerEvents(new AdvancementsListener(), this);
        pm.registerEvents(new DamageByEntityListener(), this);
        pm.registerEvents(new ForceMob(getInstance()), this);
        pm.registerEvents(new GamemodeChangeListener(), this);
        pm.registerEvents(new PreLoginListener(), this);
        pm.registerEvents(new InteractListener(), this);
        pm.registerEvents(new InteractEntityListener(), this);
        pm.registerEvents(new BucketListener(), this);
        pm.registerEvents(new ChunkLoadListener(), this);
        pm.registerEvents(new EntitySpawnListener(), this);
    }

    private void commandRegistration() {
        this.getCommand("timer").setExecutor(new TimerCommand());
        this.getCommand("hub").setExecutor(new HubCommand());
        this.getCommand("world").setExecutor(new WorldCommand());
        this.getCommand("gm").setExecutor(new GmCommand());
        this.getCommand("backpack").setExecutor(new BackpackCommands());
        this.getCommand("bp").setExecutor(new BackpackCommands());
        this.getCommand("position").setExecutor(new PostionCommand());
        this.getCommand("pos").setExecutor(new PostionCommand());
        this.getCommand("fly").setExecutor(new FlyCommand());
        this.getCommand("nv").setExecutor(new NvCommand());
        this.getCommand("invsee").setExecutor(new InvseeCommand());
        this.getCommand("heal").setExecutor(new HealCommand());
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("refresh").setExecutor(new ReloadCommand());
        this.getCommand("rl").setExecutor(new ReloadCommand());
        this.getCommand("sun").setExecutor(new SunCommand());
        this.getCommand("rain").setExecutor(new RainCommand());
        this.getCommand("thunder").setExecutor(new ThunderCommand());
        this.getCommand("settings").setExecutor(new SettingsCommand());
        this.getCommand("challenges").setExecutor(new SettingsCommand());
        this.getCommand("trash").setExecutor(new TrashCommand());
        this.getCommand("seed").setExecutor(new SeedCommand());
        this.getCommand("l").setExecutor(new HubCommand());
        this.getCommand("dorf").setExecutor(new DorfCommand());
        this.getCommand("save").setExecutor(new SaveCommand());
        this.getCommand("rank").setExecutor(new RankCommand());
        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("pardon").setExecutor(new UnbanCommand());
        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("tempban").setExecutor(new TempBanCommand());
        this.getCommand("timeout").setExecutor(new TempBanCommand());
        this.getCommand("stilletreppe").setExecutor(new TempBanCommand());
        this.getCommand("backup").setExecutor (new BackupCommand());
        this.getCommand("skipitem").setExecutor(new SkipitemCommand());
        this.getCommand("mobs").setExecutor(new MobsCommand());
        this.getCommand("moboverview").setExecutor(new MobsCommand());
        this.getCommand("spielzeit").setExecutor(new PlaytimeCommand());
        this.getCommand("tppos").setExecutor(new TpposCommand());
    }

    private void TabCompleterRegistration() {
        this.getCommand("timer").setTabCompleter(new TimerTabComplete());
        this.getCommand("hub").setTabCompleter(new HubTabCompleter());
        this.getCommand("world").setTabCompleter(new WorldTabCompleter());
        this.getCommand("gm").setTabCompleter(new GmTabCompleter());
        this.getCommand("position").setTabCompleter(new PositionTabCompleter());
        this.getCommand("pos").setTabCompleter(new PositionTabCompleter());
        this.getCommand("rank").setTabCompleter(new RankTabCompleter());
        this.getCommand("mobs").setTabCompleter(new MobsTabCompleter());
    }

    public void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private void copyFile(File source, File target) throws IOException {
        try (
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target)
        ) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    private void deleteFolder(String folder) {
        if(Files.exists(Paths.get(folder))) {
            try {
                Files.walk(Paths.get(folder)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Main getInstance() {
        return plugin;
    }

    public static String getPrefix(String name, String Content) {
        return "§8[" + "§6" + name + "§8] §7" + Content;
    }

    public ArrayList<String> getPermissions(Player p) {
        ArrayList<String> permissions = new ArrayList<>();
        if (getConfig().contains(p.getUniqueId().toString() + ".permissions")) {
            permissions = (ArrayList<String>) getConfig().getStringList(p.getUniqueId().toString() + ".permissions");
        }

        return permissions;
    }

}
