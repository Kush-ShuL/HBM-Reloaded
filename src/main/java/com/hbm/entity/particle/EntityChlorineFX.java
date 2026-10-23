package com.hbm.entity.particle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** Actual chlorine cloud, not EntityMist or the native client-only same-name smoke particle. */
public final class EntityChlorineFX extends EntitySourceGasCloud {
    public EntityChlorineFX(EntityType<? extends EntityChlorineFX> type,Level level){super(type,level,true);}
    public EntityChlorineFX(Level level){this(LegacyCloudContent.CHLORINE.get(),level);}
    public EntityChlorineFX(Level level,double x,double y,double z,double mx,double my,double mz){this(level,x,y,z,mx,my,mz,1F);}
    public EntityChlorineFX(Level level,double x,double y,double z,double mx,double my,double mz,float scale){super(LegacyCloudContent.CHLORINE.get(),level,x,y,z,mx,my,mz,scale,true);}
}
