package com.hbm.machine.supercomputer;
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
/** Original5boxbody/coreoffset8 and5realcomboports. */
public final class SuperComputerBlock extends DummyableBlock {
 public static final int[][] BOXES={{5,0,3,3,3,3},{6,-6,3,3,1,1},{6,-6,1,1,3,3},{7,-7,1,1,1,1},{2,0,-3,8,1,1}};
 public SuperComputerBlock(){super(Properties.of().strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return BOXES[0].clone();}@Override public int offset(){return 8;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var dir=placementFacing(state);for(int i=1;i<BOXES.length;i++){var d=rotate(BOXES[i],dir);for(var pos:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var dir=placementFacing(state);var core=pos.relative(dir,-offset());safely(()->{for(int i=1;i<BOXES.length;i++){var d=rotate(BOXES[i],dir);for(var location:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3]))){var part=location.immutable();var away=part.getY()>core.getY()?Direction.UP:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}for(var port:SuperComputerEntity.portConnections(core,dir))setPort(level,port.origin(),true);});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SuperComputerEntity(pos,state):isPort(state)?new SuperComputerPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,SuperComputerContent.ENTITY.get(),SuperComputerEntity::tick):null;}
}
