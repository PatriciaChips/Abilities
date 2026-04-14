package org.pat.abilities.Listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.TilsU;
import org.pat.pattyEssentialsV3.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        AbilityUtil ability = TilsU.getSelectedAbility(p) != null ? TilsU.getSelectedAbility(p) : null;

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
                                    p.sendMessage(TilsU.createMsg(Utils.red + "Primary on cooldown! &7" + df.format((double) (AbilityLogic.secondaryCooldown.get(uuid) - System.currentTimeMillis()) / 1000) + "s"));
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
                                    p.sendMessage(TilsU.createMsg(Utils.red + "Secondary on cooldown! &7" + df.format((double) (AbilityLogic.secondaryCooldown.get(uuid) - System.currentTimeMillis()) / 1000) + "s"));
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
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid).left() : null;

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
                            TilsU.scheduler.runTaskLater(TilsU.plugin, () -> {
                                if (TilsU.getSelectedAbility(p) != null) {
                                    applyDataToAbilityItems(ability, p.getInventory().getStorageContents(), p);
                                    TilsU.scheduler.runTaskLater(TilsU.plugin, () -> {
                                        applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                    }, 2);
                                }
                            }, (ability.getPrimaryCooldown() * 20) - 1);
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
                            TilsU.scheduler.runTaskLater(TilsU.plugin, () -> {
                                if (TilsU.getSelectedAbility(p) != null) {
                                    applyDataToAbilityItems(ability, p.getInventory().getStorageContents(), p);
                                    TilsU.scheduler.runTaskLater(TilsU.plugin, () -> {
                                        applyItemModelData(ability, p.getInventory().getStorageContents(), p);
                                    }, 2);
                                }
                            }, (ability.getPrimaryCooldown() * 20) - 1);
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
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid).left() : null;

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

    public static HashMap<UUID, Pair<String, Long>> shiftRunnables = new HashMap<>();

    @EventHandler
    public void shift(PlayerToggleSneakEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ItemStack item = p.getInventory().getItemInMainHand();
        Material mat = item != null ? item.getType() : null;
        AbilityUtil ability = Abilities.selectedAbility.containsKey(uuid) ? Abilities.selectedAbility.get(uuid).left() : null;

        DecimalFormat df = new DecimalFormat("0.00");

        if (p.getGameMode() == GameMode.ADVENTURE || p.getWorld() == Bukkit.getWorld("practice") || p.getWorld() == Bukkit.getWorld("minigames")) { // THEY ARE DEAD ABORT
            return;
        }

        if (ability != null) {
            if (item != null) {
                if (ability.hasShiftPassive()) {
                    String shiftID = TilsU.generateRandomID(5, 5);
                    long time = shiftRunnables.containsKey(uuid) ? shiftRunnables.get(uuid).right() : 0;
                    shiftRunnables.put(uuid, Pair.of(shiftID, System.currentTimeMillis() + (ability.getShiftPassiveTickRate() * 50)));
                    new BukkitRunnable() {
                        public void run() {
                            if (AbilityUtil.getSelectedAbility(p) == null || !shiftRunnables.containsKey(uuid) || !shiftRunnables.get(uuid).left().equalsIgnoreCase(shiftID)) {
                                cancel();
                                return;
                            }

                            if (p.isSneaking()) {
                                if (ability.isPrimaryMaterial(p.getInventory().getItemInMainHand()) || ability.isSecondaryMaterial(p.getInventory().getItemInMainHand())) { /** Only tick shift passive if they are holding one of their ability items */
                                    ability.tickShiftPassive(p);
                                    shiftRunnables.put(uuid, Pair.of(shiftID, System.currentTimeMillis() + (ability.getShiftPassiveTickRate() * 50)));
                                }
                            }
                        }
                    }.runTaskTimer(TilsU.plugin, shiftRunnables.containsKey(uuid) && time - System.currentTimeMillis() > 0 ? (time - System.currentTimeMillis()) / 50 : 0, ability.getShiftPassiveTickRate());
                }
                // runnable is delayed IF the player had just shifted
            }
        }

    }

    public static void applyDataToAbilityItems(AbilityUtil ability, ItemStack[] items, Player p) {
        for (ItemStack item : items) {
            if (item != null) {
                Material material = item.clone().getType();
                boolean isAbilityItem = false;
                if (ability.isPrimaryMaterial(item)) {
                    setPrimaryItemData(item, ability);
                    isAbilityItem = true;
                } else if (ability.isSecondaryMaterial(item)) {
                    setSecondaryItemData(item, ability);
                    isAbilityItem = true;
                }
                if (isAbilityItem) {
                    if (Tag.ITEMS_AXES.isTagged(material)) {
                        item.setType(AbilityUtil.axeVanity);
                        ItemMeta im = item.getItemMeta();

                        ItemStack axe = new ItemStack(material);
                        if (axe.hasData(DataComponentTypes.MAX_DAMAGE))
                            item.setData(DataComponentTypes.MAX_DAMAGE, axe.getData(DataComponentTypes.MAX_DAMAGE));
                        if (axe.hasData(DataComponentTypes.DAMAGE))
                            item.setData(DataComponentTypes.DAMAGE, axe.getData(DataComponentTypes.DAMAGE));
                        if (axe.hasData(DataComponentTypes.USE_EFFECTS))
                            item.setData(DataComponentTypes.USE_EFFECTS, axe.getData(DataComponentTypes.USE_EFFECTS));
                        if (axe.hasData(DataComponentTypes.CUSTOM_NAME))
                            item.setData(DataComponentTypes.CUSTOM_NAME, axe.getData(DataComponentTypes.CUSTOM_NAME));
                        if (axe.hasData(DataComponentTypes.ITEM_NAME))
                            item.setData(DataComponentTypes.ITEM_NAME, axe.getData(DataComponentTypes.ITEM_NAME));
                        if ((ability.isPrimaryMaterial(material) && !ability.hasPrimaryItemModel()) || (ability.isSecondaryMaterial(material) && !ability.hasSecondaryItemModel())) {
                            im.setItemModel(new NamespacedKey("minecraft", material.name().toLowerCase()));
                        } else {
                            applyItemModelData(ability, items, p);
                        }
                        if (axe.hasData(DataComponentTypes.LORE))
                            item.setData(DataComponentTypes.LORE, axe.getData(DataComponentTypes.LORE));
                        if (axe.hasData(DataComponentTypes.RARITY))
                            item.setData(DataComponentTypes.RARITY, axe.getData(DataComponentTypes.RARITY));
                        if (axe.hasData(DataComponentTypes.ENCHANTMENTS))
                            item.setData(DataComponentTypes.ENCHANTMENTS, axe.getData(DataComponentTypes.ENCHANTMENTS));
                        if (axe.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS))
                            item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, axe.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS));
                        if (axe.hasData(DataComponentTypes.TOOLTIP_DISPLAY))
                            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, axe.getData(DataComponentTypes.TOOLTIP_DISPLAY));
                        if (axe.hasData(DataComponentTypes.REPAIR_COST))
                            item.setData(DataComponentTypes.REPAIR_COST, axe.getData(DataComponentTypes.REPAIR_COST));
                        if (axe.hasData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE))
                            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, axe.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
                        if (axe.hasData(DataComponentTypes.DAMAGE_RESISTANT))
                            item.setData(DataComponentTypes.DAMAGE_RESISTANT, axe.getData(DataComponentTypes.DAMAGE_RESISTANT));
                        if (axe.hasData(DataComponentTypes.TOOL))
                            item.setData(DataComponentTypes.TOOL, axe.getData(DataComponentTypes.TOOL));
                        if (axe.hasData(DataComponentTypes.WEAPON))
                            item.setData(DataComponentTypes.WEAPON, axe.getData(DataComponentTypes.WEAPON));
                        if (axe.hasData(DataComponentTypes.ENCHANTABLE))
                            item.setData(DataComponentTypes.ENCHANTABLE, axe.getData(DataComponentTypes.ENCHANTABLE));
                        if (axe.hasData(DataComponentTypes.REPAIRABLE))
                            item.setData(DataComponentTypes.REPAIRABLE, axe.getData(DataComponentTypes.REPAIRABLE));
                        if (axe.hasData(DataComponentTypes.SWING_ANIMATION))
                            item.setData(DataComponentTypes.SWING_ANIMATION, axe.getData(DataComponentTypes.SWING_ANIMATION));
                        if (axe.hasData(DataComponentTypes.STORED_ENCHANTMENTS))
                            item.setData(DataComponentTypes.STORED_ENCHANTMENTS, axe.getData(DataComponentTypes.STORED_ENCHANTMENTS));
                        im.getPersistentDataContainer().set(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING, material.name());
                        item.setItemMeta(im);
                    }
                }
            }
        }
    }

    public static void setPrimaryItemData(ItemStack item, AbilityUtil ability) {
        if (ability.hasPrimaryAnimation()) {
            item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getPrimaryAnimation()).hasConsumeParticles(false).sound(Key.key("12345", "e")).consumeSeconds((float) ability.getPrimaryChargeDuration() / 20F).build());
        }
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>();

    }

    public static void setSecondaryItemData(ItemStack item, AbilityUtil ability) {
        if (ability.hasSecondaryAnimation()) {
            item.setData(DataComponentTypes.CONSUMABLE, Consumable.consumable().animation(ability.getSecondaryAnimation()).hasConsumeParticles(false).sound(Key.key("12345", "e")).consumeSeconds((float) ability.getSecondaryChargeDuration() / 20F).build());
        }
    }

    public static void applyItemModelData(AbilityUtil ability, ItemStack[] items, Player p) {
        for (ItemStack item : items) {
            if (item == null)
                continue;

            ItemMeta im = item.getItemMeta();
            if (ability.isPrimaryMaterial(item)) {
                if (item != null) {
                    if (AbilityUtil.onPrimaryCooldown(p)) {
                        if (ability.hasPrimaryItemModel()) {
                            im.setItemModel(new NamespacedKey("abilities", ability.getPrimaryItemModel()));
                        } else {
                            if (AbilityUtil.getHiddenMaterial(item) != null) {
                                im.setItemModel(new NamespacedKey("minecraft", AbilityUtil.getHiddenMaterial(item).name().toLowerCase()));
                            } else {
                                im.setItemModel(null);
                            }
                        }
                    } else {
                        if (ability.hasPrimaryChargedItemModel()) {
                            im.setItemModel(new NamespacedKey("abilities", ability.getPrimaryChargedItemModel()));
                        } else {
                            if (ability.hasPrimaryChargedItemModel()) {
                                im.setItemModel(new NamespacedKey("abilities", ability.getPrimaryChargedItemModel()));
                            } else {
                                if (AbilityUtil.getHiddenMaterial(item) != null) {
                                    im.setItemModel(new NamespacedKey("minecraft", AbilityUtil.getHiddenMaterial(item).name().toLowerCase()));
                                } else {
                                    im.setItemModel(null);
                                }
                            }
                        }
                    }
                }
            } else if (ability.isSecondaryMaterial(item)) {
                if (AbilityUtil.onSecondaryCooldown(p)) {
                    if (ability.hasSecondaryItemModel()) {
                        im.setItemModel(new NamespacedKey("abilities", ability.getSecondaryItemModel()));
                    } else {
                        if (AbilityUtil.getHiddenMaterial(item) != null) {
                            im.setItemModel(new NamespacedKey("minecraft", AbilityUtil.getHiddenMaterial(item).name().toLowerCase()));
                        } else {
                            im.setItemModel(null);
                        }
                    }
                } else {
                    if (ability.hasSecondaryChargedItemModel()) {
                        im.setItemModel(new NamespacedKey("abilities", ability.getSecondaryChargedItemModel()));
                    } else {
                        if (ability.hasSecondaryChargedItemModel()) {
                            im.setItemModel(new NamespacedKey("abilities", ability.getSecondaryChargedItemModel()));
                        } else {
                            if (AbilityUtil.getHiddenMaterial(item) != null) {
                                im.setItemModel(new NamespacedKey("minecraft", AbilityUtil.getHiddenMaterial(item).name().toLowerCase()));
                            } else {
                                im.setItemModel(null);
                            }
                        }
                    }
                }
            }
            item.setItemMeta(im);
        }
    }

}
