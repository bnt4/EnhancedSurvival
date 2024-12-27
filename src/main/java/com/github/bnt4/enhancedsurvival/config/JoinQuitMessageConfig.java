package com.github.bnt4.enhancedsurvival.config;

public interface JoinQuitMessageConfig {

    /**
     * @return true, if the default join and quit messages should be replaced
     */
    boolean isCustomJoinQuitMessages();

    String getCustomJoinMessage();
    String getCustomQuitMessage();

}
