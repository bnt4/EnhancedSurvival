package com.github.bnt4.enhancedsurvival.playtime;

import com.github.bnt4.enhancedsurvival.util.time.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCommand implements CommandExecutor {

    private final PlaytimeManager playtimeManager;

    public PlaytimeCommand(PlaytimeManager playtimeManager) {
        this.playtimeManager = playtimeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can only be executed as player", NamedTextColor.RED));
            return true;
        }

        if (!playtimeManager.getConfig().isPlaytime()) {
            player.sendMessage(Component.text("Playtime was disabled by the server admin", NamedTextColor.RED));
            return true;
        }

        long playtime = playtimeManager.getPlaytime(player.getUniqueId());

        if (playtime == 0) {
            player.sendMessage(Component.text("An error occurred :("));
            return true;
        }

        player.sendMessage(Component.text("Your playtime: ", NamedTextColor.WHITE)
                .append(Component.text(TimeUtil.formatDurationSeconds(playtime), NamedTextColor.GREEN)));
        return true;
    }

}
