package com.guflimc.brick.scheduler.spigot.api;

import com.guflimc.brick.scheduler.api.BrickThreadPoolScheduler;
import com.guflimc.brick.scheduler.api.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * The spigot implementation of {@link Scheduler}.
 */
public class SpigotScheduler extends BrickThreadPoolScheduler implements Scheduler, Listener {

    private final JavaPlugin plugin;

    public SpigotScheduler(@NotNull JavaPlugin plugin, @NotNull String id) {
        super(id, new SpigotSyncExecutor(plugin));
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public SpigotScheduler(@NotNull JavaPlugin plugin) {
        this(plugin, plugin.getDescription().getName());
    }

    private record SpigotSyncExecutor(JavaPlugin plugin) implements Executor {
        @Override
        public void execute(@NotNull Runnable command) {
            Bukkit.getServer().getScheduler().runTask(plugin, command);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if ( !event.getPlugin().equals(plugin) ) {
            return;
        }

        try {
            plugin.getLogger().info("Shutting down scheduler.");
            shutdown();
        } catch (InterruptedException e) {
            plugin.getLogger().warning("Failed to shutdown scheduler tasks: " + e.getMessage());
        }
    }
}
