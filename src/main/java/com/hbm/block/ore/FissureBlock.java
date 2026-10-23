package com.hbm.block.ore;

import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.registry.HbmOres;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Original bedrock fissure with a real infinite lava provider and two original render passes. */
public final class FissureBlock extends LegacyOreBlock implements EntityBlock {
    private final MapCodec<FissureBlock> fissureCodec;
    public FissureBlock(OreSpec spec) { super(spec); fissureCodec = simpleCodec(ignored -> new FissureBlock(spec)); }
    @Override protected MapCodec<? extends LegacyOreBlock> codec() { return fissureCodec; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FissureBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != HbmOres.FISSURE_ENTITY.get()) return null;
        return (world, pos, block, entity) -> FissureBlockEntity.tick((ServerLevel) world, (FissureBlockEntity) entity);
    }
    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos above = pos.above();
        if (LegacyMaterials.of(level.getBlockState(above)).isReplaceable()) level.setBlock(above,
                HbmWorldFluids.block(state.getValue(LEGACY_META) != 0 ? "rad_lava_block" : "volcanic_lava_block").defaultBlockState(), 3);
    }
}
