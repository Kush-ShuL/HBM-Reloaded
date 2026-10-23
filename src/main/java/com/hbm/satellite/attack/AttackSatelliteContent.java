package com.hbm.satellite.attack;
import com.hbm.satellite.SatelliteRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Two real source orbital factories and their distinct actual attack entities; no duplicate satellite item. */
public final class AttackSatelliteContent {
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<DeathBlastEntity>> DEATH_BLAST=ENTITIES.register("entity_laser_blast",()->EntityType.Builder.<DeathBlastEntity>of(DeathBlastEntity::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_laser_blast"));
 public static final DeferredHolder<EntityType<?>,EntityType<OrbitalLaserEntity>> ORBITAL_LASER=ENTITIES.register("entity_orbital_laser",()->EntityType.Builder.<OrbitalLaserEntity>of(OrbitalLaserEntity::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_orbital_laser"));
 public static void register(IEventBus bus){SatelliteRegistry.bind(3,SatelliteDeathRay.class,SatelliteDeathRay::new);SatelliteRegistry.bind(9,SatellitePrecisionLaser.class,SatellitePrecisionLaser::new);ENTITIES.register(bus);}
 private AttackSatelliteContent(){}
}
