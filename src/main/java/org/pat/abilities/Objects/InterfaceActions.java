package org.pat.abilities.Objects;

import org.bukkit.entity.Player;

public interface InterfaceActions {

    /**
     * Basically a main interface that all the ability classes run off to save us time going and adding the check and shit into events for each new ability we add
     */

    void runPrimaryCharge(Player p, AbilityUtil ability);

    void runSecondaryCharge(Player p, AbilityUtil ability);

    void cancelPrimaryCharge(Player p, AbilityUtil ability);

    void cancelSecondaryCharge(Player p, AbilityUtil ability);

    void runPrimary(Player p, AbilityUtil ability);

    void runSecondary(Player p, AbilityUtil ability);

    default boolean tickShiftPassive(Player p, AbilityUtil ability) {
        return false;
    }

    default boolean tickPassive(Player p, AbilityUtil ability) {
        return false;
    }

    // in the instance you wanna add the player to a map in the ability interface and store stuff in which u have to clear later in the unselectAbility function
    void selectAbility(Player p, AbilityUtil ability);

    // works together with selectAbility
    void unselectAbility(Player p, AbilityUtil ability);
}
