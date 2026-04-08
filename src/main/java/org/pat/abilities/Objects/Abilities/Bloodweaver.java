package org.pat.abilities.Objects.Abilities;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.pat.abilities.Objects.InterfaceActions;

public interface Bloodweaver extends InterfaceActions {

    @Override
    default void runPrimaryCharge(Player p) {

    }

    @Override
    default void runSecondaryCharge(Player p) {

    }

    @Override
    default void cancelPrimaryCharge(Player p) {

    }

    @Override
    default void cancelSecondaryCharge(Player p) {

    }

    @Override
    default void runPrimary(Player p) {

    }

    @Override
    default void runSecondary(Player p) {

    }

    @Override
    default boolean tickShiftPassive(Player p) {

        return true;
    }

}
