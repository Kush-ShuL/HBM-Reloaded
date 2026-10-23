package com.hbm.machine.reactor.iter.mhdt;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Six exact source body boxes and separately shifted front connector base, with original parent-chain priorities. */
public final class MhdtBlock extends DummyableBlock {
 public static final int[][] BODY={{2,0,6,7,2,2},{3,-2,6,2,1,1},{3,-2,-6,7,1,1},{3,-2,-3,5,2,2},{4,-3,-3,5,1,1},{1,0,0,1,3,3,3,0,0}};
 public record Volume(BlockPos center,int[] bounds){}
 public MhdtBlock(){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return BODY[0];}@Override public int offset(){return 7;}
 public static List<Volume> volumes(BlockPos core,Direction dir){var result=new ArrayList<Volume>();for(var source:BODY){var center=source.length==9?core.relative(dir,source[6]).above(source[7]).relative(dir.getClockWise(),source[8]):core;result.add(new Volume(center,rotate(Arrays.copyOf(source,6),dir)));}return result;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);for(var volume:volumes(core,placementFacing(state))){var d=volume.bounds();for(var part:BlockPos.betweenClosed(volume.center().offset(-d[4],-d[1],-d[2]),volume.center().offset(d[5],d[0],d[3])))if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part)||!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;}return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state);var core=pos.relative(dir,-offset());safely(()->{var volumes=volumes(core,dir);for(int i=1;i<volumes.size();i++){var volume=volumes.get(i);var center=volume.center();var d=volume.bounds();for(var mutable:BlockPos.betweenClosed(center.offset(-d[4],-d[1],-d[2]),center.offset(d[5],d[0],d[3]))){var part=mutable.immutable();if(part.equals(center))continue;var away=part.getY()<center.getY()?Direction.DOWN:part.getY()>center.getY()?Direction.UP:part.getX()<center.getX()?Direction.WEST:part.getX()>center.getX()?Direction.EAST:part.getZ()<center.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}for(var connection:MhdtEntity.portConnections(core,dir))setPort(level,connection.origin(),true);});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new MhdtEntity(pos,state):isPort(state)?new MhdtPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,MhdtContent.ENTITY.get(),MhdtEntity::tick):null;}
}
