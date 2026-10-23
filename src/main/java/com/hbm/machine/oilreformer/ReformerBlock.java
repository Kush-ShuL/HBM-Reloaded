package com.hbm.machine.oilreformer;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** Actual3cuboids including elevated XR lobes; six real fluid+HE proxies,stored-tank block-item drop. */
public final class ReformerBlock extends DummyableBlock {
 public static final int[][] ALL_DIMENSIONS={{2,0,1,1,2,2},{3,-3,1,0,-1,2},{6,-3,1,1,2,0}};
 public ReformerBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends ReformerBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return ALL_DIMENSIONS[0].clone();}@Override public int offset(){return 1;}
 public static Set<BlockPos> cells(BlockPos core,Direction face){var cells=new LinkedHashSet<BlockPos>();for(var dimensions:ALL_DIMENSIONS){var d=rotate(dimensions,face);for(var p:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))cells.add(p.immutable());}return cells;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;for(var p:cells(coreForPlacement(context),context.getHorizontalDirection().getOpposite()))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced())return null;return state;}
 private void fillXR(Level level,BlockPos core,int[] dimensions,Direction face){var d=rotate(dimensions,face);for(var p:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3]))){if(p.equals(core))continue;Direction away=p.getY()<core.getY()?Direction.DOWN:p.getY()>core.getY()?Direction.UP:p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var face=placementFacing(state);var core=pos.relative(face,-offset());safely(()->{fillXR(level,core,ALL_DIMENSIONS[1],face);fillXR(level,core,ALL_DIMENSIONS[2],face);});for(var port:ReformerEntity.portPositions(core,face))setPort(level,port,true);if(level.getBlockEntity(core) instanceof ReformerEntity machine)machine.restoreItem(stack);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ReformerEntity(pos,state):isPort(state)?new ReformerPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ReformerContent.ENTITY.get(),ReformerEntity::tick):null;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){if(context.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof ReformerEntity machine)return List.of(machine.dropStack());var origin=context.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var pos=findCore(context.getLevel(),BlockPos.containing(origin));if(pos!=null&&context.getLevel().getBlockEntity(pos) instanceof ReformerEntity machine)return List.of(machine.dropStack());}return List.of();}
}
