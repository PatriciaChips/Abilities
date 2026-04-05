package org.pat.abilities.Objects.Abilities;

import org.bukkit.entity.Player;

public class Test_Ability {

    public static void runPrimary(Player p) {
        p.sendMessage("test primary activated!");
    }

    public static void runSecondary(Player p) {
        p.sendMessage("test secondary activated!");
    }

    public static void runShiftPassive(Player p) {
        p.sendMessage("test shift passive activated!");
    }

}
