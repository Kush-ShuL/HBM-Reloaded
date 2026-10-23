package com.hbm.explosion;

import com.hbm.LegacyStartup;
import com.hbm.bomb.nuclear.NuclearConfig;
import com.hbm.bomb.nuclear.NuclearExplosionMK5;
import com.hbm.bomb.nuclear.NuclearMukeEffects;
import com.hbm.explosion.ExplosionNT.ExAttrib;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/** Actual source Muke parameter presets, independent blast/kill radii, shrapnel and 21 radiation columns. */
public final class ExplosionNukeSmall {
 private ExplosionNukeSmall(){}
 public static void explode(Level world,double x,double y,double z,MukeParams params){
  if(params.particle!=null){boolean bf=params.particle.equals("muke")&&(LegacyStartup.polaroidId==11||world.random.nextInt(100)==0);NuclearMukeEffects.sendAtOrigin(world,x,y+.5D,z,params.particle,bf,x,y,z);}
  world.playSound(null,x,y,z,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15F,1F);
  if(params.shrapnelCount>0)ExplosionLarge.spawnShrapnels(world,x,y,z,params.shrapnelCount);
  if(params.miniNuke&&!params.safe)new ExplosionNT(world,null,x,y,z,params.blastRadius).addAllAttrib(params.explosionAttribs).overrideResolution(params.resolution).explode();
  if(params.killRadius>0)ExplosionNukeGeneric.dealDamage(world,x,y,z,params.killRadius);
  if(!params.miniNuke&&world instanceof ServerLevel server){var explosion=NuclearExplosionMK5.statFac(world,(int)params.blastRadius,x,y,z);server.getChunk(Mth.floor(x/16D),Mth.floor(z/16D));world.addFreshEntity(explosion);}
  if(params.miniNuke&&world instanceof ServerLevel server){float mod=params.radiationLevel/3F;for(int i=-2;i<=2;i++)for(int j=-2;j<=2;j++)if(Math.abs(i)+Math.abs(j)<4)HbmRadiation.incrementRad(server,Mth.floor(x+i*16),Mth.floor(y),Mth.floor(z+j*16),50/(Math.abs(i)+Math.abs(j)+1)*mod);}
 }
 public static MukeParams PARAMS_SAFE=new MukeParams(){{safe=true;killRadius=45F;radiationLevel=2F;}};
 public static MukeParams PARAMS_TOTS=new MukeParams(){{blastRadius=10F;killRadius=30F;particle="tinytot";shrapnelCount=0;resolution=32;radiationLevel=1F;}};
 public static MukeParams PARAMS_LOW=new MukeParams(){{blastRadius=15F;killRadius=45F;radiationLevel=2F;}};
 public static MukeParams PARAMS_MEDIUM=new MukeParams(){{blastRadius=20F;killRadius=55F;radiationLevel=3F;}};
 public static MukeParams PARAMS_HIGH=new MukeParams(){{miniNuke=false;blastRadius=NuclearConfig.radius("fatman");shrapnelCount=0;}};
 public static class MukeParams {
  public boolean miniNuke=true,safe=false;public float blastRadius,killRadius,radiationLevel=1F;public String particle="muke";public int shrapnelCount=25,resolution=64;
  public ExAttrib[] explosionAttribs={ExAttrib.FIRE,ExAttrib.NOPARTICLE,ExAttrib.NOSOUND,ExAttrib.NODROP,ExAttrib.NOHURT};
 }
}
