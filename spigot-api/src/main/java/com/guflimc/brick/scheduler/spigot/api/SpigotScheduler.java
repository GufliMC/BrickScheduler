package com.guflimc.brick.scheduler.spigot.api;

import com.guflimc.brick.scheduler.api.BrickThreadPoolScheduler;
import com.guflimc.brick.scheduler.api.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * The spigot implementation of {@link Scheduler}.
 */
public class SpigotScheduler extends BrickThreadPoolScheduler implements Scheduler {

    private final JavaPlugin plugin;
    private final PluginListener listener;

    public SpigotScheduler(@NotNull JavaPlugin plugin, @NotNull String id) {
        super(id, new SpigotSyncExecutor(plugin));
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(listener = new PluginListener(this), plugin);
    }

    public SpigotScheduler(@NotNull JavaPlugin plugin) {
        this(plugin, plugin.getDescription().getName());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        HandlerList.unregisterAll(listener);
    }

    // executor

    private final static class SpigotSyncExecutor implements Executor {

        private final JavaPlugin plugin;

        private SpigotSyncExecutor(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(@NotNull Runnable command) {
            Bukkit.getServer().getScheduler().runTask(plugin, command);
        }
    }

    // shutdown hook

    private final static class PluginListener implements Listener {

        private final SpigotScheduler scheduler;

        private PluginListener(@NotNull SpigotScheduler scheduler) {
            this.scheduler = scheduler;
        }

        @EventHandler
        public void onDisable(PluginDisableEvent event) {
            if ( !event.getPlugin().equals(scheduler.plugin) ) {
                return;
            }

            try {
                scheduler.plugin.getLogger().info("Shutting down scheduler.");
                scheduler.terminate(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                scheduler.plugin.getLogger().warning("Failed to shutdown scheduler tasks: " + e.getMessage());
            }
        }
    }
}
