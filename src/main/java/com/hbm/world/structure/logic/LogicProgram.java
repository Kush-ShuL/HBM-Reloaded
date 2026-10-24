package com.hbm.world.structure.logic;

import com.hbm.block.skeletonholder.SkeletonHolderEntity;
import com.hbm.bomb.conventional.TimedChargeBlockEntity;
import com.hbm.bomb.nuclear.NuclearFallingBlock;
import com.hbm.door.generic.GenericDoorBlockEntity;
import com.hbm.energy.EnergyHandler;
import com.hbm.entity.missile.EntityMissileTier2;
import com.hbm.entity.mob.ai.EntityAIFireGun;
import com.hbm.entity.mob.undead.UndeadSoldier;
import com.hbm.entity.mob.undead.UndeadSoldierContent;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.HbmExposure;
import com.hbm.machine.pedestal.PedestalBlockEntity;
import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

/** All and only the source enabled 16/8/3 program table entries; no dead action or fake dependency is registered. */
public final class LogicProgram {
    public record Step(Consumer<LogicRuntime> run, Supplier<List<String>> readiness) { public List<String> pending() { return readiness.get(); } }
    public record Condition(Predicate<LogicRuntime> run, Supplier<List<String>> readiness) { public List<String> pending() { return readiness.get(); } }
    public record Interaction(BiConsumer<LogicRuntime, Use> run, Supplier<List<String>> readiness) { public List<String> pending() { return readiness.get(); } }
    public record Use(Player player, Direction side, float subX, float subY, float subZ) {}
    public static final LinkedHashMap<String, Step> actions = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Condition> conditions = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Interaction> interactions = new LinkedHashMap<>();
    private static final Supplier<List<String>> READY = List::of;

    @SafeVarargs
    private static Supplier<List<String>> dependencies(String[] items, String[] blocks, Map<Integer, List<WeightedRandomObject>>... pools) {
        return () -> {
            var pending = new TreeSet<String>(LogicDependencies.pendingItems(items));
            for (String block : blocks) if (!LogicWorld.blockReady(block)) pending.add("block " + block + " -> " + LogicWorld.blockId(block));
            for (var pool : pools) pending.addAll(LogicDependencies.pendingPool(pool));
            // Only pools containing the actual original filter-mask candidates require the installation product.
            for (var pool : pools) for (var list : pool.values()) for (var entry : list)
                if (entry.item instanceof WeightedRandomObject.SourceItem item && List.of("gas_mask_m65", "gas_mask_olde", "gas_mask_mono").contains(item.symbol()))
                    pending.addAll(LogicDependencies.pendingItems("gas_mask_filter"));
            return List.copyOf(pending);
        };
    }
    private static void action(String id, Consumer<LogicRuntime> run, Supplier<List<String>> ready) { actions.put(id, new Step(run, ready)); }
    private static void condition(String id, Predicate<LogicRuntime> run, Supplier<List<String>> ready) { conditions.put(id, new Condition(run, ready)); }
    private static void interaction(String id, BiConsumer<LogicRuntime, Use> run, Supplier<List<String>> ready) { interactions.put(id, new Interaction(run, ready)); }
    public static List<String> getActionNames() { return new ArrayList<>(actions.keySet()); }
    public static List<String> getConditionNames() { return new ArrayList<>(conditions.keySet()); }
    public static List<String> getInteractionNames() { return new ArrayList<>(interactions.keySet()); }

