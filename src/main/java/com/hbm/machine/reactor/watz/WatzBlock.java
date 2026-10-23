package com.hbm.machine.reactor.watz;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

/** Original Watz five-rectangle footprint, real chained dummy/proxy state and source component return drops. */
public final class WatzBlock extends DummyableBlock {
    public WatzBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{2,0,3,3,1,1};}@Override public int offset(){return 3;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var facing=context.getHorizontalDirection().getOpposite();var core=coreForPlacement(context);for(var rel:WatzLayout.CELLS){var pos=core.offset(rel);if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;}return defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue());}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());form(level,core,facing);}
    public void form(Level level,BlockPos core){form(level,core,Direction.NORTH);}
    private void form(Level level,BlockPos core,Direction facing){safely(()->{level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);for(var rel:WatzLayout.CELLS){if(rel.equals(BlockPos.ZERO))continue;var away=rel.getY()>0?Direction.UP:rel.getX()<0?Direction.WEST:rel.getX()>0?Direction.EAST:rel.getZ()<0?Direction.NORTH:Direction.SOUTH;level.setBlock(core.offset(rel),defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}for(var connection:WatzEntity.portConnections(core))if(!connection.origin().equals(core))setPort(level,connection.origin(),true);});}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new WatzEntity(pos,state):isPort(state)?new WatzPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,WatzContent.ENTITY.get(),WatzEntity::tick):null;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
    private static void componentDrop(Level level,BlockPos pos,ItemStack stack){level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&isCore(state)&&!level.isClientSide){componentDrop(level,pos,new ItemStack(WatzContent.CASING.get(),48));for(int i=0;i<3;i++){var bolts=HbmMaterials.bySymbol("MAT_DURA").make(AutogenContent.item("bolt"));bolts.setCount(64);componentDrop(level,pos,bolts);}componentDrop(level,pos,new ItemStack(WatzContent.ELEMENT.get(),36));componentDrop(level,pos,new ItemStack(WatzContent.COOLER.get(),26));componentDrop(level,pos,new ItemStack(WatzContent.STRUCT.get()));}super.onRemove(state,level,pos,replacement,moving);}
}
