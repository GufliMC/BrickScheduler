package com.guflimc.brick.scheduler.api;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Countdown {

    private final Scheduler scheduler;

    private Duration remaining;

    private final Consumer<Long> handler;
    private final Set<Milestone> milestones;

    private SchedulerTask task;

    record Milestone(long seconds, Consumer<Long> handler) {}

    Countdown(Scheduler scheduler, Duration duration, Consumer<Long> handler, Set<Milestone> milestones) {
        this.scheduler = scheduler;
        this.remaining = duration;
        this.handler = handler;
        this.milestones = milestones;
    }

    //

    public void start() {
        stop();
        if ( Duration.ZERO.equals(remaining) ) {
            return;
        }

        task = scheduler.asyncRepeating(() -> {
            remaining = remaining.minus(1, ChronoUnit.SECONDS);
            handler.accept(remaining.getSeconds());

            for (Milestone milestone : milestones) {
                if ( milestone.seconds() == remaining.getSeconds() ) {
                    milestone.handler().accept(remaining.getSeconds());
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        if ( task != null ) {
            task.cancel();
        }
    }

    public Duration remaining() {
        return remaining;
    }

    public void set(Duration duration) {
        remaining = duration;
    }

    public void set(long amount, TemporalUnit unit) {
        set(Duration.of(amount, unit));
    }

}