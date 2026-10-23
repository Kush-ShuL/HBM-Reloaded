package com.hbm.bomb.nuclear;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
public final class NuclearSoliniumCloud extends NuclearFleijaCloud {
 public NuclearSoliniumCloud(EntityType<? extends NuclearSoliniumCloud> type,Level level){super(type,level);}
 public NuclearSoliniumCloud(Level level,int maxAge){super(NuclearContent.SOLINIUM_CLOUD.get(),level,maxAge);}
 @Override protected boolean solinium(){return true;}
 @Override public int legacyTrackingRange(){return 1000;}
}
