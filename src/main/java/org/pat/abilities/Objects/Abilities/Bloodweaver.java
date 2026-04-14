package org.pat.abilities.Objects.Abilities;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.pat.abilities.Objects.AbilityUtil;
import org.pat.abilities.Objects.InterfaceActions;
import org.pat.abilities.TilsU;
import org.pat.pattyEssentialsV3.ColoredText;
import org.pat.pattyEssentialsV3.Utils;

import java.text.DecimalFormat;
import java.util.*;

public interface Bloodweaver extends InterfaceActions {

    HashMap<UUID, Float> bloodStored = new HashMap<>();
    HashMap<UUID, HashMap<LivingEntity, Pair<Long, Float>>> playerBleed = new HashMap<>();

    /**
     * Util
     */
    String notEnoughBlood = ColoredText.t(Utils.darkred + "&oYou feel light-headed..");

    /**
     * Primary
     */
    float primarySpeed = 2F;
    float primaryStepDistance = 0.2F;
    float primaryRange = 40;
    float primaryBloodRequirement = 0.175F; //0.15
    float primaryDamage = 1F;
    float bleedDamageIterator = 0.075F;
    Long bleedDuration = 4000L; // Ms
    int steps = Math.max(1, (int) Math.ceil(primarySpeed / primaryStepDistance));
    float kunaiSize = 0.3F;
    float kunaiOffset = 0.35F;
    boolean primaryDebugVisualiser = false;

    /**
     * Secondary
     */
    float secondaryDashSpeed = 7;
    float secondaryDashBloodRequirement = 0.1F;
    float secondaryAirWalkBloodRequirement = 0.175F;
    float secondaryVerticalBoost = 0.45F;
    float secondaryHorizontalBoost = 0.5F;

    /**
     * Shift-passive
     */
    int lifedrain_radius = 8;
    float addedBloodPerEntity = 0.8F;
    float exponentialIncreaseInBlood = 0.7F;
    float healFactor = 0.15F;

    /**
     * Passive
     */
    float passiveBloodAddedPerTick = 0.0005F; // 0.0005

    @Override
    default void runPrimaryCharge(Player p, AbilityUtil ability) {
        p.playSound(p, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.05F, 1);
    }

    @Override
    default void runSecondaryCharge(Player p, AbilityUtil ability) {
    }

    @Override
    default void cancelPrimaryCharge(Player p, AbilityUtil ability) {
        p.stopSound(Sound.ENTITY_EVOKER_FANGS_ATTACK);
        p.playSound(p, Sound.ITEM_SHEARS_SNIP, 0.1F, 0.5F);
    }

    @Override
    default void cancelSecondaryCharge(Player p, AbilityUtil ability) {

    }

