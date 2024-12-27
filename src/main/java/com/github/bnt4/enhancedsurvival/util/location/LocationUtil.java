package com.github.bnt4.enhancedsurvival.util.location;

import org.bukkit.Location;

public class LocationUtil {

    public static String formatLocation(Location location) {
        return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

}
