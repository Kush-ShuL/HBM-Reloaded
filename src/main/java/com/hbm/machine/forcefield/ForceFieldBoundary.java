package com.hbm.machine.forcefield;
import com.hbm.registry.HbmSounds;import java.util.*;import java.util.function.IntConsumer;import net.minecraft.core.BlockPos;import net.minecraft.world.entity.Entity;import net.minecraft.world.entity.player.Player;import net.minecraft.world.level.Level;import net.minecraft.world.phys.*;
/** Exact source inside/outside crossing execution, shared with original unregistered BlockFF prototype. */
public final class ForceFieldBoundary {
 private final List<Entity> outside=new ArrayList<>(),inside=new ArrayList<>();
 public void clear(){outside.clear();inside.clear();}
 public void prototype(Level level,BlockPos pos){update(level,pos,10,false,damage->{});}
 public void update(Level level,BlockPos pos,float radius,boolean muffled,IntConsumer damage){
  var oldOutside=new ArrayList<>(outside);var oldInside=new ArrayList<>(inside);clear();double x=pos.getX()+.5,y=pos.getY()+.5,z=pos.getZ()+.5,range=radius+25;
  for(Entity entity:level.getEntities((Entity)null,new AABB(x-range,y-range,z-range,x+range,y+range,z+range))){
   if(entity instanceof Player)continue;boolean out=Math.sqrt(Math.pow(x-entity.getX(),2)+Math.pow(y-entity.getY(),2)+Math.pow(z-entity.getZ(),2))>radius;
   if(!oldOutside.contains(entity)&&!oldInside.contains(entity)){(out?outside:inside).add(entity);continue;}
   boolean crossedIn=oldOutside.contains(entity)&&!out,crossedOut=oldInside.contains(entity)&&out;
   if(crossedIn||crossedOut){
    Vec3 direction=normalize(new Vec3(x-entity.getX(),y-entity.getY(),z-entity.getZ()));float positionRadius=crossedIn?radius+1:radius-1;
    entity.moveTo(x-direction.x*positionRadius,y-direction.y*positionRadius,z-direction.z*positionRadius,0,0);
    double speed=entity.getDeltaMovement().length();Vec3 reflected=direction.scale(crossedIn?-speed:speed);entity.setDeltaMovement(reflected);
    // Original directly changes pos fields after setLocationAndAngles, retaining that method's bounding box.
    entity.setPosRaw(entity.getX()-reflected.x,entity.getY()-reflected.y,entity.getZ()-reflected.z);
    if(crossedOut||!muffled){if(level.isClientSide)level.playLocalSound(entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("weapon.sparkShoot"),entity.getSoundSource(),2.5F,1,false);else level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("weapon.sparkShoot"),entity.getSoundSource(),2.5F,1);}
    (crossedIn?outside:inside).add(entity);if(!level.isClientSide)damage.accept(impact(entity));
   }else (out?outside:inside).add(entity);
  }
 }
 private static double length(Vec3 vector){return(float)Math.sqrt(vector.x*vector.x+vector.y*vector.y+vector.z*vector.z);}
 private static Vec3 normalize(Vec3 vector){double length=length(vector);return length<1.0E-4?Vec3.ZERO:new Vec3(vector.x/length,vector.y/length,vector.z/length);}
 private static int impact(Entity entity){double mass=entity.getBbHeight()*entity.getBbWidth()*entity.getBbWidth();double s1=length(entity.getDeltaMovement()),s2=length(new Vec3(entity.getX()-entity.xo,entity.getY()-entity.yo,entity.getZ()-entity.zo));double speed=s1==0?s2:s2==0?s1:Math.min(s1,s2);return(int)(mass*speed*50);}
}
