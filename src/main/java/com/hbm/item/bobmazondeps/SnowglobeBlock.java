package com.hbm.item.bobmazondeps;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

/** Snowglobe has no INBTBlockTransformable in X5846; native structure decoding retains raw16 metadata. */
public final class SnowglobeBlock extends TrinketBlock {
 private static final VoxelShape SHAPE=box(4,0,4,12,5,12);
 public SnowglobeBlock(){this(Properties.of().mapColor(MapColor.NONE).strength(0,0).sound(SoundType.STONE).noOcclusion());}
 private SnowglobeBlock(Properties properties){super(properties);}
 @Override protected MapCodec<? extends SnowglobeBlock> codec(){return simpleCodec(SnowglobeBlock::new);}
 @Override protected boolean snowglobe(){return true;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SnowglobeEntity(pos,state);}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(META,(state.getValue(META)+rotation.ordinal()*4)&15);}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.setValue(META,mirror.mirror(state.getValue(META),16));}
 private InteractionResult activate(Level level,BlockPos pos){if(level.isClientSide&&level.getBlockEntity(pos)instanceof SnowglobeEntity tile)BobmazonDependenciesContent.CLIENT_OPEN.accept(tile);return InteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){activate(level,pos);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
