package org.pat.abilities.Objects;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Utils;

public interface InterfaceActions {

    /**
     * Basically a main interface that all the ability classes run off to save us time going and adding the check and shit into events for each new ability we add
     */

    void runPrimaryCharge(Player p);

    void runSecondaryCharge(Player p);

    void cancelPrimaryCharge(Player p);

    void cancelSecondaryCharge(Player p);

    void runPrimary(Player p);

    void runSecondary(Player p);

    default boolean tickShiftPassive(Player p) {
        return false;
    }

    default boolean tickPassive(Player p) {
        return false;
    }
}
