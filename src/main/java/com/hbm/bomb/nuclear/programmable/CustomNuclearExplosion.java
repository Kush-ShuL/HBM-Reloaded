package com.hbm.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.*;
import com.hbm.explosion.ExplosionLarge;
import net.minecraft.sounds.*;
import net.minecraft.world.level.Level;

/** NukeCustom.explodeCustom: exact source precedence, caps, float yields, fallout and nested coordinate offsets. */
public final class CustomNuclearExplosion {
 public static void explode(Level level,double x,double y,double z,CustomBombPayload payload){if(level.isClientSide)return;float tnt=payload.tnt(),nuke=payload.nuke(),hydro=payload.hydro(),amat=payload.amat(),dirty=Math.min(payload.dirty(),100),schrab=payload.schrab(),euph=payload.euph();
  if(euph>0){var ex=new NuclearExplosionMK3(NuclearContent.MK3.get(),level);ex.setPos(x,y,z);ex.destructionRange=150;ex.speed=NuclearConfig.BLAST_SPEED.get();ex.coefficient=1;ex.waste=false;level.addFreshEntity(ex);level.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,100000F,1);var cloud=new NuclearRainbowCloud(level,50);cloud.setPos(x,y,z);level.addFreshEntity(cloud);}
  else if(schrab>0){schrab+=amat/2+hydro/4+nuke/8+tnt/16;schrab=Math.min(schrab,CustomBombPayload.MAX_SCHRAB);var ex=NuclearExplosionMK3.statFacFleija(level,x+.5,y+.5,z+.5,(int)schrab);if(!ex.isRemoved()){level.addFreshEntity(ex);var cloud=new NuclearFleijaCloud(NuclearContent.FLEIJA_CLOUD.get(),level,(int)schrab);cloud.setPos(x+.5,y+.5,z+.5);level.addFreshEntity(cloud);}}
  else if(amat>0){amat+=hydro/2+nuke/4+tnt/8;amat=Math.min(amat,CustomBombPayload.MAX_AMAT);var ex=new NuclearBalefireExplosion(NuclearSpecialContent.BALEFIRE_EXPLOSION.get(),level);ex.setPos(x+.5,y+.5,z+.5);ex.destructionRange=(int)amat;level.addFreshEntity(ex);NuclearTorex.statFacBale(level,x+.5,y+5,z+.5,amat);}
  else if(hydro>0){hydro+=nuke/2+tnt/4;hydro=Math.min(hydro,CustomBombPayload.MAX_HYDRO);dirty*=.25F;level.addFreshEntity(NuclearExplosionMK5.statFac(level,(int)hydro,x+.5,y+.5,z+.5).moreFallout((int)dirty));NuclearTorex.statFacStandard(level,x+.5,y+5,z+.5,hydro);}
  else if(nuke>0){nuke+=tnt/2;nuke=Math.min(nuke,CustomBombPayload.MAX_NUKE);level.addFreshEntity(NuclearExplosionMK5.statFac(level,(int)nuke,x+.5,y+5,z+.5).moreFallout((int)dirty));NuclearTorex.statFacStandard(level,x+.5,y+5,z+.5,nuke);}
  else if(tnt>=75){tnt=Math.min(tnt,CustomBombPayload.MAX_TNT);level.addFreshEntity(NuclearExplosionMK5.statFacNoRad(level,(int)tnt,x+.5,y+.5,z+.5));NuclearTorex.statFacStandard(level,x+.5,y+5,z+.5,tnt);}
  else if(tnt>0)ExplosionLarge.explode(level,x+.5,y+.5,z+.5,tnt,true,true,true);
 }
 private CustomNuclearExplosion(){}
}
