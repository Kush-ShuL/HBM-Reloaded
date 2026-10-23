package com.hbm.door.sourceseal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual BlockHatch: ordinary full iron cube, infinite hardness/resistance, hidden real ItemBlock. */
public final class SourceSealHatchBlock extends BaseEntityBlock {
    public SourceSealHatchBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SourceSealHatchEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,SourceSealContent.ENTITY.get(),SourceSealHatchEntity::tick);}
}
