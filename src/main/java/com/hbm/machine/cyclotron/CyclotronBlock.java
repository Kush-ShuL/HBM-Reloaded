package com.hbm.machine.cyclotron;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Actual5x5x3 source Dummyable with twelve combo proxies and cosmetic plugs. */
public final class CyclotronBlock extends DummyableBlock {
 public CyclotronBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends CyclotronBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{2,0,2,2,2,2};}@Override public int offset(){return 2;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:CyclotronEntity.ports(core))setPort(level,port,true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CyclotronEntity(pos,state):isPort(state)?new CyclotronPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CyclotronContent.ENTITY.get(),CyclotronEntity::tick):null;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
  if(player.isShiftKeyDown())return super.useItemOn(stack,state,level,pos,player,hand,hit);if(level.isClientSide)return ItemInteractionResult.SUCCESS;
  var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof CyclotronEntity machine))return ItemInteractionResult.FAIL;
  for(int i=0;i<4;i++)if(stack.is(CyclotronEntity.plugItem(i))&&!machine.getPlug(i)){stack.shrink(1);machine.setPlug(i);level.playSound(null,pos,HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1.5F,1);return ItemInteractionResult.CONSUME;}
  if(player instanceof ServerPlayer server)server.openMenu(machine);return ItemInteractionResult.CONSUME;
 }
}
