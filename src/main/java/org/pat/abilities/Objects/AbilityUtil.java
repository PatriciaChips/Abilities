package org.pat.abilities.Objects;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Objects.Abilities.Test_Ability;
import org.pat.abilities.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum AbilityUtil {

    /**
     * ENUM NAME IS USED TO IDENTIFY ABILITY
     * <p>
     * Material Identifiers can be either Tag or Material objects
     * Tag example: Tag.ITEMS_SWORDS
     *
     * Charge duration can be 0
     * Charge duration will not exist unless a ItemUseAnimation is used
     */

    test(5, 5, Material.STICK, Tag.ITEMS_SWORDS, new Affinity[]{Affinity.movement}, ItemUseAnimation.BRUSH, null, 20, 0, builder -> {
        builder.add(InterfaceActions.class, new Test_Ability() {
        });
    });

    private long primaryCooldown;
    private long secondaryCooldown;
    private Object primaryMaterialIdentifier;
    private Object secondaryMaterialIdentifier;
    private Affinity[] affinity;

    private ItemUseAnimation primaryAnimation;
    private ItemUseAnimation secondaryAnimation;
    private long primaryChargeDuration; // TICKS
    private long secondaryChargeDuration; // TICKS

    private final HashMap<Class<?>, Object> behaviors = new HashMap<>();

    AbilityUtil(long primaryCooldown, long secondaryCooldown, Object primaryMaterialIdentifier, Object secondaryMaterialIdentifier, Affinity[] affinity, @Nullable ItemUseAnimation primaryAnimation, @Nullable ItemUseAnimation secondaryAnimation, long primaryChargeDuration, long secondaryChargeDuration, AbilityBehaviorBuilder builder) {
        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
        this.primaryMaterialIdentifier = primaryMaterialIdentifier;
        this.secondaryMaterialIdentifier = secondaryMaterialIdentifier;
        this.affinity = affinity;

        this.primaryAnimation = primaryAnimation;
        this.secondaryAnimation = secondaryAnimation;
        this.primaryChargeDuration = primaryChargeDuration;
        this.secondaryChargeDuration = secondaryChargeDuration;

        builder.build(new Builder(this));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) behaviors.get(type);
    }

    public interface AbilityBehaviorBuilder {
        void build(Builder builder);
    }

    public static class Builder {
        private final AbilityUtil util;

        public Builder(AbilityUtil util) {
            this.util = util;
        }

        public <T> Builder add(Class<T> type, T impl) {
            util.behaviors.put(type, impl);
            return this;
        }
    }

    public void runPrimaryCharge(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runPrimaryCharge(p);
    }

    public void runSecondaryCharge(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runSecondaryCharge(p);
    }

    public void runPrimary(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runPrimary(p);
    }

    public void runSecondary(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runSecondary(p);
    }

    public void tickShiftPassive(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.tickShiftPassive(p);
    }

    public void tickPassive(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.tickPassive(p);
    }

    /**
     * Ability cooldowns will be adjustable in-game via a gui at some point
     */

    public void saveToConfig() {
        Utils.scheduler.runTaskAsynchronously(Utils.plugin, () -> {
            Utils.plugin.getConfig().set("Abilities." + name() + ".primary", primaryCooldown);
            Utils.plugin.getConfig().set("Abilities." + name() + ".secondary", secondaryCooldown);
            Utils.plugin.saveConfig();
        });
    }

    public void loadFromConfig() {
        if (Utils.plugin.getConfig().contains("Abilities." + name())) {
            setPrimaryCooldown(Utils.plugin.getConfig().getLong("Abilities." + name() + ".primary"));
            setSecondaryCooldown(Utils.plugin.getConfig().getLong("Abilities." + name() + ".secondary"));
        }
    }

    public long getPrimaryCooldown() {
        return primaryCooldown;
    }

    public long getSecondaryCooldown() {
        return secondaryCooldown;
    }

    public void setPrimaryCooldown(long primaryCooldown) {
        this.primaryCooldown = primaryCooldown;
    }

    public void setSecondaryCooldown(long secondaryCooldown) {
        this.secondaryCooldown = secondaryCooldown;
    }

    public boolean isPrimaryMaterial(ItemStack item) {
        if (item == null)
            return false;

        if (primaryMaterialIdentifier instanceof Material)
            return primaryMaterialIdentifier == item.getType();

        if (primaryMaterialIdentifier instanceof Tag tag)
            return tag.isTagged(item.getType());

        return false;
    }

    public boolean isPrimaryMaterial(Material mat) {
        return isPrimaryMaterial(new ItemStack(mat));
    }

    public boolean isSecondaryMaterial(ItemStack item) {
        if (item == null)
            return false;

        if (secondaryMaterialIdentifier instanceof Material)
            return secondaryMaterialIdentifier == item.getType();

        if (secondaryMaterialIdentifier instanceof Tag tag)
            return tag.isTagged(item.getType());

        return false;
    }

    public boolean isSecondaryMaterial(Material mat) {
        return isSecondaryMaterial(new ItemStack(mat));
    }

    public Affinity getAffinity() {
        return affinity[0];
    }

    public Affinity[] getAffinities() {
        return affinity;
    }

    /**
     * Instance of Tag Object will return first material in that tag list
     */

    public Material getPrimaryMaterial() {
        if (primaryMaterialIdentifier instanceof Material material)
            return material;
        if (primaryMaterialIdentifier instanceof Tag tag)
            return (Material) tag.getValues().stream().toList().get(0);
        return null;
    }

    public Material getSecondaryMaterial() {
        if (secondaryMaterialIdentifier instanceof Material material)
            return material;
        if (secondaryMaterialIdentifier instanceof Tag tag)
            return (Material) tag.getValues().stream().toList().get(0);
        return null;
    }

    /**
     * Return a list of all materials flagged for primary/secondary
     */

    public Set<Material> getPrimaryFlaggedItemList() {
        if (primaryMaterialIdentifier instanceof Material material)
            return new HashSet<>(Collections.singleton(material));
        if (primaryMaterialIdentifier instanceof Tag tag)
            return tag.getValues();
        return new HashSet<>();
    }

    public Set<Material> getSecondaryFlaggedItemList() {
        if (secondaryMaterialIdentifier instanceof Material material)
            return new HashSet<>(Collections.singleton(material));
        if (secondaryMaterialIdentifier instanceof Tag tag)
            return tag.getValues();
        return new HashSet<>();
    }

    public ItemUseAnimation getPrimaryAnimation() {
        return primaryAnimation;
    }

    public ItemUseAnimation getSecondaryAnimation() {
        return secondaryAnimation;
    }

    public boolean hasPrimaryHaveAnimation() {
        return primaryAnimation != null;
    }

    public boolean hasSecondaryHaveAnimation() {
        return secondaryAnimation != null;
    }

    public long getPrimaryChargeDuration() {
        return primaryChargeDuration;
    }

    public long getSecondaryChargeDuration() {
        return secondaryChargeDuration;
    }

    public static boolean isChargingAbility(Player p) {
        return AbilityLogic.isEating.containsKey(p.getUniqueId());
    }

    public static boolean isChargingPrimary(Player p) {
        return AbilityLogic.isEating.containsKey(p.getUniqueId()) && AbilityLogic.isEating.get(p.getUniqueId()).left().equalsIgnoreCase("p");
    }

    public static boolean isChargingSecondary(Player p) {
        return AbilityLogic.isEating.containsKey(p.getUniqueId()) && AbilityLogic.isEating.get(p.getUniqueId()).left().equalsIgnoreCase("s");
    }
}
