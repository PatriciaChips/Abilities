package org.pat.abilities;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pat.abilities.Commands.Abug;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Listeners.DigPacketInjector;
import org.pat.abilities.Listeners.Join;
import org.pat.abilities.Objects.AbilityUtil;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.UUID;

public final class Abilities extends JavaPlugin {

    public static HashMap<UUID, AbilityUtil> selectedAbility = new HashMap<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        /** Injector function for detecting when a player cancels their ability charge */
        Utils.scheduler.runTaskLater(Utils.plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                DigPacketInjector.inject(p);
            }
        }, 20);

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
        getCommand("ability").setExecutor(new Abug());

        /** Listeners */
        getServer().getPluginManager().registerEvents(new AbilityLogic(), this);
        getServer().getPluginManager().registerEvents(new Join(), this);

    }

    @Override
    public void onDisable() {

    }
}
