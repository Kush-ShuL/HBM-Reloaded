package com.hbm.item.dye;
import net.minecraft.sounds.*;import net.minecraft.stats.Stats;import net.minecraft.world.*;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.entity.player.Player;import net.minecraft.world.food.FoodProperties;import net.minecraft.world.item.*;import net.minecraft.world.level.Level;
/** Original ItemCrayon is always-edible ItemFood(3,false), not a paint tool or vanilla sheep dye. */
public final class CrayonItem extends ChemicalDyeItem {
 public CrayonItem(ChemicalDyeType type){super("crayon",type,new Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(.6F).alwaysEdible().build()));}
 @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}
 @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.canEat(true))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
 @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){if(!(consumer instanceof Player player))return stack;stack.shrink(1);player.getFoodData().eat(3,.6F);level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);player.awardStat(Stats.ITEM_USED.get(this));return stack;}
}
