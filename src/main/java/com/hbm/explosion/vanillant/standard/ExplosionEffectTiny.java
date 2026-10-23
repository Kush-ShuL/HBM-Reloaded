package com.hbm.explosion.vanillant.standard;

import com.hbm.bomb.conventional.TinyExplosionEffects;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import net.minecraft.world.level.Level;

/** Original tiny weapon SFX bridge; does not silently substitute the standard VNT particle composition. */
public final class ExplosionEffectTiny implements IExplosionSFX {
    @Override public void doEffect(ExplosionVNT explosion,Level level,double x,double y,double z,float size){if(!level.isClientSide)TinyExplosionEffects.compose(level,x,y,z);}
}
