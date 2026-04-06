package org.pat.abilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.pat.abilities.Objects.AbilityUtil;

import java.security.SecureRandom;

public class Utils {

    public static Plugin plugin = Abilities.getPlugin(Abilities.class);
    public static BukkitScheduler scheduler = plugin.getServer().getScheduler();

    public static AbilityUtil getSelectedAbility(Player p) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId()))
            return Abilities.selectedAbility.get(p.getUniqueId());
        return null;
    }

    private final SecureRandom random = new SecureRandom();
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private final String DIGITS = "0123456789";

    public String generateRandomID(int digitCount, int letterCount) {
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < digitCount; i++) {
            id.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        for (int i = 0; i < letterCount; i++) {
            id.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return id.toString();
    }

}
