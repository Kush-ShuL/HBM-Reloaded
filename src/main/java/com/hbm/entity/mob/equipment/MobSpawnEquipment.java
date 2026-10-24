package com.hbm.entity.mob.equipment;

import com.hbm.pollution.PollutionSystem;
import com.hbm.pollution.PollutionType;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.world.structure.generated.SourceRandom;
import com.hbm.world.structure.logic.LogicDependencies;
import com.hbm.world.structure.logic.MobUtil;
import com.hbm.world.structure.logic.WeightedRandomObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

/** Actual spawn/check/despawn equipment dispatch, independent from structure authoring actions. */
public final class MobSpawnEquipment {
    private static final Random EQUIPMENT_RANDOM = new Random();
    private static boolean registered;

    public static void register(ModContainer container) {
        if (registered) return;
        registered = true;
        MobEquipmentConfig.register(container);
        NeoForge.EVENT_BUS.addListener(MobSpawnEquipment::check);
        NeoForge.EVENT_BUS.addListener(MobSpawnEquipment::finalized);
        NeoForge.EVENT_BUS.addListener(MobSpawnEquipment::despawn);
        NeoForge.EVENT_BUS.addListener(MobSpawnEquipment::joined);
    }

    private static void check(MobSpawnEvent.PositionCheck event) { spawn(event); }
    private static void finalized(FinalizeSpawnEvent event) { spawn(event); }
    private static void despawn(MobDespawnEvent event) { spawn(event); }

    private static void spawn(MobSpawnEvent event) {
        Mob entity = event.getEntity();
        if (event instanceof MobDespawnEvent) {
            // The old AllowDespawn hook ran only at this idle-age gate, before its result was interpreted.
            if (entity.isPersistenceRequired() || (entity.getNoActionTime() & 31) != 31) return;
        } else if (event instanceof MobSpawnEvent.PositionCheck check) {
            // Actual natural spawning and HBM's explicit canEntitySpawn callers; not every new spawn reason.
            if (check.getSpawnType() != MobSpawnType.NATURAL && check.getSpawnType() != MobSpawnType.EVENT) return;
        } else if (event instanceof FinalizeSpawnEvent finalized) {
            if (finalized.getSpawnType() != MobSpawnType.NATURAL) return;
        } else return;
        decorate(entity, event.getLevel(), event.getX(), event.getY(), event.getZ());
    }

    private static void decorate(Mob entity, ServerLevelAccessor level, double x, double y, double z) {
        if (!MobEquipmentConfig.equipmentEnabled() || entity.isBaby() || entity.level().isClientSide) return;
        // Other living classes had an empty pool and no random selection in this callback.
        if (!(entity instanceof Zombie) && !(entity instanceof AbstractSkeleton)) return;
        synchronized (EQUIPMENT_RANDOM) {
            // Event-local access retains a WorldGenRegion's random rather than borrowing its ServerLevel random.
            var worldRandom = new SourceRandom(level.getRandom());
            float soot = PollutionSystem.get(level.getLevel(), BlockPos.containing((float) x, (float) y, (float) z), PollutionType.SOOT);
            Map<Integer, List<WeightedRandomObject>> slots;
            if (entity instanceof Zombie) {
                // The chance is consumed before testing soot, exactly as in the original short-circuit expression.
                if (worldRandom.nextFloat() < 0.005F && soot > 2) {
                    MobUtil.equipFullSet(entity, LogicDependencies.item("hazmat_helmet"), LogicDependencies.item("hazmat_plate"),
                            LogicDependencies.item("hazmat_legs"), LogicDependencies.item("hazmat_boots"));
                    return;
                }
                slots = MobUtil.slotPoolCommonS;
            } else {
                slots = MobUtil.slotPoolRangedS;
                ItemStack weapon = skeletonWeapon(soot, worldRandom);
                // The shared map mutation, null50/weapon1 weights and HashMap slot iteration are source behavior.
                slots.put(0, MobUtil.createSlotPool(50, weapon.isEmpty() ? new Object[][] {} : new Object[][] {{weapon, 1}}));
            }
            MobUtil.assignItemsToEntity(entity, slots, EQUIPMENT_RANDOM);
        }
    }

    private static ItemStack skeletonWeapon(float soot, Random random) {
        if (!MobEquipmentConfig.skeletonWeaponsEnabled()) return ItemStack.EMPTY;
        soot -= MobEquipmentConfig.sootReduction(); // Compound assignment deliberately casts the result back to float.
        if (random.nextDouble() > Math.log(soot) * 0.25) return ItemStack.EMPTY;
        var pool = new ArrayList<WeightedRandomObject>();
        if (soot < 0.3) {
            pool.add(new WeightedRandomObject(new ItemStack(LogicDependencies.item("gun_pepperbox")), 5));
            pool.add(new WeightedRandomObject(null, 20));
        } else if (soot > 0.3 && soot < 1) pool.addAll(MobUtil.slotPoolGuns.get(0.3));
        else if (soot < 3) pool.addAll(MobUtil.slotPoolGuns.get(1D));
        else if (soot < 5) pool.addAll(MobUtil.slotPoolGuns.get(3D));
        else pool.addAll(MobUtil.slotPoolGuns.get(5D));
        return MobUtil.choose(random, pool).asStack();
    }

    private static void joined(EntityJoinLevelEvent event) {
        var parts = event.getEntity().getParts();
        if (parts != null) for (var part : parts) if (part == null) {
            com.hbm.HbmReloaded.LOGGER.error("Prevented spawning of multipart entity {} due to parts being null!",
                    event.getEntity().getClass().getCanonicalName());
            event.setCanceled(true);
            return;
        }
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob entity)) return;
        if (entity.getMainHandItem().getItem() instanceof ItemGunBaseNT) MobUtil.addFireTask(entity);
    }
    private MobSpawnEquipment() {}
}
