package com.hbm.weapon.sedna.gun.rifle50;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual EntityMappings entity_falling_building, range1000 and every-tick velocity updates. */
public final class Rifle50Entities {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<EntityBuilding>> BUILDING=ENTITIES.register("entity_falling_building",()->EntityType.Builder.<EntityBuilding>of(EntityBuilding::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_falling_building"));
 public static void register(IEventBus bus){ENTITIES.register(bus);}
 private Rifle50Entities(){}
}
