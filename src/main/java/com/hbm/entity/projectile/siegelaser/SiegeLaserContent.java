package com.hbm.entity.projectile.siegelaser;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class SiegeLaserContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntitySiegeLaser>> LASER=ENTITIES.register("entity_ntm_siege_laser",()->EntityType.Builder.<EntitySiegeLaser>of(EntitySiegeLaser::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_ntm_siege_laser"));
    public static void register(IEventBus bus){ENTITIES.register(bus);EntitySiegeLaser.registerLifecycle();}
    private SiegeLaserContent(){}
}
