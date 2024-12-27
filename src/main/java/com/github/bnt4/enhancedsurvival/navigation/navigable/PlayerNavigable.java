package com.github.bnt4.enhancedsurvival.navigation.navigable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerNavigable implements Navigable {

    private final UUID targetUniqueId;
    private final String targetName;

    public PlayerNavigable(Player target) {
        this.targetUniqueId = target.getUniqueId();
        this.targetName = target.getName();
    }

    @Override
    public String getName() {
        return targetName;
    }

    @Override
    public Location getTarget() {
        Player target = Bukkit.getPlayer(targetUniqueId);
        if (target != null) {
            return target.getLocation().clone().add(0, 1, 0);
        }
        return null;
    }

    @Override
    public String getPausedReason() {
        if (getTarget() == null) {
            return "Target " + getName() + " offline";
        }
        return null;
    }

}
