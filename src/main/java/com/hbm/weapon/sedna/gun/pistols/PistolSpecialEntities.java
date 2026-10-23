package com.hbm.weapon.sedna.gun.pistols;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityTorpedo;
import com.hbm.weapon.specialvehicle.VehicleImpacts;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original Protege's own falling torpedo; Lilmac continues to use the actual shared boxcar. */
public final class PistolSpecialEntities {
    private static final DeferredRegister<EntityType<?>> TYPES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityTorpedo>> TORPEDO=TYPES.register("entity_torpedo",()->EntityType.Builder.<EntityTorpedo>of(EntityTorpedo::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_torpedo"));
    public static void register(IEventBus bus){TYPES.register(bus);}
    public static void boxcar(EntityBulletBaseMK4 bullet,HitResult hit){VehicleImpacts.spawnBoxcar(bullet.level(),hit.getLocation());bullet.discard();}
    public static void torpedo(EntityBulletBaseMK4 bullet,HitResult hit){var entity=new EntityTorpedo(bullet.level());var position=hit.getLocation();entity.setPos(position.x,position.y+50,position.z);bullet.level().addFreshEntity(entity);bullet.discard();}
    private PistolSpecialEntities(){}
}
