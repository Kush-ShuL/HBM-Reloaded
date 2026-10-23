package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.ExplosionVisuals;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
public class ExplosionEffectStandard implements IExplosionSFX {
 @Override public void doEffect(ExplosionVNT explosion,Level world,double x,double y,double z,float size){
  if(world.isClientSide)return;
  world.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,4F,(1F+(world.random.nextFloat()-world.random.nextFloat())*.2F)*.7F);
  ExplosionVisuals.send(world,ExplosionVisuals.STANDARD,x,y,z,size,explosion.compat.getToBlow(),250);
 }
}
