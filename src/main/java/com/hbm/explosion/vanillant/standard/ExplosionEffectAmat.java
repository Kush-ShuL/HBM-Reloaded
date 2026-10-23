package com.hbm.explosion.vanillant.standard;
import com.hbm.explosion.ExplosionVisuals;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IExplosionSFX;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
public class ExplosionEffectAmat implements IExplosionSFX {
 @Override public void doEffect(ExplosionVNT explosion,Level world,double x,double y,double z,float size){
  if(size<15)world.playSound(null,x,y,z,SoundEvents.GENERIC_EXPLODE,SoundSource.BLOCKS,4F,(1.4F+(world.random.nextFloat()-world.random.nextFloat())*.2F)*.7F);
  else world.playSound(null,x,y,z,HbmSounds.get("weapon.mukeExplosion"),SoundSource.BLOCKS,15F,1F);
  ExplosionVisuals.send(world,ExplosionVisuals.AMAT,x,y,z,size,List.of(),200);
 }
}
