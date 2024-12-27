package com.github.bnt4.enhancedsurvival.config;

public interface WaypointConfig {

    /**
     * @return true if waypoint system is enabled
     */
    boolean isWaypoints();

    /**
     * @return true if a waypoint is set at the death location if a player dies
     */
    boolean isSetWaypointOnDeath();

    /**
     * @return true if global waypoints are enabled
     */
    boolean isGlobalWaypoints();

    /**
     * @return true if permission {@link com.github.bnt4.enhancedsurvival.util.Permission#WAYPOINTS_GLOBAL_ADMIN} or OP is required to modify global waypoints
     */
    boolean isRequirePermissionToModifyGlobalWaypoints();

}
