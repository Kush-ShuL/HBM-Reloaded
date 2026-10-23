package com.hbm.machine.soyuzlauncher;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual BlockSoyuzStruct, an ordinary textured core plus its source construction miniature. */
public final class SoyuzStructureBlock extends BaseEntityBlock {
    public SoyuzStructureBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion()); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(properties -> new SoyuzStructureBlock()); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SoyuzStructureEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide ? null : createTickerHelper(type,SoyuzLauncherContent.STRUCTURE_ENTITY.get(),(world,pos,s,core)->core.tick()); }
}
