package com.guflimc.brick.scheduler.api;

public class SchedulerAPI {

    private static Scheduler scheduler;

    public static void setScheduler(Scheduler scheduler) {
        SchedulerAPI.scheduler = scheduler;
    }

    //

    public static Scheduler get() {
        return scheduler;
    }

}
