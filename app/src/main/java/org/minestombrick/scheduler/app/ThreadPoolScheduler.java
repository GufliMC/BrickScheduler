/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package org.minestombrick.scheduler.app;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.minestombrick.scheduler.api.Scheduler;
import org.minestombrick.scheduler.api.SchedulerTask;

import java.util.concurrent.*;

public class ThreadPoolScheduler implements Scheduler {

    private final ScheduledThreadPoolExecutor scheduler;
    private final CustomExecutor schedulerWorkerPool;
    private final ForkJoinPool worker;

    private final MinestomExecutor syncExecutor = new MinestomExecutor();

    public ThreadPoolScheduler(String id) {
        this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(id + "-scheduler")
                .build()
        );
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.schedulerWorkerPool = new CustomExecutor(Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(id + "-schedule-worker-%d")
                .build()
        ));
        this.worker = new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> e.printStackTrace(), false);
    }

    @Override
    public Executor sync() {
        return syncExecutor;
    }

    @Override
    public Executor async() {
        return this.worker;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.schedulerWorkerPool.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.schedulerWorkerPool.execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask syncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.sync().execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask syncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.sync().execute(task), interval, interval, unit);
        return () -> future.cancel(false);
    }

    private static final class CustomExecutor implements Executor {
        private final ExecutorService delegate;

        private CustomExecutor(ExecutorService delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            this.delegate.execute(new CustomRunnable(command));
        }
    }

    private static final class CustomRunnable implements Runnable {
        private final Runnable delegate;

        private CustomRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final static class MinestomExecutor implements Executor {
        @Override
        public void execute(@NotNull Runnable command) {
            MinecraftServer.getSchedulerManager().buildTask(command).schedule();
        }
    }
}
