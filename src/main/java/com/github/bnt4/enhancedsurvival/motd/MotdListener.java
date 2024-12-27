package com.github.bnt4.enhancedsurvival.motd;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MotdListener implements Listener {

    private final MotdManager motdManager;

    public MotdListener(MotdManager motdManager) {
        this.motdManager = motdManager;
    }

    @EventHandler
    public void onServerListPing(PaperServerListPingEvent event) {
        try {
            event.motd(this.motdManager.getFullMotd());
        } catch (Exception ignored) {
        }
    }

}
