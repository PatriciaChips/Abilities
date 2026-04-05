package org.pat.abilities.Objects;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.pat.abilities.Utils;

public enum AbilityUtil {

    /**
     * ENUM NAME IS USED TO IDENTIFY ABILITY
     *
     * Material Identifiers can be either Tag or Material objects
     * Tag example: Tag.ITEMS_SWORDS
     */

    test(5, 5, Material.STICK, Tag.ITEMS_SWORDS, new Affinity[]{Affinity.movement});

    private long primaryCooldown;
    private long secondaryCooldown;
    private Object primaryMaterialIdentifier;
    private Object secondaryMaterialIdentifier;
    private Affinity[] affinity;

    AbilityUtil(long primaryCooldown, long secondaryCooldown, Object primaryMaterialIdentifier, Object secondaryMaterialIdentifier, Affinity[] affinity) {
        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
        this.primaryMaterialIdentifier = primaryMaterialIdentifier;
        this.secondaryMaterialIdentifier = secondaryMaterialIdentifier;
        this.affinity = affinity;
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
