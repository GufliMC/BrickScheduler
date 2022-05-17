package com.guflimc.brick.scheduler.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class BrickThreadFactory implements ThreadFactory {

    private final AtomicLong count = new AtomicLong(0);
    private final String name;

    public BrickThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setName(String.format(name + "-%d", count.getAndIncrement()));
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
        });
        return thread;
    }

}
