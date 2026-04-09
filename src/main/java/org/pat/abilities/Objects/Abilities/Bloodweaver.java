package org.pat.abilities.Objects.Abilities;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.Objects.InterfaceActions;
import org.pat.abilities.TilsU;
import org.pat.pattyEssentialsV3.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public interface Bloodweaver extends InterfaceActions {

    HashMap<UUID, Float> bloodStored = new HashMap<>();

    int lifedrain_radius = 5;
    float addedBloodPerEntity = 0.05F;
    float exponentialIncreaseInBlood = 1.1F;

    @Override
    default void runPrimaryCharge(Player p, AbilityUtil ability) {

    }

    @Override
    default void runSecondaryCharge(Player p, AbilityUtil ability) {

    }

    @Override
    default void cancelPrimaryCharge(Player p, AbilityUtil ability) {

    }

    @Override
    default void cancelSecondaryCharge(Player p, AbilityUtil ability) {

    }

    @Override
    default void runPrimary(Player p, AbilityUtil ability) {

    }

    @Override
    default void runSecondary(Player p, AbilityUtil ability) {

    }

    @Override
    default boolean tickShiftPassive(Player p, AbilityUtil ability) {
        for (LivingEntity entity : p.getLocation().getNearbyLivingEntities(lifedrain_radius)) {
            if (entity != p) {
                float blood = getBloodStored(p);

                if (blood == 0) {
                    setBloodStored(p, addedBloodPerEntity);
                } else {
                    float addedBlood = ((getBloodStored(p) * ((float) Math.pow(1.00F + addedBloodPerEntity, exponentialIncreaseInBlood))) - getBloodStored(p)) / 5;
                    new BukkitRunnable() {
                        int i = 1;
                        public void run() {
                            //code
                            addBloodStored(p, addedBlood);

                            if (i >= ability.getShiftPassiveTickRate())
                                cancel();
                            i++;
                        }
                    }.runTaskTimer(TilsU.plugin, 0L, 1L);
                }
            }
        }
        p.sendMessage(getBloodStored(p) + "b");
        return true;
    }

    @Override
    default boolean tickPassive(Player p, AbilityUtil ability) {
        p.setExp(bloodStored.get(p.getUniqueId()));
        return true;
    }

    @Override
    default void selectAbility(Player p, AbilityUtil ability) {
        bloodStored.put(p.getUniqueId(), 0F);
    }

    @Override
    default void unselectAbility(Player p, AbilityUtil ability) {
        bloodStored.remove(p.getUniqueId());
    }

    static float getBloodStored(Player p) {
        return bloodStored.containsKey(p.getUniqueId()) ? bloodStored.get(p.getUniqueId()) : 0F;
    }

    static void setBloodStored(Player p, Float set) {
        set = Math.min(set, 0.999F);
        set = Math.max(set, 0);
        bloodStored.put(p.getUniqueId(), set);
    }

    static void addBloodStored(Player p, Float add) {
        setBloodStored(p, getBloodStored(p) + add);
    }

}
