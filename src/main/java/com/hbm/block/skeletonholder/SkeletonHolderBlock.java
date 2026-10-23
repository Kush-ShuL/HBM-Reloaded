package com.hbm.block.skeletonholder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Real full-cube rock collision, source5/3/4/2 placement, and whole-stack give/take including creative players. */
public final class SkeletonHolderBlock extends BaseEntityBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public SkeletonHolderBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(2,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor(context.getRotation()*4F/360F+.5D)&3;return defaultBlockState().setValue(META,switch(yaw){case 0->5;case 1->3;case 2->4;default->2;});}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SkeletonHolderEntity(pos,state);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level world,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(player.isShiftKeyDown()||stack.isEmpty()||!(world.getBlockEntity(pos)instanceof SkeletonHolderEntity holder)||!holder.item.isEmpty())return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;if(!world.isClientSide){holder.setItem(stack.copy());player.setItemInHand(hand,ItemStack.EMPTY);}return ItemInteractionResult.sidedSuccess(world.isClientSide);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown()||!player.getMainHandItem().isEmpty()||!(world.getBlockEntity(pos)instanceof SkeletonHolderEntity holder)||holder.item.isEmpty())return InteractionResult.PASS;if(!world.isClientSide){player.setItemInHand(InteractionHand.MAIN_HAND,holder.item.copy());holder.setItem(ItemStack.EMPTY);}return InteractionResult.sidedSuccess(world.isClientSide);}
 @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!world.isClientSide&&world.getBlockEntity(pos)instanceof SkeletonHolderEntity holder&&!holder.item.isEmpty())world.addFreshEntity(new ItemEntity(world,pos.getX()+.5,pos.getY(),pos.getZ()+.5,holder.item.copy()));super.onRemove(state,world,pos,next,moving);}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){int meta=state.getValue(META);if(meta<2||meta>5)return state;return state.setValue(META,rotation.rotate(Direction.from3DDataValue(meta)).get3DDataValue());}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){int meta=state.getValue(META);if(meta<2||meta>5)return state;return state.setValue(META,mirror.mirror(Direction.from3DDataValue(meta)).get3DDataValue());}
}
