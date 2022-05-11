package com.guflimc.brick.scheduler.api;

/**
 * A task that is scheduled.
 */
public interface SchedulerTask {

    /**
     * Cancels the task
     */
    void cancel();
}