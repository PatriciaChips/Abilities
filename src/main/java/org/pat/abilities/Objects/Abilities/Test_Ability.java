package org.pat.abilities.Objects.Abilities;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Objects.InterfaceActions;
import org.pat.abilities.Utils;

public interface Test_Ability extends InterfaceActions {

    /**
     * To create a new ability copy this class and edit this methods; DO NOT RENAME THEM
     * Then you need to create a enum variable
     *
     * Remove the functions for running either shift or passive this tells the plugin it doesn't have one of the other
     * They MUST return true if you do want to use them
     */

    @Override
    default void runPrimaryCharge(Player p) {
        p.sendMessage("test primary charging..");
    }

    @Override
    default void runSecondaryCharge(Player p) {
        p.sendMessage("test secondary charging..");
    }


    @Override
    default void runPrimary(Player p) {
        p.sendMessage("test primary activated!");
    }

    @Override
    default void runSecondary(Player p) {
        p.sendMessage("test secondary activated!");
    }

    @Override
    default boolean tickShiftPassive(Player p) {
        p.sendMessage("test shift passive activated!");
        return true;
    }

    @Override
    default boolean tickPassive(Player p) {
        p.sendMessage("test passive activated!");
        return true;
    }

}
