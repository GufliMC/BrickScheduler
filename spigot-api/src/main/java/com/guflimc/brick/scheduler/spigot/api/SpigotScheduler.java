package com.guflimc.brick.scheduler.spigot.api;

import com.guflimc.brick.scheduler.api.Scheduler;
import com.guflimc.brick.scheduler.common.ThreadPoolScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * The spigot implementation of {@link Scheduler}.
 */
public class SpigotScheduler extends ThreadPoolScheduler implements Scheduler {

    /**
     * Create a scheduler for the given plugin with the given id.
     * @param plugin the plugin
     * @param id the id
     */
    public SpigotScheduler(JavaPlugin plugin, String id) {
        super(id, new SpigotSyncExecutor(plugin));
    }

    private record SpigotSyncExecutor(JavaPlugin plugin) implements Executor {
        @Override
        public void execute(@NotNull Runnable command) {
            Bukkit.getServer().getScheduler().runTask(plugin, command);
        }
    }
}
