package com.hbm.machine.reactor.iter.torus;
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
/** Actual source authored circular15×15×5body/offset7; originallayoutair cells stay untouched. */
public final class TorusBlock extends DummyableBlock {
    public TorusBlock(){super(Properties.of().strength(5,36).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,7,7,7,7};}@Override public int offset(){return 7;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var core=coreForPlacement(context);for(var part:TorusLayout.PARTS){var pos=part.at(core);if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}return defaultBlockState().setValue(LEGACY_META,context.getHorizontalDirection().getOpposite().get3DDataValue());}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){if(!level.isClientSide){var facing=placementFacing(state);form(level,pos.relative(facing,-offset()),facing);}}
    public void form(Level level,BlockPos core,Direction facing){safely(()->{level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);for(var part:TorusLayout.PARTS){if(part.x()==0&&part.y()==0&&part.z()==0)continue;var away=part.y()>0?Direction.UP:part.x()<0?Direction.WEST:part.x()>0?Direction.EAST:part.z()<0?Direction.NORTH:Direction.SOUTH;level.setBlock(part.at(core),defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}for(var connection:TorusEntity.portConnections(core))if(!connection.origin().equals(core))setPort(level,connection.origin(),true);});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new TorusEntity(pos,state):isPort(state)?new TorusPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,TorusContent.ENTITY.get(),TorusEntity::tick):null;}
}
