package com.guflimc.brick.scheduler.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The scheduler can submit repeating and delayed tasks on the main thread of the application or on a separate thread.
 */
public interface Scheduler {

    /**
     * Gets the executor that executes tasks on the main thread of the application.
     * @return the executor
     */
    Executor sync();

    /**
     * Gets the executor that executes tasks on a separate thread.
     * @return the executor
     */
    Executor async();

    //

    /**
     * Schedules a task with a delayed execution on a separate thread.
     * @param task the task
     * @param delay the delay
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask asyncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules a task to be repeatedly executed on a separate thread. The first execution is submitted with no delay.
     * @param task the task
     * @param interval the interval
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask asyncRepeating(@NotNull Runnable task, long interval, @NotNull TimeUnit unit);

    /**
     * Schedules a task to be repeatedly executed on a separate thread.
     * @param task the task
     * @param delay the delay
     * @param interval the interval
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask asyncRepeating(@NotNull Runnable task, long delay, long interval, @NotNull TimeUnit unit);

    //

    /**
     * Schedules a task with a delayed execution on the main thread.
     * @param task the task
     * @param delay the delay
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask syncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit unit);

    /**
     * Schedules a task to be repeatedly executed on the main thread. The first execution is submitted with no delay.
     * @param task the task
     * @param interval the interval
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask syncRepeating(@NotNull Runnable task, long interval, @NotNull TimeUnit unit);

    /**
     * Schedules a task to be repeatedly executed on the main thread.
     * @param task the task
     * @param delay the delay
     * @param interval the interval
     * @param unit the time unit
     * @return the scheduled task
     */
    SchedulerTask syncRepeating(@NotNull Runnable task, long delay, long interval, @NotNull TimeUnit unit);

    //

    /**
     * Supplies a value with a {@link Supplier} on a separate thread and
     * returns a {@link CompletableFuture} that will be completed with the value.
     *
     * @param supplier the supplier
     * @param <T> the return type of the supplier
     * @return the future
     */
    default <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        async().execute(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Executes the given {@link Runnable} on a separate thread and
     * returns a {@link CompletableFuture} that is completed when the execution is finished.
     *
     * @param runnable the runnable
     * @return the future
     */
    default CompletableFuture<Void> runAsync(@NotNull Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        async().execute(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

}
