package com.hbm.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The actual Simple radiation diffusion-write fog producer. */
public final class RadiationFogEffects implements RadiationEffects {
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, "hbm");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOG =
            PARTICLES.register("radiation_fog", () -> new SimpleParticleType(false));

    static void register(IEventBus bus) {
        PARTICLES.register(bus);
        HbmRadiation.addEffects(new RadiationFogEffects());
    }

    @Override public void onDiffusionWrite(ServerLevel level, ChunkPos source, ChunkPos target,
                                           float radiation, RadiationSettings settings) {
        var fog = settings.fog();
        if (!(radiation > fog.minimumRadiation()) || level.random.nextInt(fog.chanceDenominator()) != 0) return;
        // Original coordinates use the source chunk, even when this was a write to a neighbor.
        var chunk = level.getChunkSource().getChunkNow(source.x, source.z);
        if (chunk == null) return;
        int x = source.x * 16 + level.random.nextInt(16);
        int z = source.z * 16 + level.random.nextInt(16);
        // Legacy getHeightValue was the first position above positive light opacity.
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x & 15, z & 15);
        var pos = new BlockPos.MutableBlockPos(x, y, z);
        while (y >= level.getMinBuildHeight() && chunk.getBlockState(pos).getLightBlock(chunk, pos) == 0)
            pos.setY(--y);
        y = Math.max(level.getMinBuildHeight(), y + 1) + level.random.nextInt(5);
        // Native particle packets keep all client/render classes off the server.
        // Long-range delivery retains the broad fog's visibility across loaded chunks.
        for (var player : level.players())
            level.sendParticles(player, FOG.get(), true, x, y, z, 1, 0D, 0D, 0D, 0D);
    }

    private RadiationFogEffects() {}
}
