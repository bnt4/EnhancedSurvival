package com.github.bnt4.enhancedsurvival.config;

public interface NavigationConfig {

    /**
     * @return true if it is possible to navigate to either waypoints, players or coordinates or multiple
     */
    boolean isNavigation();

    /**
     * @return true if it is possible to navigate to waypoints; false if waypoints are disabled
     */
    boolean isNavigationToWaypoints();

    /**
     * @return true if it is possible to navigate to players
     */
    boolean isNavigationToPlayers();

    /**
     * @return true if it is possible to navigate to coordinates
     */
    boolean isNavigationToCoordinates();

    /**
     * @return distance in blocks when a destination should be considered as reached, 0 to disable
     */
    int destinationReachedBlocks();

}
