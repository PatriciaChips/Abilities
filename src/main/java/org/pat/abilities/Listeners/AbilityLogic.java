package org.pat.abilities.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.Abilities.Test_Ability;
import org.pat.abilities.Objects.AbilityUtil;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class AbilityLogic implements Listener {

    HashMap<UUID, Long> primary = new HashMap<>();
    HashMap<UUID, Long> secondary = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = e.getItem();
        Material mat = e.getItem() != null ? e.getItem().getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid) : null;

        DecimalFormat df = new DecimalFormat("0.00");

        if (p.getGameMode() == GameMode.ADVENTURE || p.getWorld() == Bukkit.getWorld("practice") || p.getWorld() == Bukkit.getWorld("minigames")) { // THEY ARE DEAD ABORT
            return;
        }

        if (ability != null) {

            long primaryCooldown = ability.getPrimaryCooldown();
            long secondaryCooldown = ability.getSecondaryCooldown();

            if (e.getHand() == EquipmentSlot.HAND) {
                if (item != null) {
                    switch (e.getAction()) {
                        case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK:
                            if (ability.isPrimaryMaterial(item)) {
                                if (!primary.containsKey(uuid) || primary.get(uuid) < System.currentTimeMillis()) {

                                    /**
                                     * 1; Primary ability function put here
                                     */

                                    /***/
                                    ability.runPrimary(p);
                                    /***/

                                    primary.remove(uuid);
                                } else {
                                    p.sendMessage("primary cooldown -> " + df.format((double) (primary.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                }

                                if (!primary.containsKey(uuid))
                                    primary.put(uuid, System.currentTimeMillis() + (primaryCooldown * 1000));
                            } else if (ability.isSecondaryMaterial(item)) {
                                if (!secondary.containsKey(uuid) || secondary.get(uuid) < System.currentTimeMillis()) {

                                    /**
                                     * 2; Secondary ability function put here
                                     */

                                    /***/
                                    ability.runSecondary(p);
                                    /***/

                                    secondary.remove(uuid);
                                } else {
                                    p.sendMessage("secondary cooldown -> " + df.format((double) (secondary.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                }

                                if (!secondary.containsKey(uuid))
                                    secondary.put(uuid, System.currentTimeMillis() + (secondaryCooldown * 1000));
                            }
                            break;
                    }
                }
            }
        }

    }

    @EventHandler
    public void shift(PlayerToggleSneakEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = p.getInventory().getItemInMainHand();
        Material mat = e.getItem() != null ? e.getItem().getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid) : null;

        DecimalFormat df = new DecimalFormat("0.00");

        if (p.getGameMode() == GameMode.ADVENTURE || p.getWorld() == Bukkit.getWorld("practice") || p.getWorld() == Bukkit.getWorld("minigames")) { // THEY ARE DEAD ABORT
            return;
        }
    }

}
