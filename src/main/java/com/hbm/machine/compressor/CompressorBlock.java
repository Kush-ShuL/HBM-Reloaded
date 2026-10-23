package com.hbm.machine.compressor;
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
import net.minecraft.world.level.material.MapColor;
/** Original compressor has three unioned cuboids, compact one. Both use the true final core. */
public final class CompressorBlock extends DummyableBlock {
 public final boolean compact;
 public CompressorBlock(boolean compact){super(Properties.of().mapColor(MapColor.METAL).strength(10,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());this.compact=compact;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new CompressorBlock(compact));}
 @Override public int[] dimensions(){return compact?new int[]{2,0,1,1,3,3}:new int[]{2,0,1,2,1,1};}
 @Override public int offset(){return compact?1:2;}
 private int[][] allDimensions(){return compact?new int[][]{dimensions()}:new int[][]{dimensions(),{3,-3,1,1,1,1},{8,-4,0,0,1,1}};}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null||compact)return state;BlockPos core=coreForPlacement(context);Direction facing=context.getHorizontalDirection().getOpposite();for(int[] dim:allDimensions()){int[] d=rotate(dim,facing);for(var p:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))if(!context.getLevel().isInWorldBounds(p)||!context.getLevel().hasChunkAt(p)||(!p.equals(context.getClickedPos())&&!context.getLevel().getBlockState(p).canBeReplaced()))return null;}return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;Direction facing=placementFacing(state);BlockPos core=pos.relative(facing,-offset());if(!compact)safely(()->{for(int[] dim:allDimensions()){int[] d=rotate(dim,facing);for(var p:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3]))){if(p.equals(core)||level.getBlockState(p).is(this))continue;Direction away=p.getY()<core.getY()?Direction.DOWN:p.getY()>core.getY()?Direction.UP:p.getX()<core.getX()?Direction.WEST:p.getX()>core.getX()?Direction.EAST:p.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(p,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}});for(var port:CompressorBlockEntity.ports(core,facing,compact))setPort(level,port.position(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CompressorBlockEntity(pos,state):isPort(state)?new CompressorPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CompressorContent.ENTITY.get(),CompressorBlockEntity::tick):null;}
}
