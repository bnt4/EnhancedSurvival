package com.github.bnt4.enhancedsurvival.navigation;

import com.github.bnt4.enhancedsurvival.navigation.navigable.LocationNavigable;
import com.github.bnt4.enhancedsurvival.navigation.navigable.PlayerNavigable;
import com.github.bnt4.enhancedsurvival.util.command.CommandUtil;
import com.github.bnt4.enhancedsurvival.waypoint.Waypoint;
import com.github.bnt4.enhancedsurvival.waypoint.WaypointManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NavigateCommand implements TabExecutor {

    private final NavigationManager navigationManager;
    private final WaypointManager waypointManager;

    private final Component navigationStarted = Component.text("Navigation started - use ", NamedTextColor.GREEN)
            .append(Component.text("/nav cancel", NamedTextColor.YELLOW)
                    .clickEvent(ClickEvent.runCommand("/navigate cancel")))
            .append(Component.text(" to cancel it", NamedTextColor.GREEN));

    public NavigateCommand(NavigationManager navigationManager, WaypointManager waypointManager) {
        this.navigationManager = navigationManager;
        this.waypointManager = waypointManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (!this.navigationManager.getConfig().isNavigation()) {
            player.sendMessage(Component.text("Navigation was disabled by the server admin", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage(Component.text("Navigate Usage: ", NamedTextColor.GOLD, TextDecoration.ITALIC)
                    .append(Component.text("/navigate or /nav", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));

            for (String[] help : new String[][]{
                    { "waypoint personal <Name>", "Navigate to personal waypoint" },
                    { "waypoint global <Name>", "Navigate to global waypoint" },
                    { "player <Name>", "Navigate to live location of player" },
                    { "coordinates <X> <Y> <Z> [World]", "Navigate to specified location" },
                    { "cancel", "Cancel active navigation" }
            }) {
                player.sendMessage(Component.text(" " + help[0])
                        .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(help[1], NamedTextColor.GRAY)));
            }
            return true;
        }

        String action = args[0];

        switch (action) {
            case "cancel" -> {
                if (!this.navigationManager.hasActiveNavigation(player.getUniqueId())) {
                    player.sendMessage(Component.text("You do not have an active navigation", NamedTextColor.YELLOW));
                    return true;
                }
                this.navigationManager.cancelNavigation(player.getUniqueId());
                player.sendMessage(Component.text("Navigation has been canceled", NamedTextColor.YELLOW));
            }

            case "waypoint" -> {
                if (!this.navigationManager.getConfig().isNavigationToWaypoints()) {
                    sendNavigationDisabled(player, "waypoints");
                    return true;
                }

                if (args.length != 3) {
                    sendInvalidUsage(player);
                    return true;
                }

                String waypointScope = args[1];
                if (!waypointScope.equals("global") && !waypointScope.equals("personal")) {
                    sendInvalidUsage(player);
                    return true;
                }

                if (!this.waypointManager.getConfig().isGlobalWaypoints() && waypointScope.equals("global")) {
                    player.sendMessage(Component.text("Global waypoints were disabled by the server admin. Please use personal waypoints instead.", NamedTextColor.RED));
                    return true;
                }

                UUID uniqueId = waypointScope.equals("global") ? null : player.getUniqueId();
                String name = args[2];

                if (!this.waypointManager.existsWaypoint(uniqueId, name)) {
                    player.sendMessage(Component.text("The " + waypointScope + " waypoint ", NamedTextColor.RED)
                            .append(Component.text(name, NamedTextColor.YELLOW))
                            .append(Component.text(" doesn't exist.", NamedTextColor.RED)));
                    return true;
                }

                Waypoint waypoint = this.waypointManager.getWaypoint(uniqueId, args[2]);
                this.navigationManager.startNavigation(player.getUniqueId(), waypoint);
                player.sendMessage(this.navigationStarted);
            }

            case "player" -> {
                if (!this.navigationManager.getConfig().isNavigationToPlayers()) {
                    sendNavigationDisabled(player, "players");
                    return true;
                }

                if (args.length != 2) {
                    sendInvalidUsage(player);
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    player.sendMessage(Component.text("The player ", NamedTextColor.RED)
                            .append(Component.text(args[1], NamedTextColor.YELLOW))
                            .append(Component.text(" is not online.", NamedTextColor.RED)));
                    return true;
                }

                if (player == target) {
                    player.sendMessage(Component.text("Sorry, the plugin can't help you find yourself :(", NamedTextColor.RED));
                    return true;
                }

                this.navigationManager.startNavigation(player.getUniqueId(), new PlayerNavigable(target));
                player.sendMessage(this.navigationStarted);
                target.sendMessage(Component.text("The player ", NamedTextColor.YELLOW)
                        .append(Component.text(player.getName(), NamedTextColor.GREEN))
                        .append(Component.text(" is navigating to your live location", NamedTextColor.YELLOW)));
            }
            case "coordinates" -> {
                if (!this.navigationManager.getConfig().isNavigationToCoordinates()) {
                    sendNavigationDisabled(player, "coordinates");
                    return true;
                }

                if (args.length != 4 && args.length != 5) {
                    sendInvalidUsage(player);
                    return true;
                }

                int x, y, z;
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("Invalid number for X, Y or Z coordinate", NamedTextColor.RED));
                    return true;
                }

                World world;
                if (args.length == 5) {
                    world = Bukkit.getWorld(args[4]);
                    if (world == null) {
                        player.sendMessage(Component.text("Invalid world name", NamedTextColor.RED));
                        return true;
                    }
                } else {
                    world = player.getWorld();
                }

                this.navigationManager.startNavigation(player.getUniqueId(), new LocationNavigable(new Location(world, x, y, z)));
                player.sendMessage(navigationStarted);
            }
        }

        return true;
    }

    private void sendInvalidUsage(Player player) {
        player.sendMessage(Component.text("Invalid usage. Use ", NamedTextColor.RED)
                .append(Component.text("/navigate ", NamedTextColor.YELLOW))
                .append(Component.text("or ", NamedTextColor.RED))
                .append(Component.text("/nav ", NamedTextColor.YELLOW))
                .append(Component.text("for help.", NamedTextColor.RED)));
    }

    private void sendNavigationDisabled(Player player, String to) {
        player.sendMessage(Component.text("Navigation to ", NamedTextColor.RED)
                .append(Component.text(to, NamedTextColor.YELLOW))
                .append(Component.text(" was disabled by the server admin", NamedTextColor.RED)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            CommandUtil.addMatches(matches, args[0], "waypoint", "player", "coordinates", "cancel");
        } else if (args.length > 1) {
            if (args[0].equals("waypoint")) {
                if (args.length == 2) {
                    CommandUtil.addMatches(matches, args[1], "global", "personal");
                } else if (args.length == 3 && (args[1].equals("global") || args[1].equals("personal"))) {
                    List<String> waypoints = this.waypointManager.getWaypointNames(args[1].equals("global") ? null : player.getUniqueId());
                    CommandUtil.addMatches(matches, args[2], waypoints);
                }
            } else if (args[0].equals("player")) {
                if (args.length == 2) {
                    CommandUtil.addMatches(matches, args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                }
            } else if (args[0].equals("coordinates")) {
                if (args.length == 5) {
                    CommandUtil.addMatches(matches, args[4], Bukkit.getWorlds().stream().map(World::getName).toList());
                }
            }
        }
        return matches;
    }
}
