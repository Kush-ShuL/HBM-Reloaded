package com.hbm.missile.custom.launcher;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source BlockStruct, backed by its real TileEntityMultiblock matching/building behavior. */
public final class CustomLauncherCoreBlock extends BaseEntityBlock {
    public final boolean large;
    public CustomLauncherCoreBlock(boolean large) { super(Properties.of().mapColor(MapColor.METAL).strength(5, 6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); this.large = large; }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(ignored -> new CustomLauncherCoreBlock(large)); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CustomLauncherCoreEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type, CustomLauncherContent.CORE_ENTITY.get(), CustomLauncherCoreEntity::tick); }
}
