package com.hbm.machine.annihilator;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original9x3x3 body and7-high chimney union; offset4 placement is final before first rendering. */
public final class AnnihilatorBlock extends DummyableBlock {
 private static final MapCodec<AnnihilatorBlock> CODEC=simpleCodec(ignored->new AnnihilatorBlock());public AnnihilatorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{2,0,4,4,1,1};}@Override public int offset(){return 4;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var chimney=core.relative(placementFacing(state),-3);for(var p:BlockPos.betweenClosed(chimney.offset(-1,2,-1),chimney.offset(1,8,1)))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state);var core=pos.relative(dir,-4);var chimney=core.relative(dir,-3);safely(()->{for(var p:BlockPos.betweenClosed(chimney.offset(-1,3,-1),chimney.offset(1,8,1)))level.setBlock(p,defaultBlockState().setValue(LEGACY_META,Direction.UP.get3DDataValue()),3);});for(var p:AnnihilatorEntity.portPositions(core,dir))setPort(level,p,true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new AnnihilatorEntity(pos,state):isPort(state)?new AnnihilatorPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,AnnihilatorContent.ENTITY.get(),AnnihilatorEntity::tick):null;}
}
