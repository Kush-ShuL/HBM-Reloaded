package com.hbm.entity.particle;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Source EntityFogFX: retained registered visual entity; no invented radiation emitter or drifting simulation. */
public final class EntityFogFX extends EntityModFX {
    public EntityFogFX(EntityType<? extends EntityFogFX> type,Level level){super(type,level,0,0,0);}
    public EntityFogFX(Level level){this(NuclearFogContent.FOG.get(),level);}
    public EntityFogFX(Level level,double x,double y,double z,double mx,double my,double mz){this(level,x,y,z,mx,my,mz,1F);}
    public EntityFogFX(Level level,double x,double y,double z,double mx,double my,double mz,float scale){
        super(NuclearFogContent.FOG.get(),level,x,y,z,0,0,0);
        setDeltaMovement(getDeltaMovement().scale(.10000000149011612D).add(mx,my,mz));
        particleRed=particleGreen=particleBlue=(float)(Math.random()*.30000001192092896D);
        particleScale*=.75F;particleScale*=scale;smokeParticleScale=particleScale;noPhysics=false;
    }
    @Override public void tick(){
        xo=getX();yo=getY();zo=getZ();
        if(maxAge<400)maxAge=400;
        particleAge++;
        if(particleAge>=maxAge)discard();
        setDeltaMovement(getDeltaMovement().scale(.9599999785423279D));
        if(onGround())setDeltaMovement(getDeltaMovement().multiply(.699999988079071D,1,.699999988079071D));
        // The source override never calls EntityModFX.onUpdate or moveEntity.
    }
}
