package com.hbm.block.sourcecrystal;

import com.hbm.config.GeneralModes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual source random-ticking crystal, not a passive material block or a fabricated radiation emitter. */
public final class CrystalVirusBlock extends Block {
    public CrystalVirusBlock() {
        super(Properties.of().mapColor(MapColor.METAL).strength(15F, Float.POSITIVE_INFINITY)
                .sound(SoundType.STONE).requiresCorrectToolForDrops().randomTicks());
    }
    @Override protected MapCodec<? extends Block> codec() { return MapCodec.unit(this); }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { tick(state, level, pos, random); }
    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // The original updateTick ignores its Random parameter completely.
        if (GeneralModes.flag("virusCrystalSpreadingEnabled")) CrystalActions.virus(level, pos, true);
    }
    @Override protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos from, boolean moving) {
        // This independent source hardening branch is deliberately NOT gated by spread configuration.
        if (level instanceof ServerLevel server) CrystalActions.virus(server, pos, false);
    }
}
