package com.hbm.missile.completion;
import net.minecraft.world.entity.Entity;import net.minecraft.world.level.Level;
/** Original calls to vanilla World.create/newExplosion retain strength/fire/terrain flags. */
public final class MissileImpactEffects {public static void vanilla(Level level,Entity source,double x,double y,double z,float strength,boolean fire,boolean terrain){level.explode(source,x,y,z,strength,fire,terrain?Level.ExplosionInteraction.TNT:Level.ExplosionInteraction.NONE);}private MissileImpactEffects(){}}
