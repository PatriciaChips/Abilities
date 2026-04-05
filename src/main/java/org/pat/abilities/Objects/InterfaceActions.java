package org.pat.abilities.Objects;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Utils;

public interface InterfaceActions {

    /**
     * Basically a main interface that all the ability classes run off to save us time going and adding the check and shit into events for each new ability we add
     */

    void runPrimary(Player p);

    void runSecondary(Player p);

    void runShiftPassive(Player p);

    void runPassive(Player p);
}
