package com.guflimc.brick.scheduler.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class BrickThreadPoolScheduler implements Scheduler {

    private final ScheduledThreadPoolExecutor scheduler;
    private final ErrorHandlerExecutor schedulerWorkerPool;
    private final ForkJoinPool worker;

    private final Executor syncExecutor;

    public BrickThreadPoolScheduler(String id, Executor syncExecutor) {
        this.syncExecutor = syncExecutor;

        this.scheduler = new ScheduledThreadPoolExecutor(4, new BrickThreadFactory(id + "-scheduler"));
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.schedulerWorkerPool = new ErrorHandlerExecutor(Executors.newCachedThreadPool(new BrickThreadFactory(id + "-schedule-worker")));
        this.worker = new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> e.printStackTrace(), false);
    }

    protected void shutdown() throws InterruptedException {
        scheduler.shutdown();
        worker.shutdown();
        scheduler.awaitTermination(5, TimeUnit.SECONDS);
        worker.awaitTermination(5, TimeUnit.SECONDS);
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
    public SchedulerTask asyncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.schedulerWorkerPool.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask asyncRepeating(@NotNull Runnable task, long interval, @NotNull TimeUnit unit) {
        return asyncRepeating(task, 0, interval, unit);
    }

    @Override
    public SchedulerTask asyncRepeating(@NotNull Runnable task, long delay, long interval, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.schedulerWorkerPool.execute(task), delay, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask syncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.sync().execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask syncRepeating(@NotNull Runnable task, long interval, @NotNull TimeUnit unit) {
        return syncRepeating(task, 0, interval, unit);
    }

    @Override
    public SchedulerTask syncRepeating(@NotNull Runnable task, long delay, long interval, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.sync().execute(task), delay, interval, unit);
        return () -> future.cancel(false);
    }

    ///////////////////////////////////////////////////////////////////////////

    private record ErrorHandlerExecutor(ExecutorService delegate) implements Executor {
        @Override
        public void execute(Runnable command) {
            this.delegate.execute(new ErrorBoundaryRunnable(command));
        }
    }

    private record ErrorBoundaryRunnable(Runnable delegate) implements Runnable {
        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
