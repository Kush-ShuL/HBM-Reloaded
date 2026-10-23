package com.hbm.world.meteor;

import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Original two-use remote; never replaces the native natural strike scheduler. */
public final class MeteorRemoteItem extends Item {
 public MeteorRemoteItem(){super(new Properties().durability(2).setNoRepair());}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.meteor_remote.name");}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.meteor_remote.use"));}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
  var stack=player.getItemInHand(hand);stack.hurtAndBreak(1,player,LivingEntity.getSlotForHand(hand));
  if(!level.isClientSide){MeteorScheduler.spawnAt(player,false);player.displayClientMessage(Component.translatable("message.hbm.meteor_remote.watch_head"),false);}
  level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBleep"),SoundSource.PLAYERS,1,1);player.swing(hand);
  return InteractionResultHolder.success(stack);
 }
}
