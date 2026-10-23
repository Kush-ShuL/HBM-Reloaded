package com.hbm.entity.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The four original entity mappings, with their original 250-block tracking and per-tick updates. */
public final class BlackHoleContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBlackHole>> BLACK_HOLE=ENTITIES.register("entity_black_hole",()->EntityType.Builder.<EntityBlackHole>of(EntityBlackHole::new,MobCategory.MISC).sized(.6F,1.8F).fireImmune().clientTrackingRange(16).updateInterval(1).build("hbm:entity_black_hole"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityVortex>> VORTEX=ENTITIES.register("entity_vortex",()->EntityType.Builder.<EntityVortex>of(EntityVortex::new,MobCategory.MISC).sized(.6F,1.8F).fireImmune().clientTrackingRange(16).updateInterval(1).build("hbm:entity_vortex"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityRagingVortex>> RAGING_VORTEX=ENTITIES.register("entity_raging_vortex",()->EntityType.Builder.<EntityRagingVortex>of(EntityRagingVortex::new,MobCategory.MISC).sized(.6F,1.8F).fireImmune().clientTrackingRange(16).updateInterval(1).build("hbm:entity_raging_vortex"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityQuasar>> QUASAR=ENTITIES.register("entity_digamma_quasar",()->EntityType.Builder.<EntityQuasar>of(EntityQuasar::new,MobCategory.MISC).sized(.6F,1.8F).fireImmune().clientTrackingRange(16).updateInterval(1).build("hbm:entity_digamma_quasar"));
    public static void register(IEventBus bus){ENTITIES.register(bus);}
    private BlackHoleContent(){}
}
