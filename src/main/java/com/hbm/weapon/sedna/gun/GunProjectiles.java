package com.hbm.weapon.sedna.gun;

import com.hbm.entity.projectile.*;
import com.hbm.weapon.sedna.BulletConfig;
import java.util.EnumMap;
import net.minecraft.world.entity.*;

/** Real MK4/Beam constructors. Chunkloading projectile modules must register their actual constructor before using that type. */
public final class GunProjectiles {
    @FunctionalInterface public interface Factory { Entity create(LivingEntity owner,BulletConfig config,float damage,float spread,double side,double height,double front); }
    private static final EnumMap<BulletConfig.ProjectileType,Factory> FACTORIES=new EnumMap<>(BulletConfig.ProjectileType.class);
    static {FACTORIES.put(BulletConfig.ProjectileType.BULLET,EntityBulletBaseMK4::new);FACTORIES.put(BulletConfig.ProjectileType.BEAM,EntityBulletBeamBase::new);}
    public static void register(BulletConfig.ProjectileType type,Factory factory){FACTORIES.put(type,factory);}
    public static Entity create(LivingEntity owner,BulletConfig config,float damage,float spread,double side,double height,double front){var factory=FACTORIES.get(config.pType);if(factory==null)throw new IllegalStateException("Real projectile module has not been admitted: "+config.pType);return factory.create(owner,config,damage,spread,side,height,front);}
    private GunProjectiles(){}
}
