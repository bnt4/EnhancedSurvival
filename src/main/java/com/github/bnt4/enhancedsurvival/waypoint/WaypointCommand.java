package com.github.bnt4.enhancedsurvival.waypoint;

import com.github.bnt4.enhancedsurvival.util.Permission;
import com.github.bnt4.enhancedsurvival.util.command.CommandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

public class WaypointCommand implements TabExecutor {

    private final WaypointManager waypointManager;

    public WaypointCommand(WaypointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (!waypointManager.getConfig().isWaypoints()) {
            player.sendMessage(Component.text("Waypoints were disabled by the server admin", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("");
            player.sendMessage(Component.text("Waypoint Usage: ", NamedTextColor.GOLD, TextDecoration.ITALIC)
                    .append(Component.text("/wp personal/global", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)));

            for (String[] help : new String[][]{
                    { "list", "List your personal or all global waypoints" },
                    { "set <Name>", "Set a waypoint at your current location" },
                    { "set <Name> <World> <X> <Y> <Z>", "Set a waypoint at the specified location" },
                    { "rename <Old> <New>", "Rename the specified waypoint" },
                    { "delete <Name>", "Delete the specified waypoint" }
            }) {
                player.sendMessage(Component.text(" " + help[0])
                        .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(help[1], NamedTextColor.GRAY)));
            }
            return true;
        }

        UUID uniqueId;
        String scope = args[0];
        if (scope.equals("global")) {
            uniqueId = null;
            if (!waypointManager.getConfig().isGlobalWaypoints()) {
                player.sendMessage(Component.text("Global waypoints were disabled by the server admin. Please use personal waypoints instead (/wp personal ...).", NamedTextColor.RED));
                return true;
            }
        } else if (scope.equals("personal")) {
            uniqueId = player.getUniqueId();
        } else {
            player.sendMessage(Component.text("Invalid scope \"" + args[0] + "\". Use \"personal\" for your own private, personal waypoints or \"global\" for waypoints which are the same for all players.", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sendInvalidUsage(player);
            return true;
        }

        String action = args[1];

        if (action.equals("list")) {
            player.sendMessage("");
            List<Waypoint> waypoints = waypointManager.getWaypoints(uniqueId);
            if (waypoints.size() == 0) {
                player.sendMessage(Component.text("There are currently no " + scope + " waypoints.", NamedTextColor.GRAY));
                return true;
            }
            player.sendMessage(Component.text("There are currently " + waypoints.size() + " " + scope + " waypoints:", NamedTextColor.GREEN));
            String invertedScope = (scope.equals("personal") ? "global" : "personal");

            for (Waypoint waypoint : waypoints) {
                Location location = waypoint.location();
                String locationString = location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
                String name = waypoint.name();

                player.sendMessage(Component.text("§8- ")
                        .append(Component.text("§f" + name)
                                .clickEvent(ClickEvent.suggestCommand(locationString))
                                .hoverEvent(HoverEvent.showText(Component.text("Show coordinates of " + name))))
                        .append(Component.text(" §7["))
                        .append(Component.text("§a➢")
                                .clickEvent(ClickEvent.runCommand("/navigate waypoint " + scope + " " + name))
                                .hoverEvent(HoverEvent.showText(Component.text("Navigate to " + name))))
                        .append(Component.text("§e✎")
                                .clickEvent(ClickEvent.suggestCommand("/wp " + scope + " rename " + name + " "))
                                .hoverEvent(HoverEvent.showText(Component.text("Rename " + name))))
                        .append(Component.text("§d⚐")
                                .clickEvent(ClickEvent.suggestCommand("/wp " + scope + " set " + name))
                                .hoverEvent(HoverEvent.showText(Component.text("Overwrite " + name + " with your current location"))))
                        .append(Component.text("§c✖")
                                .clickEvent(ClickEvent.suggestCommand("/wp " + scope + " delete " + name))
                                .hoverEvent(HoverEvent.showText(Component.text("Delete " + name))))
                        .append(Component.text("§9➲")
                                .clickEvent(ClickEvent.suggestCommand("/wp " + invertedScope + " set " + name + " " + locationString))
                                .hoverEvent(HoverEvent.showText(Component.text("Save " + scope + " waypoint " + name + " to " + invertedScope + " waypoints"))))
                        .append(Component.text("§7]")));
            }
            return true;
        }

        if (args.length < 3) {
            sendInvalidUsage(player);
            return true;
        }

        String name = args[2];

        if (action.equals("rename")
                || action.equals("delete")) {
            if (!waypointManager.existsWaypoint(uniqueId, name)) {
                player.sendMessage(Component.text("The " + scope + " waypoint ", NamedTextColor.RED)
                        .append(Component.text(name, NamedTextColor.YELLOW))
                        .append(Component.text(" doesn't exist.", NamedTextColor.RED)));
                return true;
            }
        }

        if (scope.equals("global")
                && waypointManager.getConfig().isRequirePermissionToModifyGlobalWaypoints()
                && !player.hasPermission(Permission.WAYPOINTS_GLOBAL_ADMIN)) {
            player.sendMessage(Component.text("Sorry, you do not have the permission to modify global waypoints.", NamedTextColor.RED));
            return true;
        }

        if (action.equals("set") || action.equals("set-overwrite")) {
            Location location = null;
            if (args.length == 3) {
                location = player.getLocation().clone();
                location.setPitch(0);
                location.setYaw(0);
            } else if (args.length == 7) {
                double x, y, z;
                try {
                    x = Double.parseDouble(args[4]);
                    y = Double.parseDouble(args[5]);
                    z = Double.parseDouble(args[6]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(Component.text("Provided an invalid number for X, Y or Z coordinate.", NamedTextColor.RED));
                    return true;
                }
                World world = Bukkit.getWorld(args[3]);
                if (world == null) {
                    player.sendMessage(Component.text("No world found for the provided world name ", NamedTextColor.RED)
                            .append(Component.text(args[3], NamedTextColor.YELLOW))
                            .append(Component.text(".", NamedTextColor.RED)));
                    return true;
                }
                location = new Location(world, x, y, z);
            } else {
                sendInvalidUsage(player);
                return true;
            }

            if (action.equals("set") && waypointManager.existsWaypoint(uniqueId, name)) {
                args[1] = "set-overwrite";
                sendOverwriteWarning(player, scope, name, args);
                return true;
            }

            waypointManager.setWaypoint(uniqueId, name, location);
            player.sendMessage(Component.text("The " + scope + " waypoint ", NamedTextColor.GREEN)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(" has been " + (action.equals("set") ? "set" : "overwritten") + ".", NamedTextColor.GREEN)));
            return true;
        }

        if (action.equals("rename") || action.equals("rename-overwrite")) {
            if (args.length != 4) {
                sendSmallUsage(player, scope + " rename <Old> <New>");
                return true;
            }
            String newName = args[3];

            if (action.equals("rename") && waypointManager.existsWaypoint(uniqueId, newName)) {
                args[1] = "rename-overwrite";
                sendOverwriteWarning(player, scope, newName, args);
                return true;
            }

            waypointManager.renameWaypoint(uniqueId, name, newName);
            player.sendMessage(Component.text("The " + scope + " waypoint ", NamedTextColor.GREEN)
                    .append(Component.text(name, NamedTextColor.GRAY))
                    .append(Component.text(" has been renamed to ", NamedTextColor.GREEN))
                    .append(Component.text(newName, NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.GREEN)));
            return true;
        }

        if (action.equals("delete") || action.equals("delete-confirm")) {
            if (action.equals("delete")) {
                player.sendMessage(Component.text("Do you really want to delete the " + scope + " waypoint ", NamedTextColor.RED)
                        .append(Component.text(name, NamedTextColor.YELLOW))
                        .append(Component.text("? ", NamedTextColor.RED))
                        .append(Component.text("[CONFIRM DELETE]", NamedTextColor.DARK_RED)
                                .clickEvent(ClickEvent.runCommand("/waypoint " + scope + " delete-confirm " + name))));
                return true;
            }

            waypointManager.deleteWaypoint(uniqueId, name);
            player.sendMessage(Component.text("The " + scope + " waypoint ", NamedTextColor.RED)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(" has been deleted.", NamedTextColor.RED)));
        }

        return true;
    }

    private void sendInvalidUsage(Player player) {
        player.sendMessage(Component.text("Invalid usage. Use ", NamedTextColor.RED)
                .append(Component.text("/waypoint ", NamedTextColor.YELLOW))
                .append(Component.text("or ", NamedTextColor.RED))
                .append(Component.text("/wp ", NamedTextColor.YELLOW))
                .append(Component.text("for help.", NamedTextColor.RED)));
    }

    private void sendSmallUsage(Player player, String usage) {
        player.sendMessage(Component.text("Invalid usage. Use: ", NamedTextColor.RED)
                .append(Component.text("/wp " + usage, NamedTextColor.YELLOW)));
    }

    private void sendOverwriteWarning(Player player, String scope, String name, String[] args) {
        player.sendMessage(Component.text("The " + scope + " waypoint ", NamedTextColor.RED)
                .append(Component.text(name, NamedTextColor.YELLOW))
                .append(Component.text(" already exists. Choose another name or ", NamedTextColor.RED))
                .append(Component.text("[CLICK TO OVERWRITE]", NamedTextColor.DARK_RED)
                        .clickEvent(ClickEvent.runCommand("/waypoint " + String.join(" ", args)))));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            CommandUtil.addMatches(matches, args[0], "personal", "global");
        } else if (args.length > 1 && (args[0].equals("personal") || args[0].equals("global"))) {
            if (args.length == 2) {
                CommandUtil.addMatches(matches, args[1], "list", "set", "rename", "delete");
            } else if (args.length == 3 && (args[1].equals("rename") || args[1].equals("delete"))) {
                List<String> waypointNames = waypointManager.getWaypointNames(args[0].equals("global") ? null : player.getUniqueId());
                CommandUtil.addMatches(matches, args[2], waypointNames);
            }
        }
        return matches;
    }

}
