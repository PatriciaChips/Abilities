package org.pat.abilities.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.pat.abilities.Objects.Abilities.Catalyst;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.TilsU;

import java.util.HashMap;

public class CatalystCorruptStacks implements Listener {

    public static HashMap<Player,Integer> corruptionStacks = new HashMap<>();
    public static  HashMap<Player,BukkitTask> secondaryChargeRunnable = new HashMap<>();
    public static HashMap<Player,Integer> numBlocksAbsorbed = new HashMap<>();
    @EventHandler
    public void applyCorruptStack (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            if (TilsU.getSelectedAbility(p).equals(AbilityUtil.catalyst)) {
                p.sendMessage("apply stack works");
                if (AbilityUtil.getSelectedAbility(p).isPrimaryMaterial(p.getInventory().getItemInMainHand()) && e.getEntity() instanceof Player damaged) {
                    if (e.getDamage() > 8) {
                        if (corruptionStacks.get(damaged) == null) {
                            corruptionStacks.put(damaged, 1);
                        } else {
                            if (corruptionStacks.get(damaged) + 1 < 5) {
                                corruptionStacks.put(damaged, corruptionStacks.get(damaged) + 1);
                            } else {
                                corruptionStacks.put(damaged, 5);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void popCorruptStack (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            if (TilsU.getSelectedAbility(p).equals(AbilityUtil.catalyst)) {
                p.sendMessage("destroy stack works");
                if (AbilityUtil.getSelectedAbility(p).isSecondaryMaterial(p.getInventory().getItemInMainHand()) && e.getEntity() instanceof Player damaged) {
                    p.sendMessage("netherite axe");
                    if (e.getDamage() > 10 && corruptionStacks.get(damaged) != null) {
                        p.sendMessage("damage > 10");
                        corruptionStacks.remove(damaged);
                        damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2, 20));
                        Catalyst.spread(getHighestBlockBelow((int) damaged.getLocation().y(),damaged.getLocation()).getLocation(),0,0,10,1,0);
                    }
                }
            }
        }
    }

    public static Block getHighestBlockBelow (int y, Location location) {

        for (int i = y; i > -63; i--) {
            Location newLoc = new Location(location.getWorld(), location.getX(), i, location.getZ());
            if (newLoc.getBlock().getType().toString() != "AIR" && newLoc.getBlock().getType() != Material.SHORT_GRASS && newLoc.getBlock().getType() != Material.TALL_GRASS) {
                return newLoc.getBlock();
            }
        }
        return location.getBlock();
    }

    public static Location getNextAirPocket(Location location) {
        double x = location.x();
        int y = (int)location.y();
        double z = location.z();

        int air = 0;
        for (int i = y; i < 320; i++) {

            Location newloc = new Location(location.getWorld(),x,i,z);
            if (newloc.getBlock().getType().equals(Material.AIR)) {
                air++;
            } else {
                air = 0;
            }

            if (air > 1) {
                y = i-1;
                break;
            }
        }

        return new Location(location.getWorld(),x,y,z);
    }

}
