package com.github.bnt4.enhancedsurvival.config;

public interface RecipeConfig {

    /**
     * @return true if it is possible to smelt rotten flesh to get leather
     */
    boolean isRottenFleshToLeather();

    /**
     * @return true if it is possible to craft quartz blocks back to quartz
     */
    boolean isQuartzBlockToQuartz();

    /**
     * @return true if it is possible to craft logs to sticks
     */
    boolean isLogsToSticks();

}
