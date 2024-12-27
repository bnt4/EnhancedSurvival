package com.github.bnt4.enhancedsurvival.playtime;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlaytimeListener implements Listener {

    private final PlaytimeManager playtimeManager;

    public PlaytimeListener(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.playtimeManager.startSession(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.playtimeManager.savePlaytime(event.getPlayer().getUniqueId());
    }

}
