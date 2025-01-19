package com.github.bnt4.enhancedsurvival.chatformat;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.ChatFormatConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatFormatListener implements Listener {

    private final ChatFormatConfig config;

    public ChatFormatListener(EnhancedSurvival plugin, ChatFormatConfig config) {
        this.config = config;
        if (config.isCustomChatFormat()) {
            plugin.registerListener(this);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer((player, displayName, message, audience) ->
                        MiniMessage.miniMessage().deserialize(config.getCustomChatFormat(),
                                Placeholder.component("v-name", event.getPlayer().name()),
                                Placeholder.component("v-message", message),
                                Placeholder.component("v-displayname", event.getPlayer().displayName())));
    }

}
