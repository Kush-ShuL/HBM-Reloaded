package com.hbm.machine.exposurechamber;
import com.hbm.multiblock.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
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
import net.minecraft.world.level.material.MapColor;
/** Source six filled regions, offset2 and five original inventory/power proxies. */
public final class ExposureChamberBlock extends DummyableBlock {
 public ExposureChamberBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,2,2,2,2};}@Override public int offset(){return 2;}
 public static Set<BlockPos> parts(BlockPos core,Direction direction){var result=new LinkedHashSet<BlockPos>();var dir=ForgeDirection.getOrientation(direction.get3DDataValue());var side=dir.getRotation(ForgeDirection.UP).getOpposite();add(result,core,new int[]{4,0,2,2,2,2},direction);add(result,core,new int[]{3,0,0,0,-3,8},direction);add(result,core.above(2),new int[]{0,0,1,-1,-3,6},direction);add(result,core.above(2),new int[]{0,0,-1,1,-3,6},direction);var far=core.offset(side.offsetX*7,0,side.offsetZ*7);add(result,far,new int[]{3,0,1,-1,0,1},direction);add(result,far,new int[]{3,0,-1,1,0,1},direction);return result;}
 private static void add(Set<BlockPos> result,BlockPos origin,int[] region,Direction facing){var d=rotate(region,facing);BlockPos.betweenClosed(origin.offset(-d[4],-d[1],-d[2]),origin.offset(d[5],d[0],d[3])).forEach(p->result.add(p.immutable()));}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;for(var part:parts(coreForPlacement(context),placementFacing(state)))if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part)||!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());var parts=parts(core,facing);safely(()->{if(!core.equals(pos))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);var done=new HashSet<BlockPos>();var queue=new ArrayDeque<BlockPos>();done.add(core);queue.add(core);while(!queue.isEmpty()){var parent=queue.removeFirst();for(var direction:Direction.values()){var child=parent.relative(direction);if(parts.contains(child)&&done.add(child)){level.setBlock(child,defaultBlockState().setValue(LEGACY_META,direction.get3DDataValue()),UPDATE_ALL);queue.addLast(child);}}}if(done.size()!=parts.size())throw new IllegalStateException("Disconnected source exposure chamber geometry");});for(var port:ExposureChamberEntity.portDefinitions(core,facing))setPort(level,port.position(),true);if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)&&level.getBlockEntity(core) instanceof NamedMultiblock named)named.setCustomName(stack.getHoverName());}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ExposureChamberEntity(pos,state):isPort(state)?new ExposureChamberPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ExposureChamberContent.ENTITY.get(),ExposureChamberEntity::tick):null;}
}
