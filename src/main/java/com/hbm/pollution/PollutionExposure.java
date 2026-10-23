package com.hbm.pollution;

import com.hbm.exposure.HbmExposure;
import com.hbm.exposure.ExposureProtection;
import com.hbm.item.medicine.MedicineEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original EntityEffectHandler.handlePollution and ModEventHandler's broken-block heavy-metal exposure. */
@EventBusSubscriber(modid="hbm")
public final class PollutionExposure {
    @SubscribeEvent public static void sync(PlayerTickEvent.Post event){if(event.getEntity() instanceof ServerPlayer player)PollutionSync.send(player,false);}
    @SubscribeEvent public static void tick(EntityTickEvent.Post event){
        if(!PollutionConfig.ENABLED.get()||!(event.getEntity() instanceof LivingEntity living)||!(living.level() instanceof ServerLevel level)||!living.isAlive()||living.isSpectator()||living.tickCount%60!=0)return;
        var pos=BlockPos.containing(living.getX(),living.getY()+living.getEyeHeight(),living.getZ());
        if(PollutionConfig.POISON.get()&&!HbmExposure.protectedFrom(living,ExposureProtection.Hazard.GAS_BLISTERING)){
            float poison=PollutionSystem.get(level,pos,PollutionType.POISON);
            if(poison>10)living.addEffect(new MobEffectInstance(poison<50?MobEffects.POISON:MobEffects.WITHER,100,poison<25?0:poison<50?1:2));
        }
        if(PollutionConfig.LEAD_POISONING.get()&&!HbmExposure.protectedFrom(living,ExposureProtection.Hazard.PARTICLE_FINE)){
            float metal=PollutionSystem.get(level,pos,PollutionType.HEAVYMETAL);
            if(metal>25)living.addEffect(new MobEffectInstance(MedicineEffects.LEAD,100,metal<50?0:2));
        }
    }
    @SubscribeEvent public static void breakBlock(BlockEvent.BreakEvent event){
        var player=event.getPlayer();if(!PollutionConfig.ENABLED.get()||!PollutionConfig.LEAD_FROM_BLOCKS.get()||!(player.level() instanceof ServerLevel level)||player.isSpectator()||HbmExposure.protectedFrom(player,ExposureProtection.Hazard.PARTICLE_FINE))return;
        float metal=PollutionSystem.get(level,event.getPos(),PollutionType.HEAVYMETAL);
        if(metal>=5)player.addEffect(new MobEffectInstance(MedicineEffects.LEAD,100,metal<10?0:metal<25?1:2));
    }
    private PollutionExposure(){}
}
