package org.pat.abilities.Objects.Abilities;

import io.papermc.paper.math.Rotation;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.SoundStop;
import net.minecraft.world.level.block.SoundType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.SculkShrieker;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SculkVein;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.pat.abilities.Listeners.CatalystCorruptStacks;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.Objects.InterfaceActions;
import org.pat.abilities.TilsU;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.pat.abilities.Listeners.CatalystCorruptStacks.*;

public interface Catalyst extends InterfaceActions {


    /**
     * To create a new ability copy this class and edit this methods; DO NOT RENAME THEM
     * Then you need to create a enum variable
     *
     * Remove the functions for running either shift or passive this tells the plugin it doesn't have one of the other
     * They MUST return true if you do want to use them
     */

    /**
     * You do not need the run or cancel method if you do not have an itemUseAnimation as part of either your primary or secondary
     * <p>
     * The cancel primary charge is a method that runs as its canceled which can be used to cancel any sounds for example
     * more commonly though you will need to check if the player is still charging for each action in the charge function and return if they are no longer charging either ability
     */

    static HashMap<Player,BukkitTask> primaryChargeRunnable = new HashMap<>();

    @Override
    default void runPrimaryCharge(Player p, AbilityUtil ability) {
        p.getWorld().playSound(p, Sound.BLOCK_SCULK_CATALYST_BLOOM, 1, 1);
        if (getHighestBlockBelow((int) p.getEyeLocation().y(), p.getEyeLocation()).getType().equals(Material.SCULK)) {
            connectedSculkBlocks.put(p,new ArrayList<>());
            getConnectedSculkBlocksToLocation(p,getHighestBlockBelow((int) p.getEyeLocation().y(), p.getLocation()).getLocation());
            List<Block> closeSculk = new ArrayList<>();
            for (Block sculk : connectedSculkBlocks.get(p)) {
                if (sculk.getLocation().distance(p.getLocation()) < 3) {
                    closeSculk.add(sculk);
                }
            }
            primaryChargeRunnable.put(p, new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block sculk : closeSculk) {
                        playSculkSpreadEffects(sculk,true,true);
                    }
                }
            }.runTaskTimer(TilsU.plugin, 0, 10));
        }
    }

    static HashMap<Player,Long> secondaryChargedTime = new HashMap<>();
    public static String SCULK = TilsU.t("&#012a39");
    public static String SCULK_DARK = TilsU.t("&#041820");
    public static String SCULK_LIGHT = TilsU.t("&#024050");
    @Override
    default void runSecondaryCharge(Player p, AbilityUtil ability) {
        secondaryChargedTime.put(p,System.currentTimeMillis());
        CatalystCorruptStacks.numBlocksAbsorbed.put(p,0);
        if (getHighestBlockBelow((int) p.getLocation().y(), p.getEyeLocation()).getLocation().getBlock().getType().equals(Material.SCULK)) {
            p.getWorld().playSound(p.getLocation(),Sound.ENTITY_WARDEN_AGITATED,1,1);
            connectedSculkBlocks.put(p,new ArrayList<>());
            getConnectedSculkBlocksToLocation(p,getHighestBlockBelow((int) p.getEyeLocation().y(), p.getLocation()).getLocation());

            CatalystCorruptStacks.secondaryChargeRunnable.put(p,new BukkitRunnable() {
                int tick = connectedSculkBlocks.get(p).size();
                int initialSize = connectedSculkBlocks.get(p).size();
                @Override
                public void run() {
                    if (!connectedSculkBlocks.get(p).isEmpty()) {
                        tick--;
                        Block block = connectedSculkBlocks.get(p).get(tick);
                        connectedSculkBlocks.get(p).remove(block);
                        playSculkSpreadEffects(block, false,false);
                        CatalystCorruptStacks.numBlocksAbsorbed.put(p, CatalystCorruptStacks.numBlocksAbsorbed.get(p) + 1);

                        p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * CatalystCorruptStacks.numBlocksAbsorbed.get(p), 1), true);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * CatalystCorruptStacks.numBlocksAbsorbed.get(p), 2), true);
                    }
                }
            }.runTaskTimer(TilsU.plugin, 0, 1));
        } else {
            p.getWorld().playSound(p.getLocation(),Sound.ENTITY_WARDEN_SONIC_CHARGE,1,1);
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,999999,5,false,false,false));
    }

    @Override
    default void cancelPrimaryCharge(Player p, AbilityUtil ability) {
        SoundStop.named(Key.key("minecraft:block.sculk_catalyst.bloom"));
        if (primaryChargeRunnable.get(p) != null) {
            primaryChargeRunnable.get(p).cancel();
        }
    }

    @Override
    default void cancelSecondaryCharge(Player p, AbilityUtil ability) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }.runTask(TilsU.plugin);

        Long chargedTime = Math.min(1500,System.currentTimeMillis()-secondaryChargedTime.get(p));
        float chargeMultiplier = (float) chargedTime /1500;
        int numBlocksAbsorbed = CatalystCorruptStacks.numBlocksAbsorbed.get(p);
        if (numBlocksAbsorbed > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {

                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 1F, (float) (1));
                }
            }.runTask(TilsU.plugin);
        }

        if (secondaryChargeRunnable.get(p) != null) {
        secondaryChargeRunnable.get(p).cancel();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Location startLocation = p.getEyeLocation();
                startLocation = startLocation.add(p.getEyeLocation().getDirection().normalize().multiply(2));
                Location finalStartLocation = startLocation;

                float range = (20*chargeMultiplier)+numBlocksAbsorbed;
                float damage = (6*chargeMultiplier)+ (float) numBlocksAbsorbed /4;
                if (!p.isSneaking()) {
                    p.setVelocity(p.getVelocity().subtract(p.getEyeLocation().getDirection().normalize().multiply(1)));
                }
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 1);
                Location sonicBoomLocation = finalStartLocation;
                while (sonicBoomLocation.distance(p.getEyeLocation()) < range) {

                    List<Entity> damagedEntities = new ArrayList<>();
                    p.getWorld().spawnParticle(Particle.SONIC_BOOM, sonicBoomLocation, 1);
                    if (!sonicBoomLocation.getNearbyEntities(1, 1, 1).isEmpty()) {
                        for (Entity damageableEntity : sonicBoomLocation.getNearbyEntities(1, 1, 1)) {
                            if (damagedEntities.contains(damageableEntity)) continue;
                            if (damageableEntity.equals(p)) continue;
                            if (damageableEntity instanceof LivingEntity livingEntity) {
                                livingEntity.damage(damage, DamageSource.builder(DamageType.SONIC_BOOM).build());
                                livingEntity.setVelocity(livingEntity.getVelocity().add(sonicBoomLocation.getDirection()).normalize().multiply(1.2 + (0.02)*numBlocksAbsorbed));
                                damagedEntities.add(livingEntity);
                            }
                        }
                    }

                    sonicBoomLocation = sonicBoomLocation.add(p.getEyeLocation().getDirection().normalize().multiply(2+ (0.02)*numBlocksAbsorbed));
                }
                CatalystCorruptStacks.numBlocksAbsorbed.put(p,0);
                if (connectedSculkBlocks.get(p) != null) {
                    connectedSculkBlocks.get(p).clear();
                }

                TilsU.setSecondaryCooldown(p,2000L);
            }
        }.runTaskLater(TilsU.plugin, numBlocksAbsorbed > 0 ? 35 : 10);
    }

    @Override
    default void runPrimary(Player p, AbilityUtil ability) {
        p.getWorld().playSound(p.getLocation(),Sound.BLOCK_SCULK_SHRIEKER_SHRIEK,1,1);
        Location spreadLocation = p.getEyeLocation();

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {

                if (count < 10) {
                    p.getWorld().spawnParticle(Particle.SHRIEK, p.getEyeLocation().add(0, 0.5, 0), 1, 0);
                    count++;
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(TilsU.plugin,0,4);

        if (primaryChargeRunnable.get(p) != null && !primaryChargeRunnable.get(p).isCancelled()) {
            primaryChargeRunnable.get(p).cancel();


            for (Block shrieker : shriekers.keySet()) {
                if (shrieker.getLocation().distance(p.getLocation()) < 30) {
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_DIG, 1, 1);
                    SculkShrieker sculkShrieker = (SculkShrieker) shrieker.getState();
                    new BukkitRunnable() {
                        int sinkSteps = 10;

                        @Override
                        public void run() {
                            p.teleport(p.getLocation().add(0, -0.25, 0));
                            sinkSteps--;
                            if (sinkSteps <= 0) {
                                Vector teleportLocation = shrieker.getLocation().toVector().subtract(p.getLocation().toVector().normalize().multiply(1));
                                p.teleport(teleportLocation.toLocation(p.getWorld()).toHighestLocation().add(0, -2.5, 0));
                                sculkShrieker.setWarningLevel(0);
                                sculkShrieker.tryShriek(p);
                                p.getWorld().playSound(sculkShrieker.getLocation(), Sound.ENTITY_WARDEN_EMERGE, 1, 1);
                                connectedSculkBlocks.put(p, new ArrayList<>());
                                getConnectedSculkBlocksToLocation(p, sculkShrieker.getLocation());
                                List<Block> closeSculk = new ArrayList<>();
                                for (Block sculk : connectedSculkBlocks.get(p)) {
                                    if (sculk.getLocation().distance(p.getLocation()) < 5) {
                                        closeSculk.add(sculk);
                                    }
                                }
                                cancel();
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        p.teleport(p.getLocation().add(0, 0.25, 0));

                                        for (Block sculk : closeSculk) {
                                            sculk.getLocation().getWorld().spawnParticle(Particle.BLOCK_CRUMBLE, sculk.getLocation().clone().add(0.5, 1.1, 0.5), 5, 0.5, 0, 0.5, 0, sculk.getBlockData());
                                        }
                                        if (p.getLocation().y() > p.getLocation().toHighestLocation().y() + 1) {
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(TilsU.plugin, 0, 2);
                            }
                        }
                    }.runTaskTimer(TilsU.plugin, 0, 2);
                    break;
                }
            }
        } else {
            spread(getHighestBlockBelow((int)spreadLocation.y(),spreadLocation).getLocation(),0,0,15,0,0);
        }
    }

    @Override
    default void runSecondary(Player p, AbilityUtil ability) {

        }

    @Override
    default boolean tickShiftPassive(Player p, AbilityUtil ability) {
        return true;
    }

    HashMap<Player,Long> corruptDebuffRemoveTimer = new HashMap<>();

    @Override
    default boolean tickPassive(Player p, AbilityUtil ability) {

        for (Player corruptedPlayer : CatalystCorruptStacks.corruptionStacks.keySet()) {
            if (corruptDebuffRemoveTimer.get(corruptedPlayer) == null) {
                corruptDebuffRemoveTimer.put(corruptedPlayer,System.currentTimeMillis()+1000*5);
            } else {
                if (System.currentTimeMillis() > corruptDebuffRemoveTimer.get(corruptedPlayer)) {
                    CatalystCorruptStacks.corruptionStacks.put(corruptedPlayer, CatalystCorruptStacks.corruptionStacks.get(corruptedPlayer) - 1);
                    corruptDebuffRemoveTimer.remove(corruptedPlayer);
                    if (CatalystCorruptStacks.corruptionStacks.get(corruptedPlayer) <= 0) {
                        CatalystCorruptStacks.corruptionStacks.remove(corruptedPlayer);
                    }
                }
            }
            if (CatalystCorruptStacks.corruptionStacks.get(corruptedPlayer) == null) continue;
            if (CatalystCorruptStacks.corruptionStacks.get(corruptedPlayer) > 3) {
                corruptedPlayer.getWorld().spawnParticle(Particle.SCULK_SOUL,corruptedPlayer.getLocation().add(0,1,0),1,0.5,0.5,0.5,0);
            }
            corruptedPlayer.getWorld().spawnParticle(Particle.SCULK_CHARGE_POP,corruptedPlayer.getLocation().add(0,1,0),1,0.5,0.5,0.5,0);


        }
        if (p.getLocation().add(0,-1,0).getBlock().getType().equals(Material.SCULK) || p.getLocation().add(0,-2,0).getBlock().getType().equals(Material.SCULK)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,10,0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,10,0));
        }
        return true;
    }

    @Override
    default void selectAbility(Player p, AbilityUtil ability) {
    }

    @Override
    default void unselectAbility(Player p, AbilityUtil ability) {
    }



    static HashMap<Player,List<Block>> connectedSculkBlocks = new HashMap<>();
    static HashMap<Block,Material> oldMaterialType = new HashMap<>();

    static List<Block> possibleSculkSpreadBlocks(Location loc) {
        List<Block> check = new ArrayList<>();
        for (BlockFace face : BlockFace.values()) {
            check.add(loc.getBlock().getRelative(face));
            if (!face.equals(BlockFace.UP) && !face.equals(BlockFace.DOWN) && !face.equals(BlockFace.SELF)) {
                check.add(loc.getBlock().getRelative(face).getRelative(BlockFace.UP));
                check.add(loc.getBlock().getRelative(face).getRelative(BlockFace.DOWN));
            }
        }
        return check;
    }

    static List<Block> sculkBlocksAroundLocation(Location loc) {
        List<Block> sculkBlocks = new ArrayList<>();
        for (Block block : possibleSculkSpreadBlocks(loc)) {
            if (block.getType().equals(Material.SCULK_SHRIEKER) || block.getType().equals(Material.SCULK) || block.getType().equals(Material.SCULK_VEIN)) {
                sculkBlocks.add(block);
            }
        }
        return sculkBlocks;
    }

    static void getConnectedSculkBlocksToLocation(Player p,Location loc) {

            for (Block block : sculkBlocksAroundLocation(loc)) {
                if (connectedSculkBlocks.get(p).contains(block)) continue;
                connectedSculkBlocks.get(p).add(block);
                getConnectedSculkBlocksToLocation(p,block.getLocation());
            }
    }


    static void playSculkSpreadEffects(Block block,boolean endBlockTypeIsSculk, boolean justFX) {

        block.getLocation().getWorld().playSound(block.getLocation().add(0.5, 0.5, 0.5), Sound.BLOCK_SCULK_CHARGE, 0.3F, 1F);
        block.getLocation().getWorld().spawnParticle(Particle.SCULK_CHARGE, block.getLocation().clone().add(0.5, 1.1, 0.5), 5, 0.5, 0, 0.5, 0, (float) Math.toRadians(0));
        block.getLocation().getWorld().spawnParticle(Particle.SCULK_CHARGE_POP, block.getLocation().clone().add(0.5, 1.1, 0.5), 5, 0.5, 0, 0.5, 0);
//        block.getLocation().getWorld().spawnParticle(Particle.SCULK_CHARGE, block.getLocation().clone().add(0.5, 1.1, 0.5), 5, 0.5, 0, 0.5, 0,(float)Math.toRadians(45));
        if (justFX) {
            return;
        }

        if (endBlockTypeIsSculk) {
            if (!block.isSolid()) {
                oldMaterialType.put(block,block.getType());
                block.setType(Material.SCULK_VEIN);
                SculkVein sculkVein = (SculkVein) block.getBlockData();
                sculkVein.setFace(BlockFace.DOWN, true);
                return;
            }
            if (!block.getType().equals(Material.SCULK)) {
                oldMaterialType.put(block,block.getType());
            }

            block.setType(Material.SCULK);
        } else {
            if (oldMaterialType.get(block) != null) {
                if (!block.getType().equals(Material.SCULK)) {
                    block.setType(oldMaterialType.get(block));
                }
            } else {
                block.setType(Material.AIR);
            }

        }
    }


    ///
    /// @param timer
    /// starts at 0, ++ for every block of sculk that spreads
    ///
    /// @param length
    /// the maximum amount of sculk allowed to spread from the method. Method stops recursing when timer reaches this.
    ///
    /// @param speed
    /// passes the delay forward so that the sculk spreads at a speed similar to vanilla.

    static void spread(Location loc, int delay, int timer, int length, int speed,int failCount) {
        Random random = new Random();
        int rint = random.nextInt(12);
        List<Block> check = new ArrayList<>();
        if (timer < length) {
            check.add(loc.getBlock().getRelative(BlockFace.NORTH));
            check.add(loc.getBlock().getRelative(BlockFace.EAST));
            check.add(loc.getBlock().getRelative(BlockFace.SOUTH));
            check.add(loc.getBlock().getRelative(BlockFace.WEST));
            check.add(loc.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.UP));
            check.add(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.UP));
            check.add(loc.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP));
            check.add(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.UP));
            check.add(loc.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN));
            check.add(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN));
            check.add(loc.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN));
            check.add(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN));
            for (Block block1 : check) {
                if (block1.getType().isOccluding() == true && block1.getRelative(BlockFace.UP).getType().isOccluding() == false) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(TilsU.plugin, new Runnable() {
                        Block block = loc.getBlock();

                        @Override
                        public void run() {
                            switch (rint) {
                                case 0:
                                    block = block.getRelative(BlockFace.NORTH);
                                    break;
                                case 1: {
                                    block = block.getRelative(BlockFace.EAST);
                                    break;
                                }
                                case 2: {
                                    block = block.getRelative(BlockFace.SOUTH);
                                    break;
                                }
                                case 3: {
                                    block = block.getRelative(BlockFace.WEST);
                                    break;
                                }
                                case 4: {
                                    block = block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN);
                                    break;
                                }
                                case 5: {
                                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN);
                                    break;
                                }
                                case 6: {
                                    block = block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN);
                                    break;
                                }
                                case 7: {
                                    block = block.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN);
                                    break;
                                }
                                case 8: {
                                    block = block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP);
                                    break;
                                }
                                case 9: {
                                    block = block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP);
                                    break;
                                }
                                case 10: {
                                    block = block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP);
                                    break;
                                }
                                case 11: {
                                    block = block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP);
                                    break;
                                }
                            }

                            if (block.getType() != Material.SCULK && block.getType() != Material.AIR && block.getType().isOccluding() == true && block.getRelative(BlockFace.UP).getType().isOccluding() == false) {

                                if (block.getRelative(BlockFace.UP).getType().isOccluding() == false) {
                                    block.getRelative(BlockFace.UP).setType(Material.AIR);
                                }
                                playSculkSpreadEffects(block, true, false);

                                Bukkit.getScheduler().scheduleSyncDelayedTask(TilsU.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType().isOccluding() == false && block.getRelative(BlockFace.NORTH).getType() != Material.SCULK && block.getRelative(BlockFace.NORTH).getType() != Material.AIR && block.getRelative(BlockFace.NORTH).getType().isOccluding() == true) {
                                            block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).setType(Material.AIR);
                                            block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).setType(Material.SCULK_VEIN);
                                            if (block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType() == Material.SCULK_VEIN) {
                                                SculkVein sculkVein = (SculkVein) block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getBlockData();
                                                sculkVein.setFace(BlockFace.DOWN, true);
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).setBlockData(sculkVein);
                                            }
                                            if (block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType().isOccluding() == false && block.getRelative(BlockFace.EAST).getType() != Material.SCULK && block.getRelative(BlockFace.EAST).getType() != Material.AIR && block.getRelative(BlockFace.EAST).getType().isOccluding() == true) {
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.AIR);
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setType(Material.SCULK_VEIN);
                                                if (block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType() == Material.SCULK_VEIN) {
                                                    SculkVein sculkVein = (SculkVein) block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getBlockData();
                                                    sculkVein.setFace(BlockFace.DOWN, true);
                                                    block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setBlockData(sculkVein);
                                                }
                                            }
                                            if (block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType().isOccluding() == false && block.getRelative(BlockFace.SOUTH).getType() != Material.SCULK && block.getRelative(BlockFace.SOUTH).getType() != Material.AIR && block.getRelative(BlockFace.SOUTH).getType().isOccluding() == true) {
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).setType(Material.AIR);
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).setType(Material.SCULK_VEIN);
                                                if (block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType() == Material.SCULK_VEIN) {
                                                    SculkVein sculkVein = (SculkVein) block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getBlockData();
                                                    sculkVein.setFace(BlockFace.DOWN, true);
                                                    block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).setBlockData(sculkVein);
                                                }
                                            }
                                            if (block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType().isOccluding() == false && block.getRelative(BlockFace.WEST).getType() != Material.SCULK && block.getRelative(BlockFace.WEST).getType() != Material.AIR && block.getRelative(BlockFace.WEST).getType().isOccluding() == true) {
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.AIR);
                                                block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setType(Material.SCULK_VEIN);
                                                if (block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType() == Material.SCULK_VEIN) {
                                                    SculkVein sculkVein = (SculkVein) block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getBlockData();
                                                    sculkVein.setFace(BlockFace.DOWN, true);
                                                    block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setBlockData(sculkVein);
                                                }
                                            }
                                        }
                                    }
                                }, 5);

                                spread(block.getLocation(), speed, timer + 1, length, speed, failCount);

                            } else {
                                if (block.getType().equals(Material.SCULK)) {
                                    if (failCount > 10) {

                                        Block relativeUp = block.getRelative(BlockFace.UP);
                                        playSculkSpreadEffects( block.getRelative(BlockFace.UP), false, true);
                                        relativeUp.setType(Material.SCULK_SHRIEKER);
                                        BlockDisplay shriekerDisplay = (BlockDisplay) block.getWorld().spawnEntity(relativeUp.getLocation(), EntityType.BLOCK_DISPLAY);
                                        shriekerDisplay.setBlock(relativeUp.getBlockData());
                                        shriekerDisplay.setVisibleByDefault(false);
                                        shriekerCheckDestroyedRunnable(shriekerDisplay, relativeUp);
                                        return;
                                    }


                                    playSculkSpreadEffects(block, true, false);
                                    spread(loc, 1, timer, length, 1, failCount + 1);
                                } else {
                                    spread(loc, 1, timer, length, 1, failCount);
                                }

                            }
                        }
                    }, delay);
                    break;
                }
            }
        }
    }

    static HashMap<Block,BlockDisplay> shriekers = new HashMap<>();

    static void shriekerCheckDestroyedRunnable (BlockDisplay shriekerDisplay, Block sculkShrieker) {

        shriekers.put(sculkShrieker,shriekerDisplay);
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean glow = false;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (AbilityUtil.getSelectedAbility(p) == null) continue;
                    if (AbilityUtil.getSelectedAbility(p).equals(AbilityUtil.catalyst)) {
                        if (p.getLocation().distance(sculkShrieker.getLocation()) < 30) {
                            glow = true;
                            p.showEntity(TilsU.plugin,shriekerDisplay);
                        }
                    } else {

                        p.hideEntity(TilsU.plugin,shriekerDisplay);
                    }
                }
                shriekerDisplay.setGlowing(glow);
                shriekerDisplay.setGlowColorOverride(Color.TEAL);


                if (!sculkShrieker.getType().equals(Material.SCULK_SHRIEKER)) {
                    shriekerDisplay.remove();
                    cancel();
                }
            }
        }.runTaskTimer(TilsU.plugin,0,1);

    }
}
