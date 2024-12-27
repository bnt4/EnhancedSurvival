package com.github.bnt4.enhancedsurvival.config;

public interface MotdConfig {

    /**
     * @return true if the motd should be set by the plugin
     */
    boolean isMotd();

    /**
     * @return name of the world the weather and time is used of
     */
    String getMotdWorldName();

    /**
     * @return line one of the motd
     */
    String getMotdLineOne();

    /**
     * @return line two of the motd
     */
    String getMotdLineTwo();

}
