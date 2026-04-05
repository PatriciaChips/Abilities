package org.pat.abilities;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pat.abilities.Commands.Abug;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Objects.AbilityUtil;

import java.util.HashMap;
import java.util.UUID;

public final class Abilities extends JavaPlugin {

    public static HashMap<UUID, AbilityUtil> selectedAbility = new HashMap<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        /** Write shit to config if it isn't already - While also adding any custom changed cooldowns 💦 */
        for (AbilityUtil abil : AbilityUtil.values()) {
            if (!getConfig().contains("Abilities." + abil.name())) {
                abil.saveToConfig();
            } else {
                abil.loadFromConfig();
            }
        }

        /** Commands */
        getCommand("abug").setExecutor(new Abug());

        /** Listeners */
         getServer().getPluginManager().registerEvents(new AbilityLogic(), this);

    }

    @Override
    public void onDisable() {

    }
}
