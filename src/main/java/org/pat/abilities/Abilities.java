package org.pat.abilities;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pat.abilities.Commands.Ability;
import org.pat.abilities.Commands.Abug;
import org.pat.abilities.Listeners.*;
import org.pat.abilities.Objects.Abilities.Catalyst;
import org.pat.abilities.Objects.AbilityUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Abilities extends JavaPlugin {

    public static HashMap<UUID, Pair<AbilityUtil, String>> selectedAbility = new HashMap<>();

    // So I have a way to stop players from selecting an ability from the GUI
    public static Set<UUID> abilityBanned = new HashSet<>();



    @Override
    public void onEnable() {

        saveDefaultConfig();

        /** Injector function for detecting when a player cancels their ability charge */
        for (Player p : Bukkit.getOnlinePlayers()) {
            FoodCancelInjector.inject(p);
        }

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
        getCommand("ability").setExecutor(new Ability());

        /** Listeners */
        getServer().getPluginManager().registerEvents(new AbilityLogic(), this);
        getServer().getPluginManager().registerEvents(new Join(), this);
        getServer().getPluginManager().registerEvents(new ClickInv(), this);
        getServer().getPluginManager().registerEvents(new CatalystCorruptStacks(), this);

    }

    @Override
    public void onDisable() {

        for (Block shriekers : Catalyst.shriekers.keySet()) {
            shriekers.setType(Material.AIR);
            Catalyst.shriekers.get(shriekers).remove();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                ServerPlayer sp = ((CraftPlayer) p).getHandle();
                net.minecraft.network.Connection mcConn = (Connection) FoodCancelInjector.CONNECTION_FIELD.get(sp.connection);

                io.netty.channel.Channel channel = (Channel) FoodCancelInjector.REAL_CHANNEL_FIELD.get(mcConn);

                if (channel != null) {
                    String handlerName = "abilities_dig_" + p.getUniqueId();
                    if (channel.pipeline().get(handlerName) != null) {
                        channel.pipeline().remove(handlerName);
                        System.out.println("Removed handler for " + p.getName());
                    }
                }
            } catch (Exception ignored) {}
        }


    }
}
