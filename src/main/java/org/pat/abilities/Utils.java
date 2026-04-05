package org.pat.abilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.pat.abilities.Objects.AbilityUtil;

public class Utils {

    public static Plugin plugin = Abilities.getPlugin(Abilities.class);
    public static BukkitScheduler scheduler = plugin.getServer().getScheduler();

    public static AbilityUtil getSelectedAbility(Player p) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId()))
            return Abilities.selectedAbility.get(p.getUniqueId());
        return null;
    }

}
