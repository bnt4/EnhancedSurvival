package com.github.bnt4.enhancedsurvival.joinquitmessage;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import com.github.bnt4.enhancedsurvival.config.JoinQuitMessageConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitMessageListener implements Listener {

    private final JoinQuitMessageConfig config;

    public JoinQuitMessageListener(EnhancedSurvival plugin, JoinQuitMessageConfig config) {
        this.config = config;
        if (config.isCustomJoinQuitMessages()) {
            plugin.registerListener(this);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(MiniMessage.miniMessage().deserialize(config.getCustomJoinMessage(),
                Placeholder.component("v-name", event.getPlayer().name())));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(MiniMessage.miniMessage().deserialize(config.getCustomQuitMessage(),
                Placeholder.component("v-name", event.getPlayer().name())));
    }

}
