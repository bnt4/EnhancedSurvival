package com.github.bnt4.enhancedsurvival.navigation.navigable;

import org.bukkit.Location;

public interface Navigable {

    /**
     * @return name the player is navigating to
     */
    String getName();

    /**
     * @return target the player is navigating to
     */
    Location getTarget();

    /**
     * @return a string with the reason if the navigation is paused, otherwise null
     */
    default String getPausedReason() {
        return null;
    }

}
