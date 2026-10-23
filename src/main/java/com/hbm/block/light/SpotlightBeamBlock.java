package com.hbm.block.light;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
/** Real SpotlightBeam incoming-direction union, cutoff and repropagation callbacks. */
public final class SpotlightBeamBlock extends SourceBeamBlock {
 public SpotlightBeamBlock(){registerDefaultState(stateDefinition.any().setValue(SpotlightBlock.META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(SpotlightBlock.META);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SpotlightBeamEntity(pos,state);}
 public static List<Direction> directions(Level level,BlockPos pos){if(!(level.getBlockEntity(pos)instanceof SpotlightBeamEntity entity))return List.of();var result=new ArrayList<Direction>(6);for(var dir:Direction.values())if((entity.metadata&(1<<dir.get3DDataValue()))!=0)result.add(dir);return result;}
 public static int setDirection(Level level,BlockPos pos,Direction dir,boolean value){if(!(level.getBlockEntity(pos)instanceof SpotlightBeamEntity entity))return 0;entity.metadata=value?entity.metadata|1<<dir.get3DDataValue():entity.metadata&~(1<<dir.get3DDataValue());entity.setChanged();return entity.metadata;}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!level.isClientSide&&!state.is(next.getBlock()))for(var dir:directions(level,pos))SpotlightBlock.unpropagate(level,pos,dir);super.onRemove(state,level,pos,next,moved);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(level.isClientSide||block instanceof SpotlightBeamBlock)return;for(var dir:directions(level,pos))SpotlightBlock.backPropagate(level,pos,dir,state.getValue(SpotlightBlock.META));}
}
