package com.hbm.conveyor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source EntityMappings119; no item, recipe, egg or independent conveyor config. */
public final class MovingPackageContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<MovingPackage>> PACKAGE=ENTITIES.register("entity_c_package",()->EntityType.Builder.<MovingPackage>of(MovingPackage::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_c_package"));
    private MovingPackageContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);}
}
