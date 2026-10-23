package com.hbm.machine.reactor.rbmk.irradiation;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original vanilla ItemSoup(6): source.6saturation, single-stack,32ticks and actual bowl even in creative. */
public final class IrradiatedSoup extends Item {
    public IrradiatedSoup(){super(new Properties().stacksTo(1).food(new FoodProperties.Builder().nutrition(6).saturationModifier(.6F).build()));}@Override public Component getName(ItemStack stack){return Component.translatable("item.glowing_stew.name");}@Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}@Override public int getUseDuration(ItemStack stack,LivingEntity consumer){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.canEat(false))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){if(!(consumer instanceof Player player))return stack;stack.shrink(1);player.getFoodData().eat(6,.6F);level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);player.awardStat(Stats.ITEM_USED.get(this));return new ItemStack(Items.BOWL);}
}
