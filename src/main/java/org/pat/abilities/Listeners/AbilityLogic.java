package org.pat.abilities.Listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.CustomModelData;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.Utils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;

public class AbilityLogic implements Listener {

    public static HashMap<UUID, Long> primaryCooldown = new HashMap<>();
    public static HashMap<UUID, Long> secondaryCooldown = new HashMap<>();

    public static HashMap<UUID, Pair<String, Long>> isEating = new HashMap<>();

    @EventHandler
    public void interact(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = e.getItem();
        Material mat = e.getItem() != null ? e.getItem().getType() : null;
        AbilityUtil ability = Utils.getSelectedAbility(p) != null ? Utils.getSelectedAbility(p) : null;

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
                                if (!AbilityUtil.onPrimaryCooldown(p)) {

                                    /** This checks to make sure they didn't somehow keep the isEating Tag */
                                    if (!ability.hasPrimaryAnimation() || (isEating.containsKey(uuid) && isEating.get(uuid).left().equalsIgnoreCase("s")) || (isEating.containsKey(uuid) && isEating.get(uuid).right() + (ability.getPrimaryChargeDuration() / 20 / 1000) < System.currentTimeMillis()))
                                        isEating.remove(uuid);


                                    if (!isEating.containsKey(uuid)) {

                                        /**
                                         * 1; Primary ability charge function put here
                                         */

                                        /***/
                                        if (ability.hasPrimaryAnimation()) {
                                            setPrimaryItemData(item, ability);
                                            ability.runPrimaryCharge(p);
                                            isEating.put(uuid, Pair.of("p", System.currentTimeMillis()));
                                        } else {
                                            ability.runPrimary(p);
                                            AbilityLogic.primaryCooldown.put(uuid, System.currentTimeMillis() + (primaryCooldown * 1000));
                                            applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                        }
                                        /***/

                                    }

                                } else {
                                    p.sendMessage("primary cooldown -> " + df.format((double) (AbilityLogic.primaryCooldown.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                    item.unsetData(DataComponentTypes.CONSUMABLE);
                                }
                            } else if (ability.isSecondaryMaterial(item)) {
                                if (!AbilityUtil.onSecondaryCooldown(p)) {

                                    /** This checks to make sure they didn't somehow keep the isEating Tag */
                                    if (!ability.hasSecondaryAnimation() || (isEating.containsKey(uuid) && isEating.get(uuid).left().equalsIgnoreCase("p")) || (isEating.containsKey(uuid) && isEating.get(uuid).right() + (ability.getSecondaryChargeDuration() / 20 / 1000) < System.currentTimeMillis()))
                                        isEating.remove(uuid);

                                    if (!isEating.containsKey(uuid)) {

                                        /**
                                         * 2; Secondary ability charge function put here
                                         */

                                        /***/
                                        if (ability.hasSecondaryAnimation()) {
                                            setSecondaryItemData(item, ability);
                                            ability.runSecondaryCharge(p);
                                            isEating.put(uuid, Pair.of("s", System.currentTimeMillis()));
                                        } else {
                                            ability.runSecondary(p);
                                            AbilityLogic.secondaryCooldown.put(uuid, System.currentTimeMillis() + (secondaryCooldown * 1000));
                                            applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                        }
                                        /***/

                                    }

                                } else {
                                    p.sendMessage("secondary cooldown -> " + df.format((double) (AbilityLogic.secondaryCooldown.get(uuid) - System.currentTimeMillis()) / 1000) + "s");
                                    item.unsetData(DataComponentTypes.CONSUMABLE);
                                }
                            }
                            break;
                    }
                }
            } else {
                if (ability.isPrimaryMaterial(item) || ability.isSecondaryMaterial(item)) {
                    if (item.hasData(DataComponentTypes.CONSUMABLE)) {
                        item.unsetData(DataComponentTypes.CONSUMABLE);
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
                        if (!AbilityLogic.primaryCooldown.containsKey(uuid) || AbilityLogic.primaryCooldown.get(uuid) < System.currentTimeMillis()) {

                            /**
                             * 1; Primary ability function put here
                             */

                            /***/
                            ability.runPrimary(p);
                            e.setCancelled(true);
                            item.unsetData(DataComponentTypes.CONSUMABLE);
                            isEating.remove(uuid);
                            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                if (Utils.getSelectedAbility(p) != null) {
                                    applyDataToAbilityItems(ability, p.getInventory().getStorageContents());
                                    applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                }
                            }, (ability.getPrimaryCooldown() * 20) + 1);
                            /***/

                            AbilityLogic.primaryCooldown.remove(uuid);
                        }

                        if (!AbilityLogic.primaryCooldown.containsKey(uuid)) {
                            AbilityLogic.primaryCooldown.put(uuid, System.currentTimeMillis() + (primaryCooldown * 1000));
                            applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                        }
                    } else if (ability.isSecondaryMaterial(item)) {
                        if (!AbilityLogic.secondaryCooldown.containsKey(uuid) || AbilityLogic.secondaryCooldown.get(uuid) < System.currentTimeMillis()) {

                            /**
                             * 2; Secondary ability function put here
                             */

                            /***/
                            ability.runSecondary(p);
                            e.setCancelled(true);
                            item.unsetData(DataComponentTypes.CONSUMABLE);
                            isEating.remove(uuid);
                            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                if (Utils.getSelectedAbility(p) != null) {
                                    applyDataToAbilityItems(ability, p.getInventory().getStorageContents());
                                    applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                }
                            }, (ability.getPrimaryCooldown() * 20) + 1);
                            /***/

