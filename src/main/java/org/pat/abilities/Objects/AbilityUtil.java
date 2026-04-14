package org.pat.abilities.Objects;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.pat.abilities.Abilities;
import org.pat.abilities.Listeners.AbilityLogic;
import org.pat.abilities.Objects.Abilities.Bloodweaver;
import org.pat.abilities.Objects.Abilities.Catalyst;
import org.pat.abilities.Objects.Abilities.Test_Ability;
import org.pat.abilities.TilsU;

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
     * <p>
     * Cooldown is in seconds
     * Charge duration is in ticks
     * <p>
     * Set either shift or normal passive tickrate to <= 0 to disable either one for that ability
     * <p>
     * Charge duration can be 0, uses default eating time (32 ticks)
     * Charge duration will not exist unless a ItemUseAnimation is used
     * <p>
     * A null itemModel will return the vanilla model
     * A null chargedItemModel will return the itemModel (which uses vanilla if both are null)
     */

    test(Material.REDSTONE, 5, 5, 20, 0, Tag.ITEMS_AXES, Tag.ITEMS_SWORDS, new Affinity[]{Affinity.movement}, ItemUseAnimation.BRUSH, ItemUseAnimation.TRIDENT, 20, 0, null, null, null, null,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            builder -> {
                builder.add(InterfaceActions.class, new Test_Ability() {
                });
            }),
        catalyst(Material.ECHO_SHARD, 30, 20, 0, 2, Tag.ITEMS_SWORDS, Tag.ITEMS_AXES, new Affinity[]{Affinity.movement}, ItemUseAnimation.BLOCK, ItemUseAnimation.BLOCK, 10, 30, null, null, null, null,
            "Place beacon / Sonic boom",
            "Absorb",
            "Burrow",
            "Sculk",
            "Whilst looking at a sculk block, send a charge into the ground where you’re looking, and grow a sculk shrieker at its location (or the closest valid block)\n" +
                    "Otherwise, Fire a sonic boom in the direction you are looking. If you are standing on sculk, charge builds up around your feet and the sonic boom deals reverse knockback\n" +
            "",
            "connected sculk under where you’re standing turns into a charge and runs along the ground to your location, and is then absorbed.\n For every block of sculk that you absorb, gain 1 second of speed 3 and haste 2.",
            "If a sculk shrieker is within a 9 block radius, burrow into the ground and reappear next to the shrieker.\n If the shrieker is destroyed during the burrow, you are instantly transported to it and popped/killed.\n Shriekers within range are affected with glowing.",
                "Standing on sculk gives resistance 1 and regeneration 1",
                builder -> {
                    builder.add(InterfaceActions.class, new Catalyst() {
                    });
            }),
    bloodweaver(Material.REDSTONE, 0, 0, 30, 1, Tag.ITEMS_SWORDS, Tag.ITEMS_AXES, new Affinity[]{Affinity.combat, Affinity.movement}, ItemUseAnimation.BRUSH, ItemUseAnimation.BLOCK, 6, 2, null, null, null, null,
            "Vital Kunai",
            "Hemoflow",
            "Life-drain",
            "Blood Reservoir",
            "Shoot a selection of blood soaked kunai that steals hp from hit players and applies &nbleed&7. \n&8&nBleed&8 stacks and deals dmg over-time but will completely clear after 3 seconds unless applied again.",
            "Dash in the direction of your current horizontal velocity while on ground. \n&8While in-air conjure a platform of blood below you, only you can use.",
            "Shifting near players will steal their blood, which heals and is stored to be used for abilities.",
            "Stored blood is displayed in the xp bar, the more blood stored the more effective Life-drain becomes. \n&8Blood is self-generated at an extremely slow rate.",
            builder -> {
                builder.add(InterfaceActions.class, new Bloodweaver() {
                });

            });

    private Material guiItem;

    private long primaryCooldown;
    private long secondaryCooldown;
    private long shiftPassiveTickRate;
    private long passiveTickRate;
    private Object primaryMaterialIdentifier;
    private Object secondaryMaterialIdentifier;
    private Affinity[] affinity;

    private ItemUseAnimation primaryAnimation;
    private ItemUseAnimation secondaryAnimation;
    private long primaryChargeDuration; // TICKS
    private long secondaryChargeDuration; // TICKS

    private String primaryItemModel; // LOWERCASE
    private String secondaryItemModel; // LOWERCASE
    private String primaryChargedItemModel; // LOWERCASE
    private String secondaryChargedItemModel; // LOWERCASE

    private String primaryName;
    private String secondaryName;
    private String shiftPassiveName;
    private String passiveName;
    private String primaryDescription;
    private String secondaryDescription;
    private String shiftPassiveDescription;
    private String passiveDescription;

    private final HashMap<Class<?>, Object> behaviors = new HashMap<>();

    public static Material axeVanity = Material.MAGMA_CREAM;

    AbilityUtil(Material guiItem, long primaryCooldown, long secondaryCooldown, long shiftPassiveTickRate, long passiveTickRate, Object primaryMaterialIdentifier, Object secondaryMaterialIdentifier, Affinity[] affinity, @Nullable ItemUseAnimation primaryAnimation, @Nullable ItemUseAnimation secondaryAnimation,
                long primaryChargeDuration, long secondaryChargeDuration, @Nullable String primaryItemModel, @Nullable String secondaryItemModel, @Nullable String primaryChargedItemModel, @Nullable String secondaryChargedItemModel, String primaryName, String secondaryName, String shiftPassiveName, String passiveName, String primaryDescription, String secondaryDescription, String shiftPassiveDescription, String passiveDescription, AbilityBehaviorBuilder builder) {
        this.guiItem = guiItem;

        this.primaryCooldown = primaryCooldown;
        this.secondaryCooldown = secondaryCooldown;
        this.shiftPassiveTickRate = shiftPassiveTickRate;
        this.passiveTickRate = passiveTickRate;
        this.primaryMaterialIdentifier = primaryMaterialIdentifier;
        this.secondaryMaterialIdentifier = secondaryMaterialIdentifier;
        this.affinity = affinity;

        this.primaryAnimation = primaryAnimation;
        this.secondaryAnimation = secondaryAnimation;
        this.primaryChargeDuration = primaryChargeDuration;
        this.secondaryChargeDuration = secondaryChargeDuration;

        this.primaryItemModel = primaryItemModel;
        this.secondaryItemModel = secondaryItemModel;
        this.primaryChargedItemModel = primaryChargedItemModel;
        this.secondaryChargedItemModel = secondaryChargedItemModel;

        this.primaryName = primaryName;
        this.secondaryName = secondaryName;
        this.shiftPassiveName = shiftPassiveName;
        this.passiveName = passiveName;
        this.primaryDescription = primaryDescription;
        this.secondaryDescription = secondaryDescription;
        this.shiftPassiveDescription = shiftPassiveDescription;
        this.passiveDescription = passiveDescription;

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
        if (action != null) action.runPrimaryCharge(p, this);
    }

    public void runSecondaryCharge(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runSecondaryCharge(p, this);
    }

    public void cancelPrimaryCharge(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.cancelPrimaryCharge(p, this);
    }

    public void cancelSecondaryCharge(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.cancelSecondaryCharge(p, this);
    }

    public void runPrimary(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runPrimary(p, this);
    }

    public void runSecondary(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.runSecondary(p, this);
    }

    public void tickShiftPassive(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.tickShiftPassive(p, this);
    }

    public void tickPassive(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.tickPassive(p, this);
    }

    public void selectAbilityLogic(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.selectAbility(p, this);
    }

    public void unselectAbilityLogic(Player p) {
        InterfaceActions action = get(InterfaceActions.class);
        if (action != null) action.unselectAbility(p, this);
    }

    /**
     * Ability cooldowns will be adjustable in-game via a gui at some point
     */

    public void saveToConfig() {
        TilsU.scheduler.runTaskAsynchronously(TilsU.plugin, () -> {
            TilsU.plugin.getConfig().set("Abilities." + name() + ".primary", primaryCooldown);
            TilsU.plugin.getConfig().set("Abilities." + name() + ".secondary", secondaryCooldown);
            TilsU.plugin.saveConfig();
        });
    }

    public void loadFromConfig() {
        if (TilsU.plugin.getConfig().contains("Abilities." + name())) {
            setPrimaryCooldown(TilsU.plugin.getConfig().getLong("Abilities." + name() + ".primary"));
            setSecondaryCooldown(TilsU.plugin.getConfig().getLong("Abilities." + name() + ".secondary"));
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

        item = item.clone();

        if (item.getType() == axeVanity && item.getPersistentDataContainer().has(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING))
            item.setType(Material.valueOf(item.getPersistentDataContainer().get(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING)));

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

        item = item.clone();

        if (item.getType() == axeVanity && item.getPersistentDataContainer().has(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING))
            item.setType(Material.valueOf(item.getPersistentDataContainer().get(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING)));

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

    public boolean hasPrimaryAnimation() {
        return primaryAnimation != null;
    }

    public boolean hasSecondaryAnimation() {
        return secondaryAnimation != null;
    }

    public long getPrimaryChargeDuration() {
        if (primaryChargeDuration <= 0)
            return 32;
        return primaryChargeDuration;
    }

    public long getSecondaryChargeDuration() {
        if (secondaryChargeDuration <= 0)
            return 32;
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

    public static void selectAbility(Player p, AbilityUtil ability) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId())) {
            unselectAbility(p, ability);
        }

        String selectionId = TilsU.generateRandomID(5, 5);
        Abilities.selectedAbility.put(p.getUniqueId(), Pair.of(ability, selectionId));
        AbilityLogic.applyDataToAbilityItems(ability, p.getInventory().getStorageContents(), p);
        ability.selectAbilityLogic(p);
        if (ability.hasPassive()) {
            new BukkitRunnable() {
                public void run() {
                    if (getSelectedAbility(p) == null || !getSelectedAbilityID(p).equalsIgnoreCase(selectionId)) {
                        cancel();
                        return;
                    }

                    ability.tickPassive(p);
                }
            }.runTaskTimer(TilsU.plugin, 0L, ability.getPassiveTickRate());
        }
    }

    public static void unselectAbility(Player p, AbilityUtil ability) {
        Abilities.selectedAbility.remove(p.getUniqueId());
        ability.unselectAbilityLogic(p);
    }

    public static boolean onPrimaryCooldown(Player p) {
        return AbilityLogic.primaryCooldown.containsKey(p.getUniqueId()) && AbilityLogic.primaryCooldown.get(p.getUniqueId()) > System.currentTimeMillis();
    }

    public static boolean onSecondaryCooldown(Player p) {
        return AbilityLogic.secondaryCooldown.containsKey(p.getUniqueId()) && AbilityLogic.secondaryCooldown.get(p.getUniqueId()) > System.currentTimeMillis();
    }

    public static AbilityUtil getSelectedAbility(Player p) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId()))
            return Abilities.selectedAbility.get(p.getUniqueId()).left();
        return null;
    }

    public static String getSelectedAbilityID(Player p) {
        if (Abilities.selectedAbility.containsKey(p.getUniqueId()))
            return Abilities.selectedAbility.get(p.getUniqueId()).right();
        return null;
    }

    public String getPrimaryItemModel() {
        return primaryItemModel;
    }

    public String getSecondaryItemModel() {
        return secondaryItemModel;
    }

    public boolean hasPrimaryItemModel() {
        return getPrimaryItemModel() != null;
    }

    public boolean hasSecondaryItemModel() {
        return getSecondaryItemModel() != null;
    }

    public String getPrimaryChargedItemModel() {
        return primaryChargedItemModel;
    }

    public String getSecondaryChargedItemModel() {
        return secondaryChargedItemModel;
    }

    public boolean hasPrimaryChargedItemModel() {
        return getPrimaryChargedItemModel() != null;
    }

    public boolean hasSecondaryChargedItemModel() {
        return getSecondaryChargedItemModel() != null;
    }

    public long getPassiveTickRate() {
        return passiveTickRate;
    }

    public long getShiftPassiveTickRate() {
        return shiftPassiveTickRate;
    }

    public boolean hasPassive() {
        return getPassiveTickRate() > 0;
    }

    public boolean hasShiftPassive() {
        return getShiftPassiveTickRate() > 0;
    }

    /**
     * Strictly for axes and their annoying ass properties, just ignore this I had to make a workaround to allow axes to be edible
     */

    public static Material getHiddenMaterial(ItemStack item) {
        if (item.getType() == axeVanity && item.getPersistentDataContainer().has(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING)) {
            return Material.valueOf(item.getPersistentDataContainer().get(new NamespacedKey(TilsU.plugin, "material"), PersistentDataType.STRING));
        } else {
            return null;
        }
    }

    public Material getGuiItem() {
        return guiItem;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public String getSecondaryName() {
        return secondaryName;
    }

    public String getShiftPassiveName() {
        return shiftPassiveName;
    }

    public String getPassiveName() {
        return passiveName;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    public String getShiftPassiveDescription() {
        return shiftPassiveDescription;
    }

    public String getPassiveDescription() {
        return passiveDescription;
    }

    public static String MaterialToTangibleLoreFormat(Material mat) {
        if (Tag.ITEMS_SWORDS.isTagged(mat)) {
            return "Sword \uD83D\uDDE1";
        }
        if (Tag.ITEMS_AXES.isTagged(mat)) {
            return "Axe \uD83E\uDE93";
        }
        if (mat == Material.MACE) {
            return "Mace \uD83E\uDE93";
        }
        if (Tag.ITEMS_PICKAXES.isTagged(mat)) {
            return "Pickaxe ⛏";
        }
        return "null";
    }

    public static AbilityUtil getAbilityFromName(String name) {
        name = name.toLowerCase();
        for (var v : AbilityUtil.values()) {
            if (v.name().equalsIgnoreCase(name))
                return v;
        }
        return null;
    }

}