    static { initialize(); }
    public static void initialize() {
        actions.clear(); conditions.clear(); interactions.clear();
        action("FODDER_WAVE", LogicProgram::fodder, dependencies(new String[0], new String[]{"block_steel"}, MobUtil.slotPoolAdv));
        action("POWER_LOCK", LogicProgram::powerLock, READY);
        action("COLLAPSE_ROOF_RAD_5", tile -> collapse(tile, 4), READY);
        action("COLLAPSE_ROOF_RAD_10", tile -> collapse(tile, 8), READY);
        action("BOMB_TRAP", LogicProgram::bombTrap, dependencies(new String[0], new String[]{"charge_c4"}));
        action("BOMB_CRANE", LogicProgram::bombCrane, dependencies(new String[0], new String[]{"charge_c4", "block_steel"}));
        action("DEAD_GUY_CRANE", LogicProgram::deadGuyCrane, dependencies(new String[]{"gun_hangman", "clay_tablet"}, new String[]{"skeleton_holder"}));
        // DEAD_GUY_BASE_TOWER is defined in source but its different-name registration is commented out.
        action("SKELETON_GUN_TIER_1", tile -> skeletons(tile, 1), dependencies(new String[0], new String[0], MobUtil.slotPoolGunsTier1, MobUtil.slotPoolMasks, MobUtil.slotPoolRanged));
        action("SKELETON_GUN_TIER_2", tile -> skeletons(tile, 2), dependencies(new String[0], new String[0], MobUtil.slotPoolGunsTier2, MobUtil.slotPoolRanged));
        action("SKELETON_GUN_TIER_3", tile -> skeletons(tile, 3), dependencies(new String[0], new String[0], MobUtil.slotPoolGunsTier3, MobUtil.slotPoolAdvRanged));
        action("ZOMBIE_TIER_1", tile -> zombies(tile, false), dependencies(new String[0], new String[0], MobUtil.slotPoolCommon));
        action("ZOMBIE_TIER_2", tile -> zombies(tile, true), dependencies(new String[0], new String[0], MobUtil.slotPoolAdv));
        action("ABERRATOR", LogicProgram::aberrator, dependencies(new String[]{"item_secret_aberrator", "clay_tablet"}, new String[0]));
        action("PUZZLE_TEST", LogicProgram::puzzle, dependencies(new String[]{"gun_bolter"}, new String[]{"crate_steel"}));
        action("MISSILE_STRIKE", LogicProgram::missile, dependencies(new String[0], new String[]{"block_electrical_scrap"}));
        action("IRRADIATE_ENTITIES_AOE", LogicProgram::irradiate, READY);

        condition("EMPTY", tile -> false, READY);
        condition("PLAYER_CUBE_3", tile -> !LogicWorld.players(tile, 3).isEmpty(), READY);
        condition("PLAYER_CUBE_5", tile -> !LogicWorld.players(tile, 5).isEmpty(), READY);
        condition("PLAYER_CUBE_25", tile -> !LogicWorld.players(tile, 25).isEmpty(), READY);
        condition("BOMB_CRANE", tile -> {
            if (tile.phase == 0) {
                var pos = tile.getBlockPos().above(); tile.server().setBlock(pos, LogicWorld.block("charge_c4", 1), 3);
                if (tile.server().getBlockEntity(pos) instanceof TimedChargeBlockEntity bomb) bomb.timer = 200;
            }
            return !LogicWorld.players(tile, 10).isEmpty();
        }, dependencies(new String[0], new String[]{"charge_c4"}));
        condition("ABERRATOR", LogicProgram::aberratorCondition, READY);
        condition("REDSTONE", tile -> tile.server().hasNeighborSignal(tile.getBlockPos()), READY);
        condition("PUZZLE_TEST", tile -> {
            var world = tile.server(); var pos = tile.getBlockPos();
            if (tile.phase == 0 && world.hasNeighborSignal(pos)) {
                LogicWorld.message(LogicWorld.closest(tile, 25), "message.hbm.logic.puzzle_hint");
                world.setBlock(pos.above(), LogicWorld.block("pedestal", 0), 3); return true;
            }
            return tile.phase == 1 && world.getBlockEntity(pos.above()) instanceof PedestalBlockEntity pedestal
                && !pedestal.item().isEmpty() && pedestal.item().is(LogicDependencies.item("big_sword"));
        }, dependencies(new String[]{"big_sword"}, new String[]{"pedestal"}));

        interaction("POWER_LOCK", LogicProgram::powerLockUse, READY);
        interaction("TEST", (tile, use) -> {
            if (tile.phase > 1) return;
            if (!use.player().getMainHandItem().isEmpty()) use.player().getMainHandItem().shrink(1);
            tile.phase++;
        }, READY);
        interaction("RADAWAY_INJECTOR", (tile, use) -> {
            var stack = use.player().getMainHandItem();
            if (!stack.isEmpty() && stack.is(LogicDependencies.item("key"))) {
                stack.shrink(1); LogicWorld.message(use.player(), "message.hbm.logic.rad_treatment");
                use.player().addEffect(new MobEffectInstance(ExposureEffects.RADAWAY, 3 * 60 * 20, 4));
                use.player().addEffect(new MobEffectInstance(ExposureEffects.RADX, 3 * 60 * 20, 4));
                tile.phase = 2; tile.timer = 0;
            }
        }, dependencies(new String[]{"key"}, new String[0]));
    }