                            AbilityLogic.secondaryCooldown.remove(uuid);
                        }

                        if (!AbilityLogic.secondaryCooldown.containsKey(uuid)) {
                            AbilityLogic.secondaryCooldown.put(uuid, System.currentTimeMillis() + (secondaryCooldown * 1000));
                            applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void swapItem(PlayerSwapHandItemsEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = e.getOffHandItem();
        Material mat = item != null ? item.getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid) : null;

        DecimalFormat df = new DecimalFormat("0.00");

        if (p.getGameMode() == GameMode.ADVENTURE || p.getWorld() == Bukkit.getWorld("practice") || p.getWorld() == Bukkit.getWorld("minigames")) { // THEY ARE DEAD ABORT
            return;
        }

        if (ability != null) {
            if (item != null && !item.hasData(DataComponentTypes.CONSUMABLE)) {
                if (ability.isPrimaryMaterial(item)) {
                    if (!AbilityUtil.onPrimaryCooldown(p)) {
                        if (ability.hasPrimaryAnimation()) {
                            setPrimaryItemData(item, ability);
                        }
                    }
                } else if (ability.isSecondaryMaterial(item)) {
                    if (!AbilityUtil.onSecondaryCooldown(p)) {
                        if (ability.hasSecondaryAnimation()) {
                            setSecondaryItemData(item, ability);
                        }
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

    public static void applyDataToAbilityItems(AbilityUtil ability, ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null) {
                if (ability.isPrimaryMaterial(item)) {
                    setPrimaryItemData(item, ability);
                } else if (ability.isSecondaryMaterial(item)) {
                    setSecondaryItemData(item, ability);
                }
            }
        }
    }

    public static void setPrimaryItemData(ItemStack item, AbilityUtil ability) {
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getPrimaryAnimation()).hasConsumeParticles(false).sound(Key.key("12345", "e")).consumeSeconds((float) ability.getPrimaryChargeDuration() / 20F).build());
    }

    public static void setSecondaryItemData(ItemStack item, AbilityUtil ability) {
        item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getSecondaryAnimation()).hasConsumeParticles(false).sound(Key.key("12345", "e")).consumeSeconds((float) ability.getSecondaryChargeDuration() / 20F).build());
    }

    public static void applyItemModelData(AbilityUtil ability, ItemStack[] items, Player p) {
        for (ItemStack item : items) {
            ItemMeta im = item.getItemMeta();
            if (ability.isPrimaryMaterial(item)) {
                if (item != null) {
                    if (AbilityUtil.onPrimaryCooldown(p)) {
                        if (ability.hasPrimaryItemModel()) {
                            im.setItemModel(new NamespacedKey("abilities", ability.getPrimaryItemModel()));
                        } else {
                            im.setItemModel(null);
                        }
                    } else {
                        if (ability.hasPrimaryChargedItemModel()) {
                            im.setItemModel(new NamespacedKey("abilities", ability.getPrimaryChargedItemModel()));
                        } else {
                            im.setItemModel(ability.hasPrimaryItemModel() ? new NamespacedKey("abilities", ability.getPrimaryItemModel()):null);
                        }
                    }
                }
            } else if (ability.isSecondaryMaterial(item)) {
                if (AbilityUtil.onSecondaryCooldown(p)) {
                    if (ability.hasSecondaryItemModel()) {
                        im.setItemModel(new NamespacedKey("abilities", ability.getSecondaryItemModel()));
                    } else {
                        im.setItemModel(null);
                    }
                } else {
                    if (ability.hasSecondaryChargedItemModel()) {
                        im.setItemModel(new NamespacedKey("abilities", ability.getSecondaryChargedItemModel()));
                    } else {
                        im.setItemModel(ability.hasSecondaryItemModel() ? new NamespacedKey("abilities", ability.getSecondaryItemModel()):null);
                    }
                }
            }
            item.setItemMeta(im);
        }
    }

}
