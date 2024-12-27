package com.github.bnt4.enhancedsurvival.navigation;

import com.github.bnt4.enhancedsurvival.util.command.CommandUtil;
import com.github.bnt4.enhancedsurvival.waypoint.WaypointManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Shortcut for /navigate waypoint personal
 */
public class NwpCommand implements TabExecutor {

    private final WaypointManager waypointManager;

    public NwpCommand(WaypointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("");
            player.sendMessage(Component.text("Nwp Usage: ", NamedTextColor.GOLD, TextDecoration.ITALIC));

            player.sendMessage(Component.text(" /nwp <Name>")
                    .append(Component.text(" » ", NamedTextColor.DARK_GRAY))
                    .append(Component.text("Navigate to personal waypoint", NamedTextColor.GRAY)));
            player.sendMessage(Component.text(" (\"/nwp\" is a shortcut for \"/navigate waypoint personal\")", NamedTextColor.GRAY));
            return true;
        }

        player.performCommand("navigate waypoint personal " + args[0]);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            List<String> waypoints = this.waypointManager.getWaypointNames(player.getUniqueId());
            CommandUtil.addMatches(matches, args[0], waypoints);
        }
        return matches;
    }
}
