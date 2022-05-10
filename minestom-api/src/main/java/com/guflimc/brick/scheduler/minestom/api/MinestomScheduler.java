package com.guflimc.brick.scheduler.minestom.api;

import com.guflimc.brick.scheduler.common.ThreadPoolScheduler;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class MinestomScheduler extends ThreadPoolScheduler {

    private final static MinestomScheduler INSTANCE = new MinestomScheduler("BrickScheduler");

    public MinestomScheduler get() {
        return INSTANCE;
    }

    ///////////////////////////////////////////////////////////////////////////

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
