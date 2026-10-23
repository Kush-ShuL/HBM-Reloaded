package com.hbm.item.bottle;

import com.hbm.LegacyStartup;
import com.hbm.bomb.conventional.LargeConventionalEffects;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.exposure.HbmExposure;
import com.hbm.item.medicine.MedicineEffects;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

/** All remaining original ItemEnergy branches: seven capped bottles, coffee and explosive chocolate milk. */
public final class EnergyBottleItem extends Item {
    private final String name,container,cap;
    public EnergyBottleItem(String name,String container,String cap){super(new Properties());this.name=name;this.container=container;this.cap=cap;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+name+".name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.DRINK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity consumer){return 32;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(MedicineEffects.hasPotionSickness(player))return InteractionResultHolder.pass(stack);if(container!=null&&player.getInventory().items.stream().noneMatch(item->item.is(BottleContent.OPENER.get())))return InteractionResultHolder.pass(stack);player.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity consumer){
        if(!(consumer instanceof Player player))return stack;
        if(!player.getAbilities().instabuild)stack.shrink(1);
        if(!level.isClientSide){
            if(player instanceof FakePlayer){level.explode(player,player.getX(),player.getY(),player.getZ(),5,true,Level.ExplosionInteraction.BLOCK);return stack;}
            MedicineEffects.potionSickness(player,5);
            switch(name){
                case "bottle_nuka"->{player.heal(4);effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DIG_SPEED,600,1);radiation(player,5);}
                case "bottle_cherry"->{player.heal(6);effect(player,MobEffects.MOVEMENT_SPEED,600,0);effect(player,MobEffects.JUMP,600,2);radiation(player,5);}
                case "bottle_quantum"->{player.heal(10);effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DAMAGE_RESISTANCE,600,2);effect(player,MobEffects.DAMAGE_BOOST,600,1);radiation(player,15);}
                case "bottle2_korl"->{player.heal(6);effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DIG_SPEED,600,2);effect(player,MobEffects.DAMAGE_BOOST,600,2);}
                case "bottle2_fritz"->{player.heal(6);effect(player,MobEffects.MOVEMENT_SPEED,600,1);effect(player,MobEffects.DAMAGE_RESISTANCE,600,2);effect(player,MobEffects.JUMP,600,2);}
                case "bottle_sparkle"->{player.heal(10);effect(player,MobEffects.MOVEMENT_SPEED,2400,1);effect(player,MobEffects.DAMAGE_RESISTANCE,2400,2);effect(player,MobEffects.DAMAGE_BOOST,2400,2);effect(player,MobEffects.DIG_SPEED,2400,1);radiation(player,5);}
                case "bottle_rad"->{player.heal(10);effect(player,MobEffects.MOVEMENT_SPEED,2400,1);effect(player,MobEffects.DAMAGE_RESISTANCE,2400,2);effect(player,MobEffects.FIRE_RESISTANCE,2400,0);effect(player,MobEffects.DAMAGE_BOOST,2400,4);effect(player,MobEffects.DIG_SPEED,2400,1);radiation(player,15);}
                case "coffee","coffee_radium"->{player.heal(10);effect(player,MobEffects.MOVEMENT_SPEED,1200,2);if(name.equals("coffee_radium")){HbmExposure.incrementRadiation(player,500);awardRadium(player);}}
                case "chocolate_milk"->{double x=player.getX(),y=player.getY()+player.getBbHeight()/2D,z=player.getZ();LargeConventionalEffects.standard(level,x,y,z);new ExplosionVNT(level,x,y,z,50).setBlockAllocator(new BlockAllocatorStandard(32)).setBlockProcessor(new BlockProcessorStandard().setNoDrop()).setEntityProcessor(new EntityProcessorCross(5D)).setPlayerProcessor(new PlayerProcessorStandard()).explode();}
                default->throw new IllegalStateException("Unknown source ItemEnergy branch "+name);
            }
            if(!player.getAbilities().instabuild){if(cap!=null)addMainInventory(player,new ItemStack(HbmContent.item(cap)));if(container!=null){if(stack.isEmpty())return new ItemStack(HbmContent.item(container));addMainInventory(player,new ItemStack(HbmContent.item(container)));}}
            player.inventoryMenu.broadcastChanges();
        }
        return stack;
    }
    private static void effect(Player player,Holder<MobEffect> type,int ticks,int amplifier){player.addEffect(new MobEffectInstance(type,ticks,amplifier));}
    private static void radiation(Player player,float dose){HbmExposure.contaminate(player,HbmExposure.Contamination.RAD_BYPASS,dose);}
    private static void awardRadium(Player player){if(player instanceof ServerPlayer server){var advancement=server.server.getAdvancements().get(ResourceLocation.parse("hbm:radium"));if(advancement!=null)for(String criterion:advancement.value().criteria().keySet())server.getAdvancements().award(advancement,criterion);}}
    private static void addMainInventory(Player player,ItemStack stack){
        // Original ignores a full inventory and does not drop extras or use a modern offhand slot.
        for(var current:player.getInventory().items)if(!current.isEmpty()&&ItemStack.isSameItemSameComponents(current,stack)&&current.getCount()<Math.min(current.getMaxStackSize(),64)){int amount=Math.min(stack.getCount(),Math.min(current.getMaxStackSize(),64)-current.getCount());current.grow(amount);stack.shrink(amount);if(stack.isEmpty())return;}
        for(int i=0;i<36;i++)if(player.getInventory().items.get(i).isEmpty()){player.getInventory().items.set(i,stack.copy());stack.setCount(0);return;}
    }
    @Override public boolean hasCraftingRemainingItem(ItemStack stack){return container!=null;}
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack){return container==null?ItemStack.EMPTY:new ItemStack(HbmContent.item(container));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        if(!name.startsWith("coffee")){String key="tooltip.hbm.energy_bottle."+name+".0";if(LegacyStartup.polaroidId==11&&(name.equals("bottle_sparkle")||name.equals("bottle_rad")))key+=".P11";tooltip.add(Component.translatable(key));if(name.equals("chocolate_milk"))tooltip.add(Component.translatable("tooltip.hbm.energy_bottle.chocolate_milk.1"));}
        if(container!=null)tooltip.add(Component.translatable("tooltip.hbm.energy_bottle.opener"));
    }
}
