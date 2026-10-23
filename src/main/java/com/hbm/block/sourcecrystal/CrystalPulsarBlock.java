package com.hbm.block.sourcecrystal;

import com.hbm.explosion.ExplosionChaos;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source OR !world.isRemote is intentional: server neighbour events always invoke hardenVirus(10). */
public final class CrystalPulsarBlock extends Block {
    public CrystalPulsarBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(15F, Float.POSITIVE_INFINITY)
                .sound(SoundType.STONE).requiresCorrectToolForDrops());
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) {
        // Pure source predicate ends in OR !isRemote: no random draw or mutation precedes it.
        // Move that always-true server branch before any cold neighbour read; its sole kernel owns FULL preparation.
        if (!level.isClientSide) { ExplosionChaos.hardenVirus(level, pos.getX(), pos.getY(), pos.getZ(), 10); return; }
        boolean adjacent = false;
        for (var offset : CrystalActions.NEIGHBOR_ORDER) {
            var at = pos.offset(offset[0], offset[1], offset[2]);
            // Client cache reads never request server terrain. Server outcome is unconditionally true;
            // its complete original footprint is prepared by the sole harden operation below.
            if (level.hasChunkAt(at) && level.getBlockState(at).is(SourceCrystalsContent.VIRUS.get())) { adjacent = true; break; }
        }
        if (adjacent) ExplosionChaos.hardenVirus(level, pos.getX(), pos.getY(), pos.getZ(), 10);
    }
}
