package org.pat.abilities.Objects.Abilities;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Objects.InterfaceActions;
import org.pat.abilities.Utils;

public interface Test_Ability extends InterfaceActions {

    /**
     * To create a new ability copy this class and edit this methods; DO NOT RENAME THEM
     * Then you need to create a enum variable
     */

    @Override
    default void runPrimary(Player p) {
        p.sendMessage("test primary activated!");
    }

    @Override
    default void runSecondary(Player p) {
        p.sendMessage("test secondary activated!");
    }

    @Override
    default void runShiftPassive(Player p) {
        p.sendMessage("test shift passive activated!");
    }

    @Override
    default void runPassive(Player p) {
        p.sendMessage("test passive activated!");
    }

}
