package com.hbm.entity.particle;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
/** Source EntityModFX fields/physics shared by the actual orange entity; its client quad work is in the native renderer. */
public class EntityModFX extends Entity implements LegacyTrackingRange {
 public int particleTextureIndexX,particleTextureIndexY,particleMaxAge,particleAge,maxAge;public float particleTextureJitterX,particleTextureJitterY,particleScale,particleGravity,particleRed,particleGreen,particleBlue,particleAlpha,smokeParticleScale;public ResourceLocation particleIcon;private float sourceWidth,sourceHeight,sourceYOffset;
 protected EntityModFX(EntityType<?> type,Level level){super(type,level);}
 protected EntityModFX(EntityType<?> type,Level level,double x,double y,double z){super(type,level);particleAlpha=1;size(.2F,.2F);sourceYOffset=.1F;setPos(x,y,z);xo=x;yo=y;zo=z;particleRed=particleGreen=particleBlue=1;particleTextureJitterX=random.nextFloat()*3;particleTextureJitterY=random.nextFloat()*3;particleScale=(random.nextFloat()*.5F+.5F)*2;noCulling=true;}
 protected EntityModFX(EntityType<?> type,Level level,double x,double y,double z,double mx,double my,double mz){this(type,level,x,y,z);mx+=(float)(Math.random()*2-1)*.4F;my+=(float)(Math.random()*2-1)*.4F;mz+=(float)(Math.random()*2-1)*.4F;float speed=(float)(Math.random()+Math.random()+1)*.15F,length=(float)Math.sqrt(mx*mx+my*my+mz*mz);setDeltaMovement(mx/length*speed*.4000000059604645D,my/length*speed*.4000000059604645D+.10000000149011612D,mz/length*speed*.4000000059604645D);}
 private void size(float width,float height){sourceWidth=width;sourceHeight=height;refreshDimensions();}
 @Override public EntityDimensions getDimensions(Pose pose){return sourceWidth>0?EntityDimensions.scalable(sourceWidth,sourceHeight):super.getDimensions(pose);}
 @Override protected AABB makeBoundingBox(){return super.makeBoundingBox().move(0,-sourceYOffset,0);}
 public EntityModFX multiplyVelocity(float factor){var v=getDeltaMovement();setDeltaMovement(v.x*factor,(v.y-.10000000149011612D)*factor+.10000000149011612D,v.z*factor);return this;}
 public EntityModFX multipleParticleScaleBy(float factor){size(.2F*factor,.2F*factor);particleScale*=factor;return this;}
 public void setRBGColorF(float red,float green,float blue){particleRed=red;particleGreen=green;particleBlue=blue;}public void setAlphaF(float alpha){particleAlpha=alpha;}public float getRedColorF(){return particleRed;}public float getGreenColorF(){return particleGreen;}public float getBlueColorF(){return particleBlue;}
 public int getFXLayer(){return 0;}public void setParticleIcon(ResourceLocation icon){if(getFXLayer()!=1&&getFXLayer()!=2)throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");particleIcon=icon;}public void setParticleTextureIndex(int index){if(getFXLayer()!=0)throw new RuntimeException("Invalid call to Particle.setMiscTex");particleTextureIndexX=index%16;particleTextureIndexY=index/16;}public void nextTextureIndexX(){particleTextureIndexX++;}
 @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}@Override protected void defineSynchedData(SynchedEntityData.Builder builder){}@Override public boolean isAttackable(){return false;}@Override public int legacyTrackingRange(){return 1000;}@Override public boolean shouldRenderAtSqrDistance(double distance){return distance<25000;}
 @Override public void tick(){xo=getX();yo=getY();zo=getZ();var v=getDeltaMovement().add(0,-.04D*particleGravity,0);move(MoverType.SELF,v);v=getDeltaMovement().scale(.9800000190734863D);if(onGround())v=v.multiply(.699999988079071D,1,.699999988079071D);setDeltaMovement(v);}
 @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putShort("age",(short)particleAge);}@Override protected void readAdditionalSaveData(CompoundTag tag){particleAge=tag.getShort("age");}
 @Override public String toString(){return getClass().getSimpleName()+", Pos ("+getX()+","+getY()+","+getZ()+"), RGBA ("+particleRed+","+particleGreen+","+particleBlue+","+particleAlpha+"), Age "+particleAge;}
}
