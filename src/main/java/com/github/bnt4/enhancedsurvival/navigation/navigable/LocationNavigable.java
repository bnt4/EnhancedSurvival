package com.github.bnt4.enhancedsurvival.navigation.navigable;

import org.bukkit.Location;

public class LocationNavigable implements Navigable {

    private final Location location;

    public LocationNavigable(Location location) {
        this.location = location;
    }

    @Override
    public String getName() {
        return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

    @Override
    public Location getTarget() {
        return location;
    }

}
