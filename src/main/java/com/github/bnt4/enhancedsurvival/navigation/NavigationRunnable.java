package com.github.bnt4.enhancedsurvival.navigation;

import com.github.bnt4.enhancedsurvival.navigation.navigable.Navigable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NavigationRunnable implements Runnable {

    private final NavigationManager navigationManager;
    private final int destinationReachedBlocks;

    public NavigationRunnable(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
        this.destinationReachedBlocks = navigationManager.getConfig().destinationReachedBlocks();
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, Navigable> entry : this.navigationManager.getNavigatingEntries()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                // cancel navigation if player is offline for more than 90 seconds (to keep navigation when reconnecting)
                if (System.currentTimeMillis() > Bukkit.getOfflinePlayer(entry.getKey()).getLastSeen() + TimeUnit.SECONDS.toMillis(90)) {
                    this.navigationManager.cancelNavigation(entry.getKey());
                }
                continue;
            }

            String pausedReason = entry.getValue().getPausedReason();
            if (pausedReason != null) {
                player.sendActionBar(Component.text("Navigation paused - " + pausedReason, NamedTextColor.YELLOW));
                continue;
            }

            Location start = player.getLocation().add(0, 1, 0);
            Location end = entry.getValue().getTarget();
            if (end == null) {
                continue;
            }

            if (!start.getWorld().getName().equals(end.getWorld().getName())) {
                player.sendActionBar(Component.text("Navigation paused - Worlds not matching", NamedTextColor.YELLOW));
                continue;
            }

            if (destinationReachedBlocks != 0 && start.distance(end) <= destinationReachedBlocks) {
                this.navigationManager.cancelNavigation(entry.getKey());
                player.sendTitlePart(TitlePart.TITLE, Component.text(""));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.text("Destination reached", NamedTextColor.GREEN));
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(2), Duration.ofSeconds(2)));
                continue;
            }

            double d = start.distance(end) / ((int) (start.distance(end) * 1.1));
            for (int i = 0; i < 10; i++) {
                Location l = start.clone();
                Vector direction = end.toVector().subtract(start.toVector()).normalize();
                Vector v = direction.multiply(i * d);
                l.add(v.getX(), v.getY(), v.getZ());
                player.spawnParticle(Particle.HAPPY_VILLAGER, l, 2);
            }

            player.sendActionBar(Component.text(entry.getValue().getName() + " - " + ((int) start.distance(end)) + " Blocks", NamedTextColor.YELLOW));
        }
    }

}
