package com.hbm.item.bobmazondeps;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

/** Original full3D ItemMatch, default64 stack and even-nonair noncreative consumption. */
public final class MatchstickItem extends Item {
 private static final java.util.Random ITEM_RANDOM=new java.util.Random();
 public MatchstickItem(){super(new Properties());}
 @Override public Component getName(ItemStack stack){return Component.translatable("item.matchstick.name");}
 @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();if(level.isClientSide)return InteractionResult.SUCCESS;var player=context.getPlayer();var pos=context.getClickedPos().relative(context.getClickedFace());var stack=context.getItemInHand();if(player==null||!player.mayUseItemAt(pos,context.getClickedFace(),stack))return InteractionResult.FAIL;
  if(level.getBlockState(pos).isAir()){level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.FLINTANDSTEEL_USE,SoundSource.BLOCKS,1,ITEM_RANDOM.nextFloat()*.4F+.8F);level.setBlock(pos,Blocks.FIRE.defaultBlockState(),3);}if(!player.getAbilities().instabuild)stack.shrink(1);return InteractionResult.SUCCESS;
 }
}
