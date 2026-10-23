package com.hbm.bomb.conventional.grenade;

import com.hbm.explosion.ExplosionDeathEffects;
import com.hbm.exposure.HbmExposure;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.*;

/** HbmLivingProps' four original independent burn counters and EntityEffectHandler.handleTemperature. */
public final class FireStatus {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    private static final DeferredHolder<AttachmentType<?>,AttachmentType<Data>> TYPE=ATTACHMENTS.register("living_special_fire",()->AttachmentType.serializable(Data::new).build());
    private FireStatus(){}
    public static Data data(LivingEntity entity){return entity.getData(TYPE);}
    public static void register(IEventBus bus){ATTACHMENTS.register(bus);NeoForge.EVENT_BUS.addListener(FireStatus::tick);bus.addListener(FlameEffects::register);}
    private static void tick(EntityTickEvent.Post event){if(!(event.getEntity() instanceof LivingEntity entity)||entity.level().isClientSide||!entity.isAlive()||!entity.hasData(TYPE))return;
        var data=data(entity);if(entity.fireImmune()){data.fire=data.phosphorus=0;}if(entity.isInWaterOrRain())data.fire=0;
        if(data.fire>0){data.fire--;sound(entity,15);if((entity.tickCount+entity.getId())%40==0)entity.hurt(entity.damageSources().onFire(),2);particle(entity,0);}
        if(data.phosphorus>0){data.phosphorus--;sound(entity,15);if((entity.tickCount+entity.getId())%40==0)entity.hurt(entity.damageSources().onFire(),5);particle(entity,0);}
        if(data.balefire>0){data.balefire--;sound(entity,15);HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,5);if((entity.tickCount+entity.getId())%20==0)entity.hurt(entity.damageSources().onFire(),5);particle(entity,1);}
        if(data.blackFire>0){data.blackFire--;sound(entity,10);HbmExposure.contaminate(entity,HbmExposure.Contamination.CREATIVE,5);if((entity.tickCount+entity.getId())%10==0)entity.hurt(entity.damageSources().onFire(),10);particle(entity,4);}
        if((data.fire>0||data.phosphorus>0||data.balefire>0||data.blackFire>0)&&!entity.isAlive())ExplosionDeathEffects.decide(entity,entity.damageSources().onFire());
    }
    private static void sound(LivingEntity entity,int period){if((entity.tickCount+entity.getId())%period==0)entity.level().playSound(null,entity.getX(),entity.getY()+entity.getBbHeight()/2,entity.getZ(),SoundEvents.FIRE_EXTINGUISH,SoundSource.NEUTRAL,1,1.5F+entity.getRandom().nextFloat()*.5F);}
    private static void particle(LivingEntity entity,int type){var random=entity.getRandom();FlameEffects.send(entity.level(),entity.getX()-entity.getBbWidth()/2+entity.getBbWidth()*random.nextDouble(),entity.getY()+random.nextDouble()*entity.getBbHeight(),entity.getZ()-entity.getBbWidth()/2+entity.getBbWidth()*random.nextDouble(),type);}
    public static final class Data implements INBTSerializable<CompoundTag>{
        public int fire,phosphorus,balefire,blackFire;
        @Override public CompoundTag serializeNBT(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putInt("hfr_fire",fire);tag.putInt("hfr_phosphorus",phosphorus);tag.putInt("hfr_balefire",balefire);tag.putInt("hfr_blackfire",blackFire);return tag;}
        @Override public void deserializeNBT(HolderLookup.Provider lookup,CompoundTag tag){fire=tag.getInt("hfr_fire");phosphorus=tag.getInt("hfr_phosphorus");balefire=tag.getInt("hfr_balefire");blackFire=tag.getInt("hfr_blackfire");}
    }
}
