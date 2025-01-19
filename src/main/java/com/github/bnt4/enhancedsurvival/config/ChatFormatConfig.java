package com.github.bnt4.enhancedsurvival.config;

public interface ChatFormatConfig {

    /**
     * @return true, if the default chat format should be replaced
     */
    boolean isCustomChatFormat();

    String getCustomChatFormat();

}
