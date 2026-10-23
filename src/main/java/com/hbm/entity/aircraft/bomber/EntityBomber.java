package com.hbm.entity.aircraft.bomber;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.config.GeneralModes;
import com.hbm.entity.aircraft.c130.EntityPlaneBase;
import com.hbm.entity.specialvehicle.EntityBoxcar;
import com.hbm.registry.HbmSounds;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.Heightmap;
/** Actual source bomber with all factory RNG, style, drop windows and source-empty Stinger branch. */
public final class EntityBomber extends EntityPlaneBase {
 private static final EntityDataAccessor<Byte> STYLE=SynchedEntityData.defineId(EntityBomber.class,EntityDataSerializers.BYTE);
 public static Consumer<EntityBomber> CLIENT_TICK=ignored->{},CLIENT_REMOVE=ignored->{};
 public int bombStart=75,bombStop=125,bombRate=3,type;
 public EntityBomber(EntityType<? extends EntityBomber> type,Level level){super(type,level);}
 @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(STYLE,(byte)0);}
 public int style(){return entityData.get(STYLE);}
 private void style(int style){entityData.set(STYLE,(byte)style);}
 @Override public void tick(){
  super.tick();if(level().isClientSide){CLIENT_TICK.accept(this);return;}
  if(health>0&&tickCount>bombStart&&tickCount<bombStop&&tickCount%bombRate==0){
   if(type==3){fizz();BomberClouds.spawn(level(),getX(),getY()-1F,getZ(),10,.5,false);}
   else if(type==5){ /* The source intentionally has no Stinger payload. */ }
   else if(type==6){
    level().playSound(null,getX()+.5F,getY()+.5F,getZ()+.5F,HbmSounds.get("weapon.missileTakeOff"),SoundSource.NEUTRAL,10F,.9F+random.nextFloat()*.2F);
    var rocket=new EntityBoxcar(level());rocket.setPos(getX()+random.nextDouble()-.5,getY()-random.nextDouble(),getZ()+random.nextDouble()-.5);level().addFreshEntity(rocket);
   }else if(type==7){fizz();BomberClouds.spawn(level(),getX(),level().getHeight(Heightmap.Types.WORLD_SURFACE,(int)getX(),(int)getZ())+2,getZ(),10,1,true);}
   else{
    level().playSound(null,getX()+.5F,getY()+.5F,getZ()+.5F,HbmSounds.get("entity.bombWhistle"),SoundSource.NEUTRAL,10F,.9F+random.nextFloat()*.2F);
    var zeta=new EntityBombletZeta(BomberContent.ZETA.get(),level());zeta.rotation();zeta.type=type;
    zeta.setPos(getX()+random.nextDouble()-.5,getY()-random.nextDouble(),getZ()+random.nextDouble()-.5);
    var motion=getDeltaMovement();if(type==0)zeta.setDeltaMovement(motion.x+random.nextGaussian()*.15,0,motion.z+random.nextGaussian()*.15);else zeta.setDeltaMovement(motion.x,0,motion.z);
    level().addFreshEntity(zeta);
   }
  }
 }
 private void fizz(){level().playSound(null,getX()+.5F,getY()+.5F,getZ()+.5F,SoundEvents.FIRE_EXTINGUISH,SoundSource.NEUTRAL,5F,2.6F+(random.nextFloat()-random.nextFloat())*.8F);}
 public void fac(Level world,double x,double y,double z){
  var vector=LegacyVector.createVectorHelper(world.random.nextDouble()-.5,0,world.random.nextDouble()-.5).normalize();
  double speed=GeneralModes.flag("1.14_enableBomberShortMode")?1:2;vector.xCoord*=speed;vector.zCoord*=speed;
  moveTo(x-vector.xCoord*100,y+50,z-vector.zCoord*100,0,0);loadNeighboringChunks((int)(x/16),(int)(z/16));
  setDeltaMovement(vector.xCoord,0,vector.zCoord);rotation();
  int style=switch(world.random.nextInt(7)){case 0,1->1;case 2,3->2;case 4->5;case 5->6;default->7;};
  if(world.random.nextInt(100)==0)style=switch(world.random.nextInt(4)){case 0->0;case 1->3;case 2->4;default->8;};style(style);
 }
 public static EntityBomber create(Level world,int type,double x,double y,double z){
  var bomber=new EntityBomber(BomberContent.BOMBER.get(),world);bomber.timer=200;
  switch(type){
   case 1->{bomber.bombStart=50;bomber.bombStop=100;bomber.bombRate=5;}
   case 2->{bomber.bombStart=50;bomber.bombStop=100;bomber.bombRate=4;}
   case 3,7->{bomber.bombStart=75;bomber.bombStop=125;bomber.bombRate=1;}
   case 4->{bomber.bombStart=60;bomber.bombStop=70;bomber.bombRate=65;}
   case 5,6->{bomber.bombStart=50;bomber.bombStop=150;bomber.bombRate=10;}
   default->{type=0;bomber.bombStart=50;bomber.bombStop=100;bomber.bombRate=2;}
  }
  bomber.fac(world,x,y,z);
  if(type==4){int style=switch(world.random.nextInt(3)){case 0->5;case 1->6;default->7;};if(world.random.nextInt(100)==0)style=8;bomber.style(style);}
  else if(type==5)bomber.style(4);else if(type==6||type==7)bomber.style(6);
  bomber.type=type;return bomber;
 }
 @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);bombStart=tag.getInt("bombStart");bombStop=tag.getInt("bombStop");bombRate=tag.getInt("bombRate");type=tag.getInt("type");style(tag.getByte("style"));}
 @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("bombStart",bombStart);tag.putInt("bombStop",bombStop);tag.putInt("bombRate",bombRate);tag.putInt("type",type);tag.putByte("style",(byte)style());}
 @Override public void remove(RemovalReason reason){if(level().isClientSide)CLIENT_REMOVE.accept(this);super.remove(reason);}
}
