package com.github.bnt4.enhancedsurvival.waypoint;

import com.github.bnt4.enhancedsurvival.EnhancedSurvival;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WaypointListener implements Listener {

    private final EnhancedSurvival plugin;
    private final WaypointManager waypointManager;

    public WaypointListener(EnhancedSurvival plugin, WaypointManager waypointManager) {
        this.plugin = plugin;
        this.waypointManager = waypointManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();

        int deaths = player.getStatistic(Statistic.DEATHS);
        String waypointName = "Death_" + (deaths + 1);

        this.waypointManager.setWaypoint(player.getUniqueId(), waypointName, player.getLocation());

        Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            player.sendMessage(Component.text("The personal waypoint ", NamedTextColor.GREEN)
                    .append(Component.text(waypointName, NamedTextColor.WHITE)
                            .clickEvent(ClickEvent.runCommand("/navigate waypoint personal " + waypointName))
                            .hoverEvent(HoverEvent.showText(Component.text("Navigate to waypoint", NamedTextColor.GREEN))))
                    .append(Component.text(" was set at your death location.", NamedTextColor.GREEN)));
        }, 18);

    }

}
