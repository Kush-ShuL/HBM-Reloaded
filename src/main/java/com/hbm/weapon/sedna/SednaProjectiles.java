package com.hbm.weapon.sedna;

import com.hbm.entity.projectile.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original 0.5-size MK4, 250-block tracking, frequency1, velocity updates disabled. */
public final class SednaProjectiles {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBulletBaseMK4>> BULLET=ENTITIES.register("entity_bullet_mk4",()->EntityType.Builder.<EntityBulletBaseMK4>of(EntityBulletBaseMK4::new,MobCategory.MISC)
            .sized(.5F,.5F).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_bullet_mk4"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBulletBeamBase>> BEAM=ENTITIES.register("entity_beam_mk4",()->EntityType.Builder.<EntityBulletBeamBase>of(EntityBulletBeamBase::new,MobCategory.MISC)
            .sized(.5F,.5F).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_beam_mk4"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityCoin>> COIN=ENTITIES.register("entity_coin",()->EntityType.Builder.<EntityCoin>of(EntityCoin::new,MobCategory.MISC)
            .sized(1,1).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_coin"));
    private SednaProjectiles(){}
    public static void register(IEventBus bus){CatapultBulletConfigs.init();ENTITIES.register(bus);}
}
