package com.hbm.client.bomb.nuclear;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;

/** Source vanillaExt/volcano overrides applied after the shared original SmokeFX constructor. */
public final class LegacyVolcanoSmokeParticle extends FlareSmokeParticle {
    public LegacyVolcanoSmokeParticle(ClientLevel level,double x,double y,double z) {
        super(level,x,y,z);
        // EntitySmokeFX samples its default age before ClientProxy replaces it.
        lifetime=(int)(8D/(Math.random()*.8D+.2D));
        sourceScale=100;
        lifetime=200+level.random.nextInt(50);
        hasPhysics=false;
        yd=2.5+level.random.nextDouble();
        xd=level.random.nextGaussian()*.2;
        zd=level.random.nextGaussian()*.2;
    }
    @Override public AABB getRenderBoundingBox(float partial) {
        return new AABB(x-15,y-15,z-15,x+15,y+15,z+15);
    }
}
