package com.hbm.entity.projectile.legacy;

import com.hbm.entity.projectile.EntityBullet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyBulletContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBullet>> BULLET=ENTITIES.register("entity_bullet",()->EntityType.Builder.<EntityBullet>of(EntityBullet::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_bullet"));
    public static void register(IEventBus bus){ENTITIES.register(bus);}
    private LegacyBulletContent(){}
}
