package com.hbm.item.food;

import com.hbm.entity.effect.EntityVortex;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.EnumMultiItem;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Original ItemConserve, with all 27 real variants and its three special food branches. */
public final class ConserveItem extends EnumMultiItem {
    public ConserveItem(){super("canned_conserve",64,true,ConserveFoodType.names(),IntStream.range(0,ConserveFoodType.values().length).boxed().toList());}
    public ConserveFoodType type(ItemStack stack){return ConserveFoodType.values()[ordinal(stack)];}
    public ItemStack stack(ConserveFoodType type){var stack=new ItemStack(this);stack.set(HbmHazards.LEGACY_METADATA.get(),type.ordinal());return stack;}
    @Override public String getDescriptionId(ItemStack stack){return "item.hbm.canned_"+type(stack).id();}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.canned_"+type(stack).id()+".name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.EAT;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!player.canEat(false))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){
        if(!(consumer instanceof Player player))return stack;var type=type(stack);
        //Source decrements even in creative; capture metadata before modern ItemStack becomes empty.
        stack.shrink(1);player.getFoodData().eat(type.foodLevel,type.saturation);
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.PLAYER_BURP,SoundSource.PLAYERS,.5F,level.random.nextFloat()*.1F+.9F);
        player.getInventory().add(new ItemStack(HbmContent.item("can_key")));
        if(type==ConserveFoodType.BHOLE&&!level.isClientSide){
            var vortex=new EntityVortex(level,.5F).setShrinkRate(.01F);vortex.noBreak();vortex.setPos(player.position());level.addFreshEntity(vortex);
        }else if(type==ConserveFoodType.RECURSION&&level.random.nextInt(10)>0){player.getInventory().add(stack(ConserveFoodType.RECURSION));}
        else if(type==ConserveFoodType.FIST){player.hurt(player.damageSources().magic(),2);}
        return stack;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        String key="item.canned_"+type(stack).id()+".desc",translated=Component.translatable(key).getString();if(!translated.equals(key))for(var line:translated.split("\\$"))lines.add(Component.literal(line));
    }
}
