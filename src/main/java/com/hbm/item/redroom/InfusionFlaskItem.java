package com.hbm.item.redroom;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Original ItemFlask EnumInfusion.SHIELD:32tick drink,+5 shield/max,cap100,no empty bottle. */
public final class InfusionFlaskItem extends Item {
 public InfusionFlaskItem(){super(new Properties().stacksTo(64));}
 @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
 @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.DRINK;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(ShieldInfusion.data(player).maxShield>=ShieldInfusion.CAP)return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
 @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity){if(!(entity instanceof Player player))return stack;if(!player.getAbilities().instabuild)stack.shrink(1);if(!level.isClientSide)ShieldInfusion.drink(player);return stack;}
}
