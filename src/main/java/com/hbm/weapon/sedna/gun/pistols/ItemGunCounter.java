package com.hbm.weapon.sedna.gun.pistols;

import com.hbm.weapon.sedna.gun.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class ItemGunCounter extends ItemGunBaseNT implements IStatTrack {
    public ItemGunCounter(WeaponQuality quality,GunConfig...configs){super(quality,configs);}
    public static int getScore(ItemStack stack){return getValueInt(stack,"stattrack");}
    @Override public Component getName(ItemStack stack){return Component.literal(Integer.toString(getScore(stack)));}
    @Override public void tickUp(ItemStack stack,LivingEntity target){setValueInt(stack,"stattrack",getScore(stack)+1);}
    @EventBusSubscriber(modid="hbm")
    public static final class Events {
        @SubscribeEvent public static void death(LivingDeathEvent event){
            if(event.getEntity().level().isClientSide)return;
            if(event.getSource().getEntity() instanceof LivingEntity attacker){
                var held=attacker.getMainHandItem();
                if(held.getItem() instanceof IStatTrack weapon)weapon.tickUp(held,event.getEntity());
            }
        }
    }
}
