package com.hbm.railway.cart;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The source registered development entity has no item, egg, recipe or natural producer. */
public final class SourceCartBaseContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<SourceMinecartTest>> TEST=ENTITIES.register("entity_minecart_test",()->EntityType.Builder.<SourceMinecartTest>of(SourceMinecartTest::new,MobCategory.MISC).sized(.98F,.7F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_minecart_test"));
    public static void register(IEventBus bus){ENTITIES.register(bus);}
    private SourceCartBaseContent(){}
}
