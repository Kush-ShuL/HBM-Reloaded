package com.hbm.entity.mob.ghost;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class GhostContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGhost>> GHOST=ENTITIES.register("entity_ntm_ghost",()->EntityType.Builder.<EntityGhost>of(EntityGhost::new,MobCategory.MISC).sized(.6F,1.8F).eyeHeight(1.8F*.85F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_ntm_ghost"));
    public static void register(IEventBus bus){ENTITIES.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(GHOST.get(),EntityGhost.attributes().build()));GhostEncounters.register();}
    private GhostContent(){}
}
