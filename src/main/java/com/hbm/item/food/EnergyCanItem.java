package com.hbm.item.food;

import com.hbm.item.medicine.MedicineEffects;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

/** The exact source ItemEnergy.makeCan consumption path and all nine real can effects. */
public final class EnergyCanItem extends Item {
    private final String name;
    public EnergyCanItem(String name){super(new Properties());this.name=name;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.DRINK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);if(MedicineEffects.hasPotionSickness(player))return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);return InteractionResultHolder.consume(stack);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){
        if(!(consumer instanceof Player player))return stack;
        if(!player.getAbilities().instabuild)stack.shrink(1);
        if(!level.isClientSide){
            if(player instanceof FakePlayer){level.explode(player,player.getX(),player.getY(),player.getZ(),5,true,Level.ExplosionInteraction.BLOCK);return stack;}
            MedicineEffects.potionSickness(player,5);
            switch(name){
                case "can_smart","can_overcharge"->{effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DAMAGE_RESISTANCE,600,2);effect(player,MobEffects.DAMAGE_BOOST,600,0);}
                case "can_creature"->{effect(player,MobEffects.MOVEMENT_SPEED,600,0);effect(player,MobEffects.DAMAGE_RESISTANCE,600,2);effect(player,MobEffects.REGENERATION,600,1);}
                case "can_redbomb"->{effect(player,MobEffects.MOVEMENT_SPEED,600,0);effect(player,MobEffects.ABSORPTION,600,2);effect(player,MobEffects.JUMP,600,1);}
                case "can_mrsugar"->{effect(player,MobEffects.MOVEMENT_SPEED,600,0);effect(player,MobEffects.DIG_SPEED,600,1);effect(player,MobEffects.JUMP,600,2);}
                case "can_luna"->{effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DAMAGE_RESISTANCE,600,2);effect(player,MobEffects.DAMAGE_BOOST,600,1);effect(player,MobEffects.REGENERATION,600,2);}
                case "can_bepis"->{effect(player,MobEffects.MOVEMENT_SPEED,600,3);effect(player,MobEffects.DAMAGE_RESISTANCE,600,3);}
                case "can_breen"->effect(player,MobEffects.CONFUSION,600,0);
                case "can_mug"->{effect(player,MobEffects.DAMAGE_RESISTANCE,3600,2);effect(player,MobEffects.REGENERATION,1200,2);}
                default->throw new IllegalStateException("Unmapped source energy can "+name);
            }
            if(!player.getAbilities().instabuild){
                //Original deliberately ignores insertion failures, rather than dropping extras.
                player.getInventory().add(new ItemStack(HbmContent.item("ring_pull")));
                if(stack.isEmpty())return new ItemStack(HbmContent.item("can_empty"));
                player.getInventory().add(new ItemStack(HbmContent.item("can_empty")));
            }
            player.inventoryMenu.broadcastChanges();
        }
        return stack;
    }
    private static void effect(Player player,Holder<MobEffect> type,int ticks,int amplifier){player.addEffect(new MobEffectInstance(type,ticks,amplifier));}
    @Override public boolean hasCraftingRemainingItem(ItemStack stack){return true;}
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return new ItemStack(HbmContent.item("can_empty"));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        if(name.equals("can_mug"))return;
        lines.add(Component.translatable("tooltip.hbm.energy_can."+name+".0"));
        if(name.equals("can_breen"))lines.add(Component.translatable("tooltip.hbm.energy_can."+name+".1"));
    }
}
