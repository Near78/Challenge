package de.near.challenges.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

public class UpdateChecker {
    private JavaPlugin plugin;
    private int resourceID;
    public UpdateChecker(JavaPlugin plugin, int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public void Check(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceID).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
