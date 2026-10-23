package com.hbm.item.food;

import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Source ItemLemon(6,1F,false)'s pudding branch has lore and ordinary food, no medicine effect. */
public final class PuddingItem extends Item {
    public PuddingItem(){super(new Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(1).build()));}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(!player.canEat(false))return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);return InteractionResultHolder.consume(stack);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){
        if(!(consumer instanceof Player player))return stack;
        stack.shrink(1);player.getFoodData().eat(6,1);
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);
        player.awardStat(Stats.ITEM_USED.get(this));return stack;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(var line:Language.getInstance().getOrDefault("item.pudding.desc").split("\\$"))lines.add(Component.literal(line));}
}
