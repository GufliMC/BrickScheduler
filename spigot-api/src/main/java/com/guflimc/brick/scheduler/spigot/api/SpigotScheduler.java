package com.guflimc.brick.scheduler.spigot.api;

import com.guflimc.brick.scheduler.common.ThreadPoolScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class SpigotScheduler extends ThreadPoolScheduler {

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
