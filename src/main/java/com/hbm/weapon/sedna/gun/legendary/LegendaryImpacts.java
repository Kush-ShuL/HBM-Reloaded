package com.hbm.weapon.sedna.gun.legendary;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import java.util.function.BiConsumer;
import net.minecraft.world.phys.HitResult;
/** Bound only to the actual original falling vehicle entities; missing implementations fail closed. */
public final class LegendaryImpacts {
    private static BiConsumer<EntityBulletBaseMK4,HitResult> boxcar,boat;
    public static void install(BiConsumer<EntityBulletBaseMK4,HitResult> boxcarEffect,BiConsumer<EntityBulletBaseMK4,HitResult> boatEffect){boxcar=java.util.Objects.requireNonNull(boxcarEffect);boat=java.util.Objects.requireNonNull(boatEffect);}
    public static void boxcar(EntityBulletBaseMK4 bullet,HitResult hit){if(boxcar==null)throw new IllegalStateException("Original EntityBoxcar is not bound");boxcar.accept(bullet,hit);}
    public static void boat(EntityBulletBaseMK4 bullet,HitResult hit){if(boat==null)throw new IllegalStateException("Original EntityDuchessGambit is not bound");boat.accept(bullet,hit);}
    private LegendaryImpacts(){}
}
