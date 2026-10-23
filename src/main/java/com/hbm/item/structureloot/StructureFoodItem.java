package com.hbm.item.structureloot;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Exact ItemLemon branches for Twinkie, definitelyfood and cement, including old creative consumption. */
public class StructureFoodItem extends Item {
    private final String name;private final int nutrition;private final float saturation;private final boolean alwaysEdible;
    public StructureFoodItem(String name,int nutrition,float saturation){this(name,nutrition,saturation,64);}
    protected StructureFoodItem(String name,int nutrition,float saturation,int maximumStackSize){this(name,nutrition,saturation,maximumStackSize,false);}
    protected StructureFoodItem(String name,int nutrition,float saturation,int maximumStackSize,boolean alwaysEdible){super(new Properties().stacksTo(maximumStackSize).food(sourceFood(nutrition,saturation,alwaysEdible)));this.name=name;this.nutrition=nutrition;this.saturation=saturation;this.alwaysEdible=alwaysEdible;}
    private static FoodProperties sourceFood(int nutrition,float saturation,boolean alwaysEdible){var builder=new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation);if(alwaysEdible)builder.alwaysEdible();return builder.build();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity consumer){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.canEat(alwaysEdible))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){if(!(consumer instanceof Player player))return stack;stack.shrink(1);player.getFoodData().eat(nutrition,saturation);level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);onFoodEaten(stack,level,player);player.awardStat(Stats.ITEM_USED.get(this));return stack;}
    /** Default preserves existing food behavior; source subclasses supply only their actual eat callback. */
    protected void onFoodEaten(ItemStack stack,Level level,Player player){}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){if(name.equals("twinkie"))tooltip.add(Component.translatable("item.twinkie.desc"));}
}
