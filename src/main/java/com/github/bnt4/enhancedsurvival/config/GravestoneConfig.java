package com.github.bnt4.enhancedsurvival.config;

public interface GravestoneConfig {

    /**
     * @return true, if a gravestone should spawn when a player dies
     */
    boolean isGravestones();

    /**
     * @return the time after the gravestone will be removed, 0 if it should stay forever
     */
    int removeGravestoneAfterMinutes();

}
