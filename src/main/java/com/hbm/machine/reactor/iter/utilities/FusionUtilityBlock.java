package com.hbm.machine.reactor.iter.utilities;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Original two source Dummyable shapes; creative Klystron has the actual narrow uppercap, boiler has4 fluid extras. */
public final class FusionUtilityBlock extends DummyableBlock {
 public final boolean creative;private static final int[] TOP={4,-3,4,3,1,1};
 public FusionUtilityBlock(boolean creative){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());this.creative=creative;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return creative?new int[]{3,0,4,3,2,2}:new int[]{3,0,4,4,1,1};}@Override public int offset(){return creative?3:4;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null||!creative)return state;var core=coreForPlacement(context);var bounds=rotate(TOP,placementFacing(state));for(var part:BlockPos.betweenClosed(core.offset(-bounds[4],-bounds[1],-bounds[2]),core.offset(bounds[5],bounds[0],bounds[3])))if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part)||!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());safely(()->{if(creative){var bounds=rotate(TOP,facing);for(var part:BlockPos.betweenClosed(core.offset(-bounds[4],4,-bounds[2]),core.offset(bounds[5],4,bounds[3])))level.setBlock(part,defaultBlockState().setValue(LEGACY_META,Direction.UP.get3DDataValue()),UPDATE_ALL);}else for(var port:FusionBoilerEntity.portConnections(core,facing))setPort(level,port.origin(),true);});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?creative?new CreativeKlystronEntity(pos,state):new FusionBoilerEntity(pos,state):!creative&&isPort(state)?new FusionBoilerPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){if(!isCore(state))return null;return creative?createTickerHelper(type,FusionUtilityContent.CREATIVE_ENTITY.get(),CreativeKlystronEntity::tick):createTickerHelper(type,FusionUtilityContent.BOILER_ENTITY.get(),FusionBoilerEntity::tick);}
}