    @Override
    default void runPrimary(Player p, AbilityUtil ability) {
        if (getBloodStored(p) >= primaryBloodRequirement) {
            addBloodStored(p, -primaryBloodRequirement);

            spawnKunai(p, kunaiSize, kunaiOffset);
            spawnKunai(p, kunaiSize, kunaiOffset);
            if (new Random().nextInt(1) == 1) {
                spawnKunai(p, kunaiSize, kunaiOffset);
            }
            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                spawnKunai(p, kunaiSize, kunaiOffset);
                if (new Random().nextInt(1) == 1) {
                    spawnKunai(p, kunaiSize, kunaiOffset);
                }
            }, 2);
            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                spawnKunai(p, kunaiSize, kunaiOffset);
                p.playSound(p, Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.03F, 1);
            }, 5);
        } else {
            p.playSound(p, Sound.ITEM_SHIELD_BREAK, 0.1F, 1.2F);
            p.sendMessage(notEnoughBlood);
        }
    }

    static void spawnKunai(Player p, float kunaiSize, float offset) {
        Vector forward = p.getEyeLocation().getDirection().normalize();

        Vector arbitrary = new Vector(0, 1, 0);
        if (Math.abs(forward.dot(arbitrary)) > 0.9)
            arbitrary = new Vector(1, 0, 0);

        Vector right = forward.clone().crossProduct(arbitrary).normalize();
        Vector up = right.clone().crossProduct(forward).normalize();

        double spread = offset;

        double randRight = (Math.random() * 2 - 1) * spread;
        double randUp = Math.random() * spread;

        Vector offsetVec = right.multiply(randRight).add(up.multiply(randUp));
        Vector result = forward.clone().add(offsetVec).normalize();

        //Location location = p.getEyeLocation().clone().add(offsetVec);

        List<Location> bluePoints = new ArrayList<>();
        List<Location> greenPoints = new ArrayList<>();

        Vector offsetVecTrail = offsetVec.clone();
        Location tL = p.getLocation().add(0, 1.4, 0).add(p.getLocation().getDirection().setY(0).normalize().multiply(-0.5));

        new BukkitRunnable() {
            int iT = 0;

            public void run() {

                offsetVecTrail.add(p.getEyeLocation().getDirection().normalize().multiply(0.01));
                offsetVecTrail.multiply(0.92);

                tL.setDirection(offsetVecTrail);

                BlockDisplay bd = p.getWorld().spawn(tL, BlockDisplay.class);
                bd.setTransformationMatrix(new Matrix4f().scale(0.1F, 0.1F, (float) offsetVecTrail.length()).translate(-0.5F, -0.5F, 0));
                bd.setBlock(Material.REDSTONE_BLOCK.createBlockData());
                bd.setInterpolationDelay(-1);
                bd.setInterpolationDuration(6);

                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                    bd.setTransformationMatrix(new Matrix4f().scale(0.0F, 0.0F, (float) offsetVecTrail.length()).translate(-0.5F, -0.5F, 0));
                }, 1);

                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                    bd.remove();
                }, 8);

                tL.add(offsetVecTrail);

                if (iT == 6) {
                    cancel();

                    p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, tL, 1, 0, 0, 0, 0);

                    Vector vectorAdj = null;
                    Block targetBlock = p.getTargetBlockExact((int) primaryRange);
                    if (targetBlock != null) {
                        vectorAdj = targetBlock.getLocation().add(0.5, 0.5, 0.5).toVector().subtract(tL.toVector()).normalize();
                    }
                    Entity targetEntity = p.getTargetEntity((int) primaryRange);
                    if (targetEntity instanceof LivingEntity && (targetBlock == null || p.getTargetEntity((int) primaryRange).getLocation().add(0, 1, 0).distance(tL) <= targetBlock.getLocation().add(0.5, 0.5, 0.5).distance(tL))) {
                        vectorAdj = p.getTargetEntity((int) primaryRange).getLocation().add(0.5, 0.5, 0.5).toVector().subtract(tL.toVector()).normalize();
                    }

                    tL.setDirection(p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(primaryRange)).toVector().subtract(tL.toVector()).normalize());
                    if (vectorAdj != null)
                        tL.setDirection(tL.getDirection().add(vectorAdj.multiply(1)));
                    Location location = tL;

                    ItemDisplay kunai = p.getWorld().spawn(tL, ItemDisplay.class);
                    kunai.setTransformationMatrix(new Matrix4f().scale(kunaiSize + (float) new Random().nextInt(3) / 10F).translate(0.4F, -0.4F, 0).rotateLocalY((float) Math.toRadians(90)).rotateLocalX((float) Math.toRadians(45)).rotateLocalZ((float) Math.toRadians(180 + new Random().nextInt(180))));
                    kunai.setItemStack(new ItemStack(getRanSword()));
                    kunai.setTeleportDuration(2);
                    kunai.setBrightness(new Display.Brightness(10, 10));

                    new BukkitRunnable() {

                        float distance = primaryRange;
                        Vector vec = location.getDirection().normalize().multiply(primarySpeed);

                        @Override
                        public void run() {

                            Vector micro = vec.clone().multiply(1F / (steps));

                            if (distance <= 0) {
                                kunai.remove();
                                cancel();
                                return;
                            }

                            if (distance - primarySpeed > 0) {
                                Location temp = location.clone();
                                for (int i = 0; i < steps; i++) {
                                    if (temp.getBlock().isSolid()) {
                                        kunai.teleport(temp);
                                        cancel();
                                        Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                            kunai.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, kunai.getLocation().add(0, 0.1, 0), 1, 0, 0, 0, 0);
                                            kunai.remove();
                                        }, 60);
                                        return;
                                    }
                                    Set<LivingEntity> entities = new HashSet<>(temp.getNearbyLivingEntities(4));
                                    entities.remove(p);
                                    if (entities.size() >= 1) {
                                        LivingEntity hitEntity = null;
                                        for (LivingEntity entity : entities) {
                                            if (entity.getBoundingBox().contains(temp.toVector())) {
                                                primaryHitEntityLogic(p, entity);
                                                hitEntity = entity;
                                            }
                                        }
                                        if (hitEntity != null) {
                                            kunai.teleport(temp);
                                            kunai.remove();
                                            cancel();
                                            return;
                                        }
                                    }
                                    temp.add(micro);
                                    if (i != steps - 1)
                                        greenPoints.add(temp.clone());
                                }
                            }

                            kunai.teleport(location);

                            if (new Random().nextInt(5) == 4)
                                p.getWorld().spawnParticle(Particle.DUST, location, 1, 0, 0, 0, new Particle.DustTransition(Color.fromRGB(148, 20, 9), Color.fromRGB(74, 9, 4), 0.75F));

                            distance -= primarySpeed;

                            LivingEntity target = getLookDirectionTarget(p.getEyeLocation(), primaryRange, 30, p);
                            if (target != null) {
                                Location targetPos = target.getLocation().add(0, target.getHeight() * 0.5, 0);

                                Vector desired = targetPos.toVector().subtract(location.clone().toVector()).normalize().multiply(primarySpeed);
                                Vector steer = desired.subtract(vec).normalize().multiply(0.08); // constant force
                                vec.add(steer);
                                vec = vec.normalize().multiply(primarySpeed);

                                location.setDirection(vec);
                            }

                            Location tLoc = location.clone();
                            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                BlockDisplay bd = p.getWorld().spawn(tLoc, BlockDisplay.class);
                                bd.setInterpolationDelay(-1);
                                bd.setTransformationMatrix(new Matrix4f().scale(0.02F, 0.02F, (float) vec.length()).translate(-0.5F, -0.5F, 0));
                                bd.setInterpolationDuration(5);
                                bd.setBlock(Material.RED_STAINED_GLASS.createBlockData());

                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    bd.setTransformationMatrix(new Matrix4f().scale(0, 0, (float) vec.length()));
                                }, 1);

                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    bd.remove();
                                }, 7);
                            }, 3);

                            bluePoints.add(location.clone());
                            location.add(vec);
                        }

                    }.runTaskTimer(TilsU.plugin, 0L, 1L);


                    if (primaryDebugVisualiser) {
                        new BukkitRunnable() {
                            long timer = System.currentTimeMillis() + 10000; // Remove dots after 10 seconds

                            @Override
                            public void run() {

                                if (timer < System.currentTimeMillis())
                                    cancel();

                                for (Location loc : greenPoints) {
                                    loc.getWorld().spawnParticle(
                                            Particle.DUST, loc, 1, 0, 0, 0,
                                            new Particle.DustOptions(Color.LIME, 0.4F)
                                    );
                                }

                                for (Location loc : bluePoints) {
                                    loc.getWorld().spawnParticle(
                                            Particle.DUST, loc, 1, 0, 0, 0,
                                            new Particle.DustOptions(Color.BLUE, 0.8F)
                                    );
                                }
                            }
                        }.runTaskTimer(TilsU.plugin, 0L, 1L);
                    }
                }

                iT++;

            }
        }.runTaskTimer(TilsU.plugin, 0, 1L);

    }

    static Material getRanSword() {
        return Tag.ITEMS_SWORDS.getValues().stream().toList().get(new Random().nextInt(Tag.ITEMS_SWORDS.getValues().stream().toList().size() - 1));
    }

    static void primaryHitEntityLogic(Player p, LivingEntity entity) {
        entity.damage(primaryDamage);
        entity.setNoDamageTicks(0);
        Vector kb = entity.getLocation().toVector()
                .subtract(p.getLocation().toVector())
                .normalize()
                .multiply(0.05);
        entity.setVelocity(entity.getVelocity().add(kb));
        healPlayer(p, healFactor, -1F, -1F);
        addEntityBleed(p, entity, getEntityBleedDamage(p, entity) + bleedDamageIterator);
    }

    static LivingEntity getLookDirectionTarget(Location eye, double maxDistance, double maxAngleDegrees, Player p) {

        Vector look = eye.getDirection();

        LivingEntity best = null;
        double bestAngle = maxAngleDegrees;

        for (LivingEntity entity : p.getLocation().getNearbyLivingEntities(maxDistance)) {

            if (entity == p)
                continue;

            if (entity instanceof Player player && player.getGameMode() != GameMode.SURVIVAL)
                continue;

            // Vector from player to entity
            Vector toEntity = entity.getLocation().add(0, entity.getHeight() * 0.5, 0)
                    .toVector()
                    .subtract(eye.toVector())
                    .normalize();

            // Angle between look direction and entity direction
            double angle = Math.toDegrees(look.angle(toEntity));

            if (angle < bestAngle) {
                bestAngle = angle;
                best = entity;
            }
        }

        return best;
    }


    @Override
    default void runSecondary(Player p, AbilityUtil ability) {
        BoundingBox feet = p.getBoundingBox().clone();
        feet.shift(0, -0.05, 0);

        boolean grounded = false;

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = feet.getCenter().toLocation(p.getWorld()).add(x, -(feet.getHeight() / 2), z);
                Block block = p.getWorld()
                        .getBlockAt(loc);
                grounded = block
                        .getCollisionShape()
                        .overlaps(feet) && block.isSolid();
                if (grounded)
                    break;
            }
        }

        if ((p.isOnGround() && grounded) || p.getLocation().add(0, -0.75F, 0).getBlock().isSolid()) {
            if (getBloodStored(p) > secondaryDashBloodRequirement) {
                addBloodStored(p, -secondaryDashBloodRequirement);

                Vector vec = p.getLocation().toVector().subtract(v.get(p.getUniqueId()));
                if (p.getLocation().getX() == v.get(p.getUniqueId()).getX() && p.getLocation().getZ() == v.get(p.getUniqueId()).getZ()) {
                    vec = p.getEyeLocation().getDirection();
                }
                vec.setY(0);
                vec.normalize().multiply(secondaryDashSpeed);
                p.setVelocity(p.getVelocity().add(vec));

                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_NAUTILUS_DASH, 0.15F, 1.1F);
                new BukkitRunnable() {
                    int iterations = 6;
                    Location oldLoc = null;

                    public void run() {
                        Location loc = p.getLocation().add(0, 1, 0);
                        if (oldLoc != null) {
                            oldLoc.setY(loc.getY());
                            Vector vec = oldLoc.toVector().subtract(loc.toVector()).normalize().multiply(oldLoc.distance(loc));
                            loc.setDirection(vec);

                            p.getWorld().spawnParticle(Particle.DUST, p.getLocation().add(0, 1, 0), 2, 0, 1, 0, new Particle.DustTransition(Color.fromRGB(148, 20, 9), Color.fromRGB(74, 9, 4), 0.75F));

                            BlockDisplay blud = p.getWorld().spawn(loc, BlockDisplay.class);
                            blud.setTransformationMatrix(new Matrix4f().scale(0.01F, 2, (float) vec.length()).translate(-0.5F, -0.5F, -0.5F));
                            blud.setInterpolationDuration(7);
                            blud.setInterpolationDelay(2);
                            blud.setBlock(Material.RED_STAINED_GLASS.createBlockData());
                            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                blud.setTransformationMatrix(new Matrix4f().scale(0.01F, 0, (float) vec.length()).translate(-0.5F, -0.5F, -0.5F));
                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    blud.remove();
                                }, blud.getInterpolationDuration());
                            }, 2);
                        }
                        oldLoc = loc;
                        iterations--;
                        if (iterations < 0)
                            cancel();
                    }
                }.runTaskTimer(TilsU.plugin, 0L, 0L);
            } else {
                p.sendMessage(notEnoughBlood);
                p.playSound(p, Sound.ITEM_SHIELD_BREAK, 0.1F, 1.2F);
            }
        } else {
            if (getBloodStored(p) > secondaryAirWalkBloodRequirement) {
                addBloodStored(p, -secondaryAirWalkBloodRequirement);

                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_SCULK_CATALYST_PLACE, 0.5F, 0.1F);

                Location base = p.getLocation().add(0, -1, 0);
                base.setY(base.getBlockY());
                base.setPitch(-90);
                base.setYaw(new Random().nextInt(91));

                int interpDur = 10;

                BlockDisplay blud = p.getWorld().spawn(base.clone().add(0, 1, 0), BlockDisplay.class);
                blud.setTransformationMatrix(new Matrix4f().scale(2, 2, 0.1F).translate(-0.5F, -0.5F, -0.5F));
                blud.setInterpolationDuration(interpDur);
                blud.setInterpolationDelay(2);
                blud.setBlock(Material.REDSTONE_BLOCK.createBlockData());

                BlockDisplay bludg = p.getWorld().spawn(base.clone().add(0, 1, 0), BlockDisplay.class);
                bludg.setTransformationMatrix(new Matrix4f().scale(2.1F, 2.1F, 0.2F).translate(-0.5F, -0.5F, -0.5F));
                bludg.setInterpolationDuration(interpDur);
                bludg.setInterpolationDelay(2);
                bludg.setBlock(Material.RED_STAINED_GLASS.createBlockData());

                base.setYaw(base.getYaw() + 45);

                BlockDisplay blud1 = p.getWorld().spawn(base.clone().add(0, 1 + 0.05, 0), BlockDisplay.class);
                blud1.setTransformationMatrix(new Matrix4f().scale(2, 2, 0.1F).translate(-0.5F, -0.5F, -0.5F));
                blud1.setInterpolationDuration(interpDur);
                blud1.setInterpolationDelay(2);
                blud1.setBlock(Material.REDSTONE_BLOCK.createBlockData());

                BlockDisplay bludg1 = p.getWorld().spawn(base.clone().add(0, 1 + 0.05, 0), BlockDisplay.class);
                bludg1.setTransformationMatrix(new Matrix4f().scale(2.1F, 2.1F, 0.2F).translate(-0.5F, -0.5F, -0.5F));
                bludg1.setInterpolationDuration(interpDur);
                bludg1.setInterpolationDelay(2);
                bludg1.setBlock(Material.RED_STAINED_GLASS.createBlockData());

                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                    blud.setTransformationMatrix(new Matrix4f().scale(0, 0, 0).translate(-0.5F, -0.5F, -0.5F));
                    blud1.setTransformationMatrix(new Matrix4f().scale(0, 0, 0).translate(-0.5F, -0.5F, -0.5F));
                    bludg.setTransformationMatrix(new Matrix4f().scale(0, 0, 0).translate(-0.5F, -0.5F, -0.5F));
                    bludg1.setTransformationMatrix(new Matrix4f().scale(0, 0, 0).translate(-0.5F, -0.5F, -0.5F));
                    Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                        blud.remove();
                        blud1.remove();
                        bludg.remove();
                        bludg1.remove();
                    }, blud.getInterpolationDuration());
                }, 2);

                //p.sendMessage(base.getY() + " | " + p.getLocation().getY() + " | " + p.getFallDistance());

                Vector fallVec = p.getVelocity();
                fallVec.setY(0);
                fallVec.add(new Vector(0, secondaryVerticalBoost, 0));
                fallVec.add(p.getEyeLocation().getDirection().setY(0).normalize().multiply(secondaryHorizontalBoost));
                p.setVelocity(fallVec);

                /**
                 for (int x = -1; x <= 1; x++) {
                 for (int z = -1; z <= 1; z++) {
                 Location loc = base.clone().add(x, 0, z);
                 if (!loc.getBlock().isSolid()) {
                 p.sendBlockChange(loc, Material.BEDROCK.createBlockData());
                 Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                 p.sendBlockChange(loc, loc.getBlock().getBlockData());
                 }, 20);
                 }
                 }
                 }
                 */
            } else {
                p.sendMessage(notEnoughBlood);
                p.playSound(p, Sound.ITEM_SHIELD_BREAK, 0.1F, 1.2F);
            }
        }
    }

    @Override
    default boolean tickShiftPassive(Player p, AbilityUtil ability) {

        int bloodAddAmount = 1 + new Random().nextInt(4);
        float addedBlood = ((getBloodStored(p) * ((float) Math.pow(1.00F + addedBloodPerEntity, exponentialIncreaseInBlood))) - getBloodStored(p)) / ability.getShiftPassiveTickRate() / bloodAddAmount;

        for (LivingEntity entity : p.getLocation().getNearbyLivingEntities(lifedrain_radius)) {
            if (entity != p) {

                for (int bA = 1; bA <= bloodAddAmount; bA++) {
                    Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                        new BukkitRunnable() {
                            float ranSize = 0.04F + ((float) new Random().nextInt(7) / 100F);
                            Location location = entity.getLocation().clone().add(0, (float) new Random().nextInt(((int) entity.getHeight()) * 10) / 10F, 0);
                            float itDis = 0.1F;
                            Vector ranVecOffset = new Vector(-0.5F + (float) new Random().nextInt(10) / 10F, (float) new Random().nextInt(5) / 5F, -0.25F + (float) new Random().nextInt(10) / 10F).multiply(0.5);

                            public void run() {
                                //code
                                Vector vec = p.getLocation().add(0, 0.3, 0).toVector().subtract(location.toVector()).normalize().multiply(itDis).add(ranVecOffset);

                                location.add(vec);
                                location.setDirection(vec.multiply(-1));

                                ranVecOffset.multiply(0.8);

                                BlockDisplay blud = p.getWorld().spawn(location, BlockDisplay.class);
                                blud.setTransformationMatrix(new Matrix4f().scale(ranSize, ranSize, (float) vec.length()).translate(-0.5F, -0.5F, 0));
                                blud.setInterpolationDuration(2);
                                blud.setInterpolationDelay(1);

                                if (new Random().nextInt(5) == 4)
                                    p.getWorld().spawnParticle(Particle.DUST, location, 2, 0, 0, 0, new Particle.DustTransition(Color.fromRGB(148, 20, 9), Color.fromRGB(74, 9, 4), 0.25F));

                                blud.setBlock(Material.REDSTONE_BLOCK.createBlockData());

                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    blud.setTransformationMatrix(new Matrix4f().scale(0, 0, (float) vec.length() / (1 + new Random().nextInt(10))).translate(-0.5F, -0.5F, 0));
                                }, 1);

                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    blud.setBlock(Material.RED_STAINED_GLASS.createBlockData());
                                }, 1);

                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                    blud.remove();
                                }, 4);

                                itDis = itDis * 1.08f;

                                if (location.distance(p.getLocation().add(0, 1, 0)) < 1 || itDis >= 5) {
                                    cancel();

                                    healPlayer(p, healFactor, -1F, -1F);

                                    new BukkitRunnable() {
                                        int i = 1;
                                        boolean start = false;

                                        public void run() {
                                            //code
                                            if (getBloodStored(p) <= 0 || start) {
                                                addBloodStored(p, 0.0045F);
                                                start = true;
                                            } else {
                                                addBloodStored(p, addedBlood);
                                            }

                                            if (i >= ability.getShiftPassiveTickRate())
                                                cancel();
                                            i++;
                                        }
                                    }.runTaskTimer(TilsU.plugin, 0L, 1L);
                                }
                            }
                        }.runTaskTimer(TilsU.plugin, 0, 1L);
                    }, new Random().nextInt(4));
                }
            }
        }
        return true;
    }

    static void healPlayer(LivingEntity p, float addedHealth, @Nullable Float healthLowCap, @Nullable Float healthHighCap) {
        healthLowCap = (healthLowCap == -1 ? 0 : healthLowCap);
        healthHighCap = (healthHighCap == -1 ? (float) p.getMaxHealth() : healthHighCap);

        double health = p.getHealth();

        if (health < healthLowCap || health > healthHighCap) {
            return;
        }

        double absorption = p.getAbsorptionAmount();
        double change = addedHealth;

        if (change < 0 && absorption > 0) {
            double absorbed = Math.min(absorption, -change);
            absorption -= absorbed;
            change += absorbed;
            p.setAbsorptionAmount(absorption);
        }

        if (change != 0) {
            double newHealth = Math.min(Math.max(health + change, healthLowCap), healthHighCap);
            p.setHealth(newHealth);
        }
    }

    @Override
    default boolean tickPassive(Player p, AbilityUtil ability) {
        p.setExp(bloodStored.get(p.getUniqueId()));

        if (getBloodStored(p) > 0.5) {
            p.getWorld().spawnParticle(Particle.DUST, p.getLocation(), 4, 0.15, 0, 0.15, new Particle.DustTransition(Color.fromRGB(148, 20, 9), Color.fromRGB(74, 9, 4), 0.75F));
        }

        addBloodStored(p, passiveBloodAddedPerTick);

        v.put(p.getUniqueId(), b.get(p.getUniqueId()).getLocation().toVector());
        b.get(p.getUniqueId()).teleport(p.getLocation().add(0, 3, 0));

        if (playerBleed.containsKey(p.getUniqueId())) {
            for (var v : playerBleed.get(p.getUniqueId()).entrySet()) {
                if (v.getValue().left() < System.currentTimeMillis()) {
                    playerBleed.get(p.getUniqueId()).remove(v.getKey());
                }
            }
        }
        return true;
    }

    static void bleedPassiveTick(Player p) {
        new BukkitRunnable() {
            public void run() {
                if (playerBleed.containsKey(p.getUniqueId())) {
                    for (var v : playerBleed.get(p.getUniqueId()).entrySet()) {
                        if (v.getValue().left() > System.currentTimeMillis()) {
                            if (v.getKey() != null) {
                                LivingEntity entity = v.getKey();
                                if (entity instanceof Player player && player.getGameMode() != GameMode.SURVIVAL)
                                    continue;
                                healPlayer(entity, -getEntityBleedDamage(p, entity), 6F, -1F);
                                DecimalFormat df = new DecimalFormat("0.00");
                                Random ran = new Random();
                                for (int i = 0; i <= 5; i++) {
                                    Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                        new BukkitRunnable() {
                                            int trailLength = 5;
                                            Location trailStart = entity.getLocation().add(0, 0.5 + (float) ran.nextInt(11) / 10F, 0);
                                            Vector ranOffset = new Vector(-1 + (float) ran.nextInt(200) / 100F, -0.5 + (float) ran.nextInt(70) / 1000F, -1 + (float) ran.nextInt(200) / 100F).normalize().multiply(0.35);

                                            public void run() {

                                                if (trailLength <= 0)
                                                    cancel();

                                                trailStart.setDirection(ranOffset);

                                                BlockDisplay blood = p.getWorld().spawn(trailStart, BlockDisplay.class);
                                                blood.setTransformationMatrix(new Matrix4f().scale(0.05F + (float) ran.nextInt(700) / 10000F, 0.05F + (float) ran.nextInt(700) / 10000F, (float) ranOffset.length()).translate(-0.5F, -0.5F, 0));
                                                blood.setInterpolationDuration(8);
                                                blood.setInterpolationDelay(-1);
                                                blood.setBlock(Material.REDSTONE_BLOCK.createBlockData());

                                                float length = (float) ranOffset.length();
                                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                                    blood.setTransformationMatrix(new Matrix4f().scale(0, 0, length).translate(-0.5F, -0.5F, 0));
                                                }, 1);

                                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                                    blood.setBlock(Material.RED_STAINED_GLASS.createBlockData());
                                                }, 1);

                                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                                    blood.remove();
                                                }, 10);

                                                if (trailStart.clone().add(ranOffset).getBlock().isSolid()) {
                                                    Location loc = trailStart.clone().add(ranOffset);
                                                    loc.setDirection(new Vector(0, 1, 0));
                                                    /**
                                                     BlockDisplay marker = p.getWorld().spawn(trailStart, BlockDisplay.class);
                                                     marker.setTransformationMatrix(new Matrix4f().scale(0.05F, 0.05F, 2));
                                                     marker.setBlock(Material.LIME_STAINED_GLASS.createBlockData());
                                                     */
                                                    loc.setDirection(loc.getDirection().normalize().multiply(0.01));
                                                    for (int i = 0; i <= 50; i++) {
                                                        if (!loc.add(loc.getDirection().normalize().multiply(0.01)).getBlock().isSolid()) {
                                                            loc.setDirection(loc.getDirection().multiply(-1));
                                                            loc.setYaw(ran.nextInt(91));
                                                            BlockDisplay splatter = p.getWorld().spawn(loc, BlockDisplay.class);
                                                            float ranSize = 0.1F + ((float) ran.nextInt(30) / 100);
                                                            splatter.setTransformationMatrix(new Matrix4f().scale(ranSize, ranSize, 0.04F).translate(-0.5F, -0.5F, 0));
                                                            Material ranBLock = Material.REDSTONE_BLOCK;
                                                            switch (ran.nextInt(3)) {
                                                                case 0:
                                                                    ranBLock = Material.RED_CONCRETE;
                                                                    break;
                                                                case 1:
                                                                    ranBLock = Material.RED_CONCRETE_POWDER;
                                                                    break;
                                                            }
                                                            splatter.setBlock(ranBLock.createBlockData());
                                                            splatter.setInterpolationDelay(30);
                                                            splatter.setInterpolationDuration(40);
                                                            Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                                                splatter.setTransformationMatrix(new Matrix4f().scale(0, 0, 0.04F).translate(-0.5F, -0.5F, 0));
                                                                Utils.scheduler.runTaskLater(Utils.plugin, () -> {
                                                                    splatter.remove();
                                                                }, 41);
                                                            }, 31);
                                                            break;
                                                        }
                                                    }
                                                    cancel();
                                                }

                                                if (trailStart.clone().getBlock().isSolid()) {
                                                    cancel();
                                                }

                                                trailStart.add(ranOffset);

                                                ranOffset.add(new Vector(0, -0.015F, 0));
                                                ranOffset.multiply(0.8F);

                                                trailLength--;
                                            }
                                        }.runTaskTimer(TilsU.plugin, 0L, 1L);
                                    }, i);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(TilsU.plugin, 0L, 10);
    }

    HashMap<UUID, BlockDisplay> b = new HashMap<>();
    HashMap<UUID, Vector> v = new HashMap<>();

    @Override
    default void selectAbility(Player p, AbilityUtil ability) {
        bloodStored.put(p.getUniqueId(), 0F);
        playerBleed.put(p.getUniqueId(), new HashMap<>());
        bleedPassiveTick(p);
        BlockDisplay bd = p.getWorld().spawn(p.getLocation(), BlockDisplay.class);
        bd.setTransformationMatrix(new Matrix4f().scale(0.25F).translate(-0.5F, -0.5F, -0.5F));
        //bd.setBlock(Material.GOLD_BLOCK.createBlockData()); //debug
        b.put(p.getUniqueId(), bd);
    }

    @Override
    default void unselectAbility(Player p, AbilityUtil ability) {
        bloodStored.remove(p.getUniqueId());
        playerBleed.remove(p.getUniqueId());
        b.get(p.getUniqueId()).remove();
        b.remove(p.getUniqueId());
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

    static void addEntityBleed(Player p, LivingEntity bleedingPlayer, float bleedDamage) {
        getPlayerBleedingEntitys(p).put(bleedingPlayer, Pair.of(System.currentTimeMillis() + bleedDuration, bleedDamage));
    }

    static HashMap<LivingEntity, Pair<Long, Float>> getPlayerBleedingEntitys(Player p) {
        if (!playerBleed.containsKey(p.getUniqueId()))
            playerBleed.put(p.getUniqueId(), new HashMap<>());
        return playerBleed.get(p.getUniqueId());
    }

    static float getEntityBleedDamage(Player p, Entity bleedingEntity) {
        return playerBleed.get(p.getUniqueId()).containsKey(bleedingEntity) ? playerBleed.get(p.getUniqueId()).get(bleedingEntity).right() : 0;
    }

    static Long getEntityBleedDuration(Player p, Entity bleedingEntity) {
        return playerBleed.get(p.getUniqueId()).containsKey(bleedingEntity) ? playerBleed.get(p.getUniqueId()).get(bleedingEntity).left() : 0;
    }

}
