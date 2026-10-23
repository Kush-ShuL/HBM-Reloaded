package com.hbm.entity.particle;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual EntityMappings104 visual registration; no item, spawn egg or source producer exists. */
public final class NuclearFogContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityFogFX>> FOG=ENTITIES.register("entity_nuclear_fog",()->EntityType.Builder.<EntityFogFX>of(EntityFogFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_nuclear_fog"));
    private NuclearFogContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);}
}
