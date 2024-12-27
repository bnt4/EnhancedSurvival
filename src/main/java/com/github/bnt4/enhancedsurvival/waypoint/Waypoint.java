package com.github.bnt4.enhancedsurvival.waypoint;

import com.github.bnt4.enhancedsurvival.navigation.navigable.Navigable;
import org.bukkit.Location;

public record Waypoint(String name, Location location) implements Navigable {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getTarget() {
        return location.clone().add(0, 1, 0);
    }

}
