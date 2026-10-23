package com.hbm.machine.reactor.icf;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source MachineICF three real cuboids; the source construction core consumes407 components once complete. */
public final class IcfReactorBlock extends DummyableBlock{
 public IcfReactorBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new IcfReactorBlock());}@Override public int[] dimensions(){return new int[]{5,0,1,1,8,8};}@Override public int offset(){return 1;}
 private int[][] cuboids(){return new int[][]{{5,0,1,1,8,8},{4,-2,-1,2,8,8},{4,-2,2,-1,8,8}};}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);var facing=context.getHorizontalDirection().getOpposite();for(var box:cuboids()){int[] d=rotate(box,facing);for(var pos:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}return state;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide)fill(level,pos.relative(placementFacing(state),-offset()),placementFacing(state));}
 public void assembleAt(Level level,BlockPos core,Direction facing){safely(()->{level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);fill(level,core,facing);});}
 private void fill(Level level,BlockPos core,Direction facing){safely(()->{for(var box:cuboids()){int[] d=rotate(box,facing);for(var at:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3]))){var pos=at.immutable();if(pos.equals(core)||level.getBlockState(pos).is(this))continue;Direction away=pos.getY()<core.getY()?Direction.DOWN:pos.getY()>core.getY()?Direction.UP:pos.getX()<core.getX()?Direction.WEST:pos.getX()>core.getX()?Direction.EAST:pos.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(pos,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}}});for(var connection:IcfReactorEntity.connections(core,facing))setPort(level,connection.origin(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new IcfReactorEntity(pos,state):isPort(state)?new IcfReactorPort(pos,state):null;}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,IcfReactorContent.REACTOR_ENTITY.get(),IcfReactorEntity::tick):null;}
}
