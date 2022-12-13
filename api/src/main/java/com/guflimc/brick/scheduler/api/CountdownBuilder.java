package com.guflimc.brick.scheduler.api;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CountdownBuilder {

    private final Scheduler scheduler;
    private final Duration duration;

    private Consumer<Long> handler;
    private Set<Countdown.Milestone> milestones = new HashSet<>();

    CountdownBuilder(Scheduler scheduler, Duration duration) {
        this.scheduler = scheduler;
        this.duration = duration;
    }

    //

    public CountdownBuilder handler(Consumer<Long> handler) {
        this.handler = handler;
        return this;
    }

    public CountdownBuilder milestone(Consumer<Long> handler, long... points) {
        for (long seconds : points) {
            milestones.add(new Countdown.Milestone(seconds, handler));
        }
        return this;
    }

    public Countdown build() {
        return new Countdown(scheduler, duration, handler, milestones);
    }

}