    private static void fodder(LogicRuntime tile) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos(); double vx = 5, vz = 0;
        double angle = 36D / 180D * Math.PI, cos = Math.cos(angle), sin = Math.sin(angle);
        for (int i = 0; i < 10; i++) {
            var mob = new Zombie(world); mob.moveTo(p.getX() + .5 + vx, world.getHeight(Heightmap.Types.MOTION_BLOCKING, p.getX(), p.getZ()), p.getZ() + .5 + vz, i * 36F, 0);
            MobUtil.assignItemsToEntity(mob, MobUtil.slotPoolAdv, new Random()); world.addFreshEntity(mob);
            double nextX = vx * cos + vz * sin; vz = vz * cos - vx * sin; vx = nextX;
        }
        world.setBlock(p, LogicWorld.block("block_steel", 0), 3);
    }
    private static void collapse(LogicRuntime tile, int radius) {
        if (tile.phase == 0) return;
        var world = tile.server(); var p = tile.getBlockPos(); int threshold = radius * radius / 2;
        for (int x = -radius; x < radius; x++) for (int y = -radius; y < radius; y++) for (int z = -radius; z < radius; z++)
            if (x * x + y * y + z * z < threshold) {
                var at = p.offset(x, y, z); var state = world.getBlockState(at);
                if (state.getBlock().getExplosionResistance() <= 70) world.addFreshEntity(new NuclearFallingBlock(world, at.getX() + .5, at.getY() + .5, at.getZ() + .5, state));
            }
        world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
    }
    private static void skeletons(LogicRuntime tile, int tier) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos();
        for (int i = 0; i < 3; i++) {
            var mob = new Skeleton(EntityType.SKELETON, world); mob.moveTo(p.getX(), p.getY(), p.getZ(), 0, 0);
            if (tier > 1) {
                var task = new EntityAIFireGun(mob); task.minWait = 4; task.maxWait = 5; task.maxRange = tier == 2 ? 50 : 100;
                task.burstTime = 6; task.inaccuracy = tier == 2 ? 5F : 1F; task.randomBurst = false; MobUtil.addFireTask(mob, task);
            }
            MobUtil.assignItemsToEntity(mob, tier == 1 ? MobUtil.slotPoolGunsTier1 : tier == 2 ? MobUtil.slotPoolGunsTier2 : MobUtil.slotPoolGunsTier3, new Random());
            if (tier == 1) MobUtil.assignItemsToEntity(mob, MobUtil.slotPoolMasks, new Random());
            MobUtil.assignItemsToEntity(mob, tier == 3 ? MobUtil.slotPoolAdvRanged : MobUtil.slotPoolRanged, new Random());
            world.addFreshEntity(mob); world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        }
    }
    private static void zombies(LogicRuntime tile, boolean advanced) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos();
        for (int i = 0; i < 3; i++) {
            var mob = new Zombie(world); mob.moveTo(p.getX(), p.getY(), p.getZ(), 0, 0);
            MobUtil.assignItemsToEntity(mob, advanced ? MobUtil.slotPoolAdv : MobUtil.slotPoolCommon, new Random());
            world.addFreshEntity(mob); world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        }
    }
    private static void aberrator(LogicRuntime tile) {
        var world = tile.server(); var p = tile.getBlockPos();
        if (tile.phase == 1 || tile.phase == 2) {
            tile.player = LogicWorld.closest(tile, 25);
            if (tile.timer == 0) {
                double vx = 20, vz = 0, angle = 36D / 180D * Math.PI, cos = Math.cos(angle), sin = Math.sin(angle);
                for (int i = 0; i < 10; i++) {
                    if (vx > 8) vx += world.random.nextInt(10) - 5;
                    var mob = UndeadSoldierContent.SOLDIER.get().create(world);
                    if (mob == null) throw new IllegalStateException("Actual UndeadSoldier factory returned null");
                    for (int j = 0; j < 7; j++) {
                        mob.moveTo(p.getX() + .5 + vx, world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) (p.getX() + .5 + vx), (int) (p.getZ() + .5 + vz)), p.getZ() + .5 + vz, i * 36F, 0);
                        if (mob.sourceCanSpawnHere()) {
                            mob.finalizeSpawn(world, world.getCurrentDifficultyAt(mob.blockPosition()), net.minecraft.world.entity.MobSpawnType.SPAWNER, null);
                            if (tile.player != null) mob.setTarget(tile.player);
                            world.addFreshEntity(mob); break;
                        }
                    }
                    double nextX = vx * cos + vz * sin; vz = vz * cos - vx * sin; vx = nextX;
                }
            }
        }
        if (tile.phase > 2) {
            if (world.getBlockEntity(p.above(18)) instanceof SkeletonHolderEntity holder)
                holder.setItem(world.random.nextInt(5) == 0 ? LogicWorld.stack("item_secret_aberrator") : LogicWorld.stack("clay_tablet", 1));
            world.setBlock(p, Blocks.OBSIDIAN.defaultBlockState(), 3);
        }
    }
    private static boolean aberratorCondition(LogicRuntime tile) {
        var world = tile.server(); var p = tile.getBlockPos();
        if (world.getDifficulty() == Difficulty.PEACEFUL) return false;
        boolean nearby = !LogicWorld.players(tile, 10).isEmpty();
        if (tile.phase == 0) return world.getGameTime() % 20 == 0 && nearby;
        if (tile.phase < 3) {
            if (world.getGameTime() % 20 != 0 || tile.timer < 60) return false;
            return LogicWorld.entities(world, UndeadSoldier.class, p.getX(), p.getY(), p.getZ(), p.getX() - 2, p.getY() + 1, p.getZ() + 1, 50, 20, 50).isEmpty() && nearby;
        }
        return false;
    }
    private static void puzzle(LogicRuntime tile) {
        if (tile.phase != 2) return;
        var world = tile.server(); var p = tile.getBlockPos(); world.setBlock(p, LogicWorld.block("crate_steel", 0), 3);
        var bolt = EntityType.LIGHTNING_BOLT.create(world);
        if (bolt == null) throw new IllegalStateException("Native lightning factory returned null");
        bolt.moveTo(p.getX(), world.getHeight(Heightmap.Types.MOTION_BLOCKING, p.getX(), p.getZ()) + 2, p.getZ()); world.addFreshEntity(bolt);
        ((Container) world.getBlockEntity(p)).setItem(15, LogicWorld.stack("gun_bolter"));
    }
    private static void missile(LogicRuntime tile) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos(); int d = LogicWorld.orientation(tile.direction), parallel = LogicWorld.rotateUp(d);
        LogicWorld.message(LogicWorld.closest(tile, 25), "message.hbm.logic.missile_fired");
        var missile = new EntityMissileTier2.EntityMissileStrong(world, p.getX() + LogicWorld.DX[d] * 300, 200, p.getZ() + LogicWorld.DZ[d] * 300,
            p.getX() + LogicWorld.DX[parallel] * 30 + LogicWorld.DX[d] * 30, p.getZ() + LogicWorld.DZ[parallel] * 30 + LogicWorld.DZ[d] * 30);
        int cx = Mth.floor(missile.getX() / 16D), cz = Mth.floor(missile.getZ() / 16D);
        for (int x = cx - 2; x <= cx + 2; x++) for (int z = cz - 2; z <= cz + 2; z++) world.getChunk(x, z);
        world.addFreshEntity(missile); world.setBlock(p, LogicWorld.block("block_electrical_scrap", 0), 3);
    }
    private static void irradiate(LogicRuntime tile) {
        var world = tile.server(); var p = tile.getBlockPos(); int d = LogicWorld.opposite(tile.direction), rot = LogicWorld.rotateUp(d);
        var entities = LogicWorld.entities(world, LivingEntity.class, p.getX() - LogicWorld.DX[rot], p.getY() - 1, p.getZ() - LogicWorld.DZ[rot],
            p.getX() + LogicWorld.DX[rot] + LogicWorld.DX[d] * 15, p.getY() + 1, p.getZ() + LogicWorld.DZ[rot] + LogicWorld.DZ[d] * 15, 2, 2, 2);
        for (var entity : entities) {
            double dx = entity.getX() - (p.getX() + .5), dy = entity.getY() + entity.getEyeHeight() - (p.getY() + .5), dz = entity.getZ() - (p.getZ() + .5);
            double originalLength = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            double vx = originalLength < 1E-4 ? 0 : dx / originalLength, vy = originalLength < 1E-4 ? 0 : dy / originalLength, vz = originalLength < 1E-4 ? 0 : dz / originalLength;
            double length = Math.max(originalLength, 1D); float resistance = 0;
            for (int i = 1; i < length; i++) resistance += world.getBlockState(BlockPos.containing(p.getX() + .5 + vx * i, p.getY() + .5 + vy * i, p.getZ() + .5 + vz * i)).getBlock().getExplosionResistance();
            if (resistance < 1) resistance = 1;
            float rads = 100F; rads /= resistance; rads /= (float) (length * length);
            HbmExposure.contaminate(entity, HbmExposure.Contamination.CREATIVE, rads, entity instanceof Player player && LogicWorld.heavyHazmat(player));
        }
        if (tile.phase == 2 && tile.timer > 40) {
            LogicWorld.message(LogicWorld.closest(tile, 25), "message.hbm.logic.rad_lockdown");
            for (int i = 1; i < 20; i++) {
                var at = p.offset(LogicWorld.DX[d] * i, 1, LogicWorld.DZ[d] * i); var state = world.getBlockState(at);
                if (state.getBlock() instanceof DummyableBlock block) {
                    var core = block.findCore(world, at);
                    if (core != null && world.getBlockEntity(core) instanceof GenericDoorBlockEntity door) { door.setPins(456); door.close(); door.lock(); break; }
                }
            }
            tile.phase = 3;
        }
    }
    private static void powerLock(LogicRuntime tile) {
        if (tile.phase != 0 || LogicWorld.players(tile, 3).isEmpty()) return;
        LogicWorld.message(LogicWorld.closest(tile, 300), "message.hbm.logic.power_warning"); tile.phase++;
        var world = tile.server(); var p = tile.getBlockPos();
        for (int i = 0; i < 6; i++) if (world.getBlockEntity(p.offset(LogicWorld.DX[i], LogicWorld.DY[i], LogicWorld.DZ[i])) instanceof LockableBlockEntity safe) { safe.setPins(world.random.nextInt(999)); break; }
    }
    private static void powerLockUse(LogicRuntime tile, Use use) {
        var world = tile.server(); var p = tile.getBlockPos(); EnergyHandler handler = null;
        for (int i = 0; i < 6; i++) if (world.getBlockEntity(p.offset(LogicWorld.DX[i], LogicWorld.DY[i], LogicWorld.DZ[i])) instanceof EnergyHandler found) { handler = found; break; }
        if (handler == null || handler.getPowerExact().compareTo(BigInteger.valueOf(500_000)) <= 0) { LogicWorld.message(use.player(), "message.hbm.logic.power_help"); return; }
        LogicWorld.message(use.player(), "message.hbm.logic.power_restored");
        for (int i = 0; i < 6; i++) if (world.getBlockEntity(p.offset(LogicWorld.DX[i], LogicWorld.DY[i], LogicWorld.DZ[i])) instanceof LockableBlockEntity safe) {
            safe.unlock(); world.playSound(null, use.player().getX(), use.player().getY(), use.player().getZ(), HbmSounds.get("block.lockOpen"), SoundSource.PLAYERS, 3, .8F); break;
        }
    }
    private static void bombTrap(LogicRuntime tile) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos(); int d = LogicWorld.opposite(tile.direction); var at = p.offset(0, 0, LogicWorld.DZ[d]);
        world.setBlock(at, LogicWorld.block("charge_c4", 2), 3);
        if (world.getBlockEntity(at) instanceof TimedChargeBlockEntity bomb) { bomb.timer = 2400; bomb.started = true; }
        world.setBlock(p, tile.disguiseId == null ? Blocks.AIR.defaultBlockState() : com.hbm.world.structure.LegacyStructureStates.resolve(tile.disguiseId, 0, 0), 3);
    }
    private static void bombCrane(LogicRuntime tile) {
        var world = tile.server(); var p = tile.getBlockPos();
        if (tile.phase == 0) { world.setBlock(p.above(), LogicWorld.block("charge_c4", 1), 3); if (world.getBlockEntity(p.above()) instanceof TimedChargeBlockEntity bomb) bomb.timer = 1200; }
        if (tile.phase >= 1) { if (world.getBlockEntity(p.above()) instanceof TimedChargeBlockEntity bomb) bomb.started = true; world.setBlock(p, LogicWorld.block("block_steel", 0), 3); }
    }
    private static void deadGuyCrane(LogicRuntime tile) {
        if (tile.phase != 1) return;
        var world = tile.server(); var p = tile.getBlockPos(); world.setBlock(p, LogicWorld.block("skeleton_holder", 0), 3);
        var player = LogicWorld.players(tile, 25).get(0);
        if (world.getBlockEntity(p) instanceof SkeletonHolderEntity holder) {
            boolean hasGun = player.getInventory().items.stream().anyMatch(stack -> stack.is(LogicDependencies.item("gun_hangman")));
            holder.setItem(hasGun ? LogicWorld.stack("clay_tablet", 0) : LogicWorld.stack("gun_hangman"));
        }
    }
    private LogicProgram() {}
}
