package org.minestombrick.scheduler.app;

import net.minestom.server.extensions.Extension;
import org.minestombrick.scheduler.api.SchedulerAPI;

public class BrickScheduler extends Extension {

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        SchedulerAPI.setScheduler(new ThreadPoolScheduler("BrickScheduler"));

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {
        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }

}
