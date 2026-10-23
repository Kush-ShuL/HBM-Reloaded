package com.hbm.item.recipedependencies;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Actual two ItemLemon instances. Its onFoodEaten override does NOT call ItemFood's potion path. */
public final class GlyphidFoodItem extends Item {
 private final String sourceName;private final int nutrition;private final float saturation;
 public GlyphidFoodItem(boolean grilled){super(new Properties().food(new FoodProperties.Builder().nutrition(grilled?8:3).saturationModifier(grilled?.75F:.5F).build()));sourceName=grilled?"glyphid_meat_grilled":"glyphid_meat";nutrition=grilled?8:3;saturation=grilled?.75F:.5F;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}
 @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}
 @Override public int getUseDuration(ItemStack stack,LivingEntity consumer){return 32;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.canEat(false))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
 @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){if(!(consumer instanceof Player player))return stack;stack.shrink(1);player.getFoodData().eat(nutrition,saturation);level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);player.awardStat(Stats.ITEM_USED.get(this));return stack;}
}
