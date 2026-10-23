package com.hbm.entity.particle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** Actual corrosive source cloud and taint-to-mutation consumer; independent of vanilla EntityCloudFX smoke. */
public final class EntityCloudFX extends EntitySourceGasCloud {
    public EntityCloudFX(EntityType<? extends EntityCloudFX> type,Level level){super(type,level,false);}
    public EntityCloudFX(Level level){this(LegacyCloudContent.CLOUD.get(),level);}
    public EntityCloudFX(Level level,double x,double y,double z,double mx,double my,double mz){this(level,x,y,z,mx,my,mz,1F);}
    public EntityCloudFX(Level level,double x,double y,double z,double mx,double my,double mz,float scale){super(LegacyCloudContent.CLOUD.get(),level,x,y,z,mx,my,mz,scale,false);}
}
