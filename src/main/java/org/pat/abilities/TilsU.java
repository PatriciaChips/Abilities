package org.pat.abilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Objects.AbilityUtil;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TilsU {

    public static Plugin plugin = Abilities.getPlugin(Abilities.class);
    public static BukkitScheduler scheduler = plugin.getServer().getScheduler();

    public static AbilityUtil getSelectedAbility(Player p) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId()))
            return Abilities.selectedAbility.get(p.getUniqueId()).left();
        return null;
    }

    public static void setPrimaryCooldown(Player p, Long cooldownTime) {
        AbilityLogic.primaryCooldown.put(p.getUniqueId(),System.currentTimeMillis()+cooldownTime);
    }
    public static void getPrimaryCooldown(Player p) {
        AbilityLogic.primaryCooldown.get(p);
    }

    public static void setSecondaryCooldown(Player p, Long cooldownTime) {
        AbilityLogic.secondaryCooldown.put(p.getUniqueId(),System.currentTimeMillis()+cooldownTime);
    }
    public static void getSecondaryCooldown(Player p) {
        AbilityLogic.secondaryCooldown.get(p);
    }


    public static String formatText(String input) { // EG; BLACK_WOOL -> Black Wool
        return Arrays.stream(input.toLowerCase().split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    static final SecureRandom random = new SecureRandom();
    static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    static final String DIGITS = "0123456789";

    public static String generateRandomID(int digitCount, int letterCount) {
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < digitCount; i++) {
            id.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        for (int i = 0; i < letterCount; i++) {
            id.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        return id.toString();
    }
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");
    public static String t(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

}
