package com.hbm.entity.aircraft.c130;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.registries.*;

/** Original EntityMappings: 1000-block range, one-tick updates and velocity synchronization. */
public final class C130Content {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final DeferredHolder<EntityType<?>, EntityType<EntityC130>> C130 = ENTITIES.register("entity_c130", () ->
            EntityType.Builder.<EntityC130>of(EntityC130::new, MobCategory.MISC).sized(8, 4).clientTrackingRange(63)
                    .updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_c130"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityParachuteCrate>> PARACHUTE_CRATE = ENTITIES.register("entity_parachute_crate", () ->
            EntityType.Builder.<EntityParachuteCrate>of(EntityParachuteCrate::new, MobCategory.MISC).sized(.6F, 1.8F).fireImmune()
                    .clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_parachute_crate"));
    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        bus.addListener((RegisterTicketControllersEvent event) -> event.register(EntityPlaneBase.TICKETS));
    }
    /** Exact source Flaregun spawnPlane consumer; its owner-only beep belongs to the projectile. */
    public static EntityC130 spawnAtSignal(ServerLevel level, int x, int y, int z, EntityC130.C130PayloadType payload) {
        var plane = new EntityC130(C130.get(), level);
        plane.fac(level, x, y, z, payload);
        level.addFreshEntity(plane);
        plane.trackingRange = 250;
        return plane;
    }
    private C130Content() {}
}
