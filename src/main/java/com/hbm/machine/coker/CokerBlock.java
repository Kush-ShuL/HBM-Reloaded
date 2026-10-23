package com.hbm.machine.coker;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
/** Real MachineCoker tower+XRbase+fourfeet. XR parent anchors are preserved;foot-atY0 must pointUP,not sideways into air. */
public final class CokerBlock extends DummyableBlock {
 public CokerBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends CokerBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{22,0,1,1,1,1};}@Override public int offset(){return 1;}
 public static Set<BlockPos> cells(BlockPos core){var cells=new LinkedHashSet<BlockPos>();for(var p:BlockPos.betweenClosed(core.offset(-1,0,-1),core.offset(1,22,1)))cells.add(p.immutable());for(var p:BlockPos.betweenClosed(core.offset(-2,1,-2),core.offset(2,6,2)))cells.add(p.immutable());for(int x:new int[]{-2,2})for(int z:new int[]{-2,2}){cells.add(core.offset(x,0,z));cells.add(core.offset(x,1,z));}return cells;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;for(var p:cells(coreForPlacement(context)))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
 private void fillXR(Level level,BlockPos anchor,int[] d){for(var p:BlockPos.betweenClosed(anchor.offset(-d[4],-d[1],-d[2]),anchor.offset(d[5],d[0],d[3]))){if(p.equals(anchor))continue;Direction away=p.getY()<anchor.getY()?Direction.DOWN:p.getY()>anchor.getY()?Direction.UP:p.getX()<anchor.getX()?Direction.WEST:p.getX()>anchor.getX()?Direction.EAST:p.getZ()<anchor.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());safely(()->{fillXR(level,core.above(),new int[]{5,0,2,2,2,2});for(int x:new int[]{-2,2})for(int z:new int[]{-2,2})fillXR(level,core.offset(x,1,z),new int[]{0,1,0,0,0,0});});for(var p:CokerEntity.portPositions(core))setPort(level,p,true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CokerEntity(pos,state):isPort(state)?new CokerPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CokerContent.ENTITY.get(),CokerEntity::tick):null;}
}
