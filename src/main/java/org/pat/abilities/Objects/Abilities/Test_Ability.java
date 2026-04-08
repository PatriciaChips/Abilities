package org.pat.abilities.Objects.Abilities;

import org.bukkit.Sound;
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

    /**
     * You do not need the run or cancel method if you do not have an itemUseAnimation as part of either your primary or secondary
     *
     * The cancel primary charge is a method that runs as its canceled which can be used to cancel any sounds for example
     * more commonly though you will need to check if the player is still charging for each action in the charge function and return if they are no longer charging either ability
     */

    @Override
    default void runPrimaryCharge(Player p) {
        p.sendMessage("test primary charging..");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    @Override
    default void runSecondaryCharge(Player p) {
        p.sendMessage("test secondary charging..");
        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    @Override
    default void cancelPrimaryCharge(Player p) {
        p.sendMessage("test primary charged cancelled");
    }

    @Override
    default void cancelSecondaryCharge(Player p) {
        p.sendMessage("test secondary charged cancelled");
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
        p.sendMessage("test shift passive ticked!");
        return true;
    }

    @Override
    default boolean tickPassive(Player p) {
        p.sendMessage("test passive ticked!");
        return true;
    }

}
