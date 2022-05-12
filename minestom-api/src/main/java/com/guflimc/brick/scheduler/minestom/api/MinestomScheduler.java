package com.guflimc.brick.scheduler.minestom.api;

import com.guflimc.brick.scheduler.api.Scheduler;
import com.guflimc.brick.scheduler.api.ThreadPoolScheduler;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * The minestom implementation of {@link Scheduler}.
 */
public class MinestomScheduler extends ThreadPoolScheduler implements Scheduler {

    private final static MinestomScheduler INSTANCE = new MinestomScheduler("BrickScheduler");

    /**
     * Get the default instance of the scheduler.
     * @return the default instance of the scheduler
     */
    public static MinestomScheduler get() {
        return INSTANCE;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create a scheduler with the given id.
     * @param id the id
     */
    public MinestomScheduler(String id) {
        super(id, new MinestomSyncExecutor());
    }

    private final static class MinestomSyncExecutor implements Executor {
        @Override
        public void execute(@NotNull Runnable command) {
            MinecraftServer.getSchedulerManager().buildTask(command).schedule();
        }
    }
}
