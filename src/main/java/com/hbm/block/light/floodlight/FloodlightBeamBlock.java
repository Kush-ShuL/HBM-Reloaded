package com.hbm.block.light.floodlight;
import com.hbm.block.light.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
/** Original FloodlightBeam shares the sole real SourceBeamBlock air-space/lighting semantics. */
public final class FloodlightBeamBlock extends SourceBeamBlock {
 public FloodlightBeamBlock(){registerDefaultState(stateDefinition.any().setValue(SpotlightBlock.META,0));}@Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(SpotlightBlock.META);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FloodlightBeamEntity(pos,state);}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,FloodlightContent.BEAM_ENTITY.get(),FloodlightBeamEntity::tick);}
}
