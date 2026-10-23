package com.hbm.machine.fractiontower;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Original bottom-only fluid identifier control; source spacer is a separate passive one-layer block. */
public final class FractionBlock extends DummyableBlock {
 public final boolean spacer;
 public FractionBlock(boolean spacer){super(Properties.of().strength(5,10).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.spacer=spacer;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{spacer?0:2,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?(spacer?new FractionSpacerEntity(pos,state):new FractionEntity(pos,state)):!spacer&&isPort(state)?new FractionPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return !spacer&&isCore(state)?createTickerHelper(type,FractionContent.ENTITY.get(),FractionEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide&&!spacer){var core=findCore(level,pos);if(core!=null)for(var port:FractionEntity.portPositions(core))setPort(level,port,true);}}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
  if(!spacer&&!player.isShiftKeyDown()&&stack.getItem() instanceof FluidIdentifier identifier){if(level.isClientSide)return ItemInteractionResult.SUCCESS;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof FractionEntity tower))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   if(level.getBlockEntity(core.below(3)) instanceof FractionEntity)player.displayClientMessage(Component.translatable("gui.hbm.fraction.bottom_only").withStyle(ChatFormatting.RED),false);
   else {var type=identifier.getType(level,core,stack);tower.tanks[0].setTankType(type);tower.setChanged();player.displayClientMessage(Component.translatable("gui.hbm.fraction.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}return ItemInteractionResult.CONSUME;
  }return super.useItemOn(stack,state,level,pos,player,hand,hit);
 }
}
