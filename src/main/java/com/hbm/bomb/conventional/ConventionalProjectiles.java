package com.hbm.bomb.conventional;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original entity mappings use 1000-block tracking and an update every tick. */
public final class ConventionalProjectiles {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<RubbleProjectile>> RUBBLE=ENTITIES.register("entity_rubble",()->EntityType.Builder.<RubbleProjectile>of(RubbleProjectile::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_rubble"));
    public static final DeferredHolder<EntityType<?>,EntityType<ShrapnelProjectile>> SHRAPNEL=ENTITIES.register("entity_shrapnel",()->EntityType.Builder.<ShrapnelProjectile>of(ShrapnelProjectile::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().noSave().clientTrackingRange(63).updateInterval(1).build("hbm:entity_shrapnel"));
    public static final DeferredHolder<EntityType<?>,EntityType<com.hbm.entity.effect.EntityMist>> MIST=ENTITIES.register("entity_mist",()->EntityType.Builder.<com.hbm.entity.effect.EntityMist>of(com.hbm.entity.effect.EntityMist::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_mist"));
    private ConventionalProjectiles(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);MistExposure.register(bus);bus.addListener(RubbleBurstEffects::register);bus.addListener(FoamSplashEffects::register);}
}
