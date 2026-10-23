package com.hbm.machine.drain;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class DrainBlock extends DummyableBlock {
 public DrainBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
 @Override public int[] dimensions(){return new int[]{0,0,2,0,0,0};}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new DrainEntity(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,DrainContent.ENTITY.get(),DrainEntity::tick):null;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
  if(player.isShiftKeyDown())return ItemInteractionResult.SUCCESS;
  if(stack.getItem() instanceof FluidIdentifier identifier){
   var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof DrainEntity drain))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   if(!level.isClientSide){var type=identifier.getType(level,core,stack);drain.tank.setTankType(type);drain.setChanged();player.displayClientMessage(Component.translatable("message.hbm.drain.type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}
   return ItemInteractionResult.SUCCESS;
  }
  return super.useItemOn(stack,state,level,pos,player,hand,hit);
 }
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return player.isShiftKeyDown()||level.isClientSide?InteractionResult.SUCCESS:InteractionResult.PASS;}
}
