package com.hbm.missile.custom;

import com.hbm.entity.missile.CustomMissileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Exact EntityMappings entity_custom_missile identity; tickets remain EntityMissileBaseNT's single controller. */
public final class CustomMissileFlightContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final DeferredHolder<EntityType<?>, EntityType<CustomMissileEntity>> CUSTOM = ENTITIES.register("entity_custom_missile", () -> EntityType.Builder.<CustomMissileEntity>of(CustomMissileEntity::new, MobCategory.MISC)
            .sized(1.5F, 1.5F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_custom_missile"));
    public static void register(IEventBus bus) { ENTITIES.register(bus); }
    private CustomMissileFlightContent() {}
}
