package com.hbm.item.muffler;

import com.hbm.multiblock.DummyableBlock;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.registry.HbmSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Original ItemMuffler: decrements its stack only on a previously unmuffled LoadedBase core. */
public final class MufflerItem extends Item {
 public MufflerItem(){super(new Properties());}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.upgrade_muffler.name");}
 @Override public InteractionResult useOn(UseOnContext context){
  var level=context.getLevel();var pos=context.getClickedPos();var block=level.getBlockState(pos).getBlock();
  if(block instanceof DummyableBlock multiblock){var core=multiblock.findCore(level,pos);if(core!=null)pos=core;}
  var entity=level.getBlockEntity(pos);
  if(!(entity instanceof MuffledMachine machine)||machine.isMuffled())return InteractionResult.PASS;
  machine.setMuffled(true);
  var player=context.getPlayer();
  if(player!=null)level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.upgradePlug"),SoundSource.PLAYERS,1,1);
  context.getItemInHand().shrink(1);entity.setChanged();
  if(!level.isClientSide)level.sendBlockUpdated(pos,entity.getBlockState(),entity.getBlockState(),2);
  return InteractionResult.sidedSuccess(level.isClientSide);
 }
}
