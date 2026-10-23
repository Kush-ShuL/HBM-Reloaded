package com.hbm.entity.grenade.waste;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Only the active source entity is registered; the deleted item remains absent. */
public final class WastePearlContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityWastePearl>> WASTE_PEARL=ENTITIES.register("entity_waste_pearl",()->EntityType.Builder.<EntityWastePearl>of(EntityWastePearl::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_waste_pearl"));
    public static void register(IEventBus bus){ENTITIES.register(bus);}
    private WastePearlContent(){}
}
