package com.hbm.entity.particle;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Only the actual two registered source entities. No fluid/HE/emission blocks are disguised as these types. */
public final class LegacyCloudContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityChlorineFX>> CHLORINE=ENTITIES.register("entity_chlorine_fx",()->EntityType.Builder.<EntityChlorineFX>of(EntityChlorineFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_chlorine_fx"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityCloudFX>> CLOUD=ENTITIES.register("entity_cloud_fx",()->EntityType.Builder.<EntityCloudFX>of(EntityCloudFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_cloud_fx"));
    private LegacyCloudContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);}
}
