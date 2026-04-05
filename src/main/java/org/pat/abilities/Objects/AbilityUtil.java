package org.pat.abilities.Objects;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Objects.Abilities.Test_Ability;
import org.pat.abilities.Utils;

import java.util.HashMap;

public enum AbilityUtil {

    /**
     * ENUM NAME IS USED TO IDENTIFY ABILITY
     * <p>
     * Material Identifiers can be either Tag or Material objects
     * Tag example: Tag.ITEMS_SWORDS
     */

    test(5, 5, Material.STICK, Tag.ITEMS_SWORDS, new Affinity[]{Affinity.movement}, builder -> {
        builder.add(InterfaceActions.class, new Test_Ability() {
        });
    });

    private long primaryCooldown;
    private long secondaryCooldown;
    private Object primaryMaterialIdentifier;
    private Object secondaryMaterialIdentifier;
    private Affinity[] affinity;

    private final HashMap<Class<?>, Object> behaviors = new HashMap<>();

    AbilityUtil(long primaryCooldown, long secondaryCooldown, Object primaryMaterialIdentifier, Object secondaryMaterialIdentifier, Affinity[] affinity, AbilityBehaviorBuilder builder) {
        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
        this.primaryMaterialIdentifier = primaryMaterialIdentifier;
        this.secondaryMaterialIdentifier = secondaryMaterialIdentifier;
        this.affinity = affinity;

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
        if (action != null) action.runShiftPassive(p);
    }

    public void tickPassive(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runPassive(p);
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
}
