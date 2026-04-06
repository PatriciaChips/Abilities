package org.pat.abilities.Listeners;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionReconfigureEvent;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.Abilities.Test_Ability;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.Utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class AbilityLogic implements Listener {

    static HashMap<UUID, Long> primary = new HashMap<>();
    static HashMap<UUID, Long> secondary = new HashMap<>();

    public static HashMap<UUID, Pair<String, Long>> isEating = new HashMap<>();

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

                                    /** This checks to make sure they didn't somehow keep the isEating Tag */
                                    if (!ability.hasPrimaryHaveAnimation() || (isEating.containsKey(uuid) && isEating.get(uuid).right() + (ability.getPrimaryChargeDuration() * 20 * 1000) < System.currentTimeMillis()))
                                        isEating.remove(uuid);

                                    if (!isEating.containsKey(uuid)) {

                                        /**
                                         * 1; Primary ability charge function put here
                                         */

                                        /***/
                                        if (ability.hasPrimaryHaveAnimation()) {
                                            item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getPrimaryAnimation()).consumeSeconds(ability.getPrimaryChargeDuration() / 20).build());
                                            ability.runPrimaryCharge(p);
                                            isEating.put(uuid, Pair.of("p", System.currentTimeMillis()));
                                        } else {
                                            ability.runPrimary(p);
                                            primary.put(uuid, System.currentTimeMillis() + (primaryCooldown * 1000));
                                        }
                                        /***/

                                    }
                                } else {
                                    p.sendMessage("primary cooldown -> " + df.format((double) (primary.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                    item.unsetData(DataComponentTypes.CONSUMABLE);
                                }
                            } else if (ability.isSecondaryMaterial(item)) {
                                if (!secondary.containsKey(uuid) || secondary.get(uuid) < System.currentTimeMillis()) {

                                    /** This checks to make sure they didn't somehow keep the isEating Tag */
                                    if (!ability.hasSecondaryHaveAnimation() || (isEating.containsKey(uuid) && isEating.get(uuid).right() + (ability.getSecondaryChargeDuration() * 20 * 1000) < System.currentTimeMillis()))
                                        isEating.remove(uuid);

                                    if (!isEating.containsKey(uuid)) {

                                        /**
                                         * 2; Secondary ability charge function put here
                                         */

                                        /***/
                                        if (ability.hasSecondaryHaveAnimation()) {
                                            item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getSecondaryAnimation()).build());
                                            ability.runSecondaryCharge(p);
                                            isEating.put(uuid, Pair.of("s", System.currentTimeMillis()));
                                        } else {
                                            ability.runSecondary(p);
                                            secondary.put(uuid, System.currentTimeMillis() + (secondaryCooldown * 1000));
                                        }
                                        /***/

                                    }
                                } else {
                                    p.sendMessage("secondary cooldown -> " + df.format((double) (secondary.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                    item.unsetData(DataComponentTypes.CONSUMABLE);
                                }
                            }
                            break;
                    }
                }
            }
        }

    }

    @EventHandler
    public void consume(PlayerItemConsumeEvent e) {

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
                    if (ability.isPrimaryMaterial(item)) {
                        if (!primary.containsKey(uuid) || primary.get(uuid) < System.currentTimeMillis()) {

                            /**
                             * 1; Primary ability function put here
                             */

                            /***/
                            ability.runPrimary(p);
                            e.setCancelled(true);
                            item.unsetData(DataComponentTypes.CONSUMABLE);
                            isEating.remove(uuid);
                            /***/

                            primary.remove(uuid);
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
                            e.setCancelled(true);
                            item.unsetData(DataComponentTypes.CONSUMABLE);
                            isEating.remove(uuid);
                            /***/

                            secondary.remove(uuid);
                        }

                        if (!secondary.containsKey(uuid))
                            secondary.put(uuid, System.currentTimeMillis() + (secondaryCooldown * 1000));
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
        Material mat = item != null ? item.getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid) : null;

        DecimalFormat df = new DecimalFormat("0.00");

        if (p.getGameMode() == GameMode.ADVENTURE || p.getWorld() == Bukkit.getWorld("practice") || p.getWorld() == Bukkit.getWorld("minigames")) { // THEY ARE DEAD ABORT
            return;
        }

        if (ability != null) {
            if (item != null) {
                if (ability.isPrimaryMaterial(item) || ability.isSecondaryMaterial(item)) { /** Only run shift passive if they are holding one of their ability items */
                }
            }
        }

    }

}
