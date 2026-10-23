package com.hbm.pollution;

import com.hbm.HbmReloaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** X5846's four-channel pollution, emissions, diffusion, terrain damage and soot mob buffs. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID)
public final class PollutionSystem {
    public static final float SOOT_PER_SECOND = 1F / 25F;
    public static final float HEAVY_METAL_PER_SECOND = 1F / 50F;
    public static final float POISON_PER_SECOND = 1F / 50F;
    private static final TagKey<EntityType<?>> GLYPHIDS = TagKey.create(Registries.ENTITY_TYPE, HbmReloaded.id("glyphids"));
    private static int ticks;

    private PollutionSystem() {}

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, PollutionConfig.SPEC, "hbm-pollution-server.toml");
    }

    private static PollutionWorldData data(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PollutionWorldData.FACTORY, "hbm_pollution");
    }

    public static float get(ServerLevel level, BlockPos position, PollutionType type) {
        if (!PollutionConfig.ENABLED.get()) return 0;
        PollutionData cell = data(level).cells.get(PollutionData.Cell.at(position.getX(), position.getZ()));
        return cell == null ? 0 : cell.get(type);
    }

    /** Source nullable pollution-data query: absence differs from an existing cell containing zero. */
    public static Float getIfPresent(ServerLevel level, BlockPos position, PollutionType type) {
        if (!PollutionConfig.ENABLED.get()) return null;
        PollutionData cell = data(level).cells.get(PollutionData.Cell.at(position.getX(), position.getZ()));
        return cell == null ? null : cell.get(type);
    }

    public static void increment(ServerLevel level, BlockPos position, PollutionType type, float amount) {
        if (!PollutionConfig.ENABLED.get()) return;
        PollutionWorldData world = data(level);
        PollutionData cell = world.cells.computeIfAbsent(PollutionData.Cell.at(position.getX(), position.getZ()), ignored -> new PollutionData());
        cell.set(type, Mth.clamp((float) (cell.get(type) + amount * PollutionConfig.emissionMultiplier()), 0, 10_000));
        world.setDirty();
    }

    public static void decrement(ServerLevel level, BlockPos position, PollutionType type, float amount) {
        increment(level, position, type, -amount);
    }

    /** Detached original four-channel PermaSync view; clients never own/advance world cells. */
    public static float[] snapshot(ServerLevel level,BlockPos position){
        var result=new float[PollutionType.values().length];
        if(!PollutionConfig.ENABLED.get())return result;
        var cell=data(level).cells.get(PollutionData.Cell.at(position.getX(),position.getZ()));
        if(cell!=null)for(var type:PollutionType.values())result[type.ordinal()]=cell.get(type);
        return result;
    }

    public static void set(ServerLevel level, BlockPos position, PollutionType type, float amount) {
        if (!PollutionConfig.ENABLED.get()) return;
        PollutionWorldData world = data(level);
        world.cells.computeIfAbsent(PollutionData.Cell.at(position.getX(), position.getZ()), ignored -> new PollutionData()).set(type, amount);
        world.setDirty();
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        if (!PollutionConfig.ENABLED.get()) return;
        boolean advance = ++ticks >= 60;
        if (advance) ticks = 0;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            PollutionWorldData world = data(level);
            damageVegetation(level, world);
            if (advance) world.advance();
        }
    }

    private static void damageVegetation(ServerLevel level, PollutionWorldData world) {
        world.cells.forEach((cell, value) -> {
            if (value.get(PollutionType.POISON) < 15) return;
            for (int index = 0; index < 5; index++) {
                int x = (cell.x() << 6) + level.random.nextInt(64), z = (cell.z() << 6) + level.random.nextInt(64);
                if (level.getChunkSource().getChunkNow(x >> 4, z >> 4)==null) continue;
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - level.random.nextInt(3) + 1;
                BlockPos position = new BlockPos(x, y, z);
                var state = level.getBlockState(position);
                var material=com.hbm.block.fluid.LegacyMaterials.of(state);
                boolean replaceSoil = state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT);
                boolean removePlant = material.name().equals("leaves") || material.name().equals("plants") || state.is(BlockTags.LEAVES) || state.getBlock() instanceof BushBlock || state.getBlock() instanceof SugarCaneBlock;
                if (!replaceSoil && !removePlant) continue;
                // Native neighbour/physics callbacks may query across the target chunk's edge.
                // Keep this loaded-only source work from synchronously generating those neighbours.
                if (!mutationNeighborhoodReady(level, x >> 4, z >> 4)) continue;
                if (replaceSoil) level.setBlock(position, Blocks.COARSE_DIRT.defaultBlockState(), 3);
                else level.removeBlock(position, false);
            }
        });
    }

    private static boolean mutationNeighborhoodReady(ServerLevel level, int chunkX, int chunkZ) {
        var chunks = level.getChunkSource();
        for (int x = chunkX - 1; x <= chunkX + 1; x++)
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++)
                if (chunks.getChunkNow(x, z) == null) return false;
        return true;
    }

    @SubscribeEvent
    public static void decorateMob(FinalizeSpawnEvent event) {
        if (!PollutionConfig.ENABLED.get() || !(event.getEntity().level() instanceof ServerLevel level)) return;
        var entity = event.getEntity();
        if (!(entity instanceof Enemy) || entity.getType().is(GLYPHIDS)) return;
        if (get(level, entity.blockPosition(), PollutionType.SOOT) <= PollutionConfig.BUFF_MOB_THRESHOLD.get()) return;
        var health = entity.getAttribute(Attributes.MAX_HEALTH);
        var damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        var healthId = HbmReloaded.id("pollution/soot_health");
        var damageId = HbmReloaded.id("pollution/soot_attack");
        if (health != null && !health.hasModifier(healthId)) health.addPermanentModifier(new AttributeModifier(healthId, 1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (damage != null && !damage.hasModifier(damageId)) damage.addPermanentModifier(new AttributeModifier(damageId, 1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        entity.heal(entity.getMaxHealth());
    }

    @SubscribeEvent
    public static void stopped(ServerStoppedEvent event) {
        ticks = 0;
    }
}
