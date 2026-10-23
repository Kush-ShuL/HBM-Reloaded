package com.hbm.bomb.conventional.grenade;

import com.hbm.bomb.conventional.ConventionalContent;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

/** EntityGrenadeBouncyBase/Generic's independent legacy flight, not EntityThrowableNT's different simulation. */
public final class GenericGrenade extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> ITEM=SynchedEntityData.defineId(GenericGrenade.class,EntityDataSerializers.INT);
    private LivingEntity thrower;private String throwerName;private int timer;
    public GenericGrenade(EntityType<? extends GenericGrenade> type,Level level){super(type,level);}
    public GenericGrenade(Level level,LivingEntity living){this(GrenadeContent.GENERIC.get(),level);thrower=living;
        setYRot(living.getYRot());setXRot(living.getXRot());
        setPos(living.getX()-(double)(Mth.cos(getYRot()/180F*(float)Math.PI)*.16F),living.getY()+living.getEyeHeight()-.10000000149011612D,living.getZ()-(double)(Mth.sin(getYRot()/180F*(float)Math.PI)*.16F));
        float factor=.4F;setThrowableHeading((double)(-Mth.sin(getYRot()/180F*(float)Math.PI)*Mth.cos(getXRot()/180F*(float)Math.PI)*factor),(double)(-Mth.sin(getXRot()/180F*(float)Math.PI)*factor),(double)(Mth.cos(getYRot()/180F*(float)Math.PI)*Mth.cos(getXRot()/180F*(float)Math.PI)*factor),1.5F,1);setXRot(0);xRotO=0;
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(ITEM,0);}
    public GenericGrenade setType(DynamiteGrenadeItem item){entityData.set(ITEM,BuiltInRegistries.ITEM.getId(item));return this;}
    public DynamiteGrenadeItem grenade(){var item=BuiltInRegistries.ITEM.byId(entityData.get(ITEM));return item instanceof DynamiteGrenadeItem grenade?grenade:GrenadeContent.DYNAMITE.get();}
    @Override public LivingEntity getOwner(){if(thrower==null&&throwerName!=null&&!throwerName.isEmpty())for(var player:level().players())if(player.getGameProfile().getName().equals(throwerName)){thrower=player;break;}return thrower;}
    public void setThrowableHeading(double x,double y,double z,float speed,float deviation){float length=(float)Math.sqrt(x*x+y*y+z*z);x/=length;y/=length;z/=length;x+=random.nextGaussian()*.007499999832361937D*deviation;y+=random.nextGaussian()*.007499999832361937D*deviation;z+=random.nextGaussian()*.007499999832361937D*deviation;setDeltaMovement(x*speed,y*speed,z*speed);yRotO=(float)(Math.atan2(x*speed,z*speed)*180D/Math.PI);setYRot(yRotO);}
    @Override public void lerpMotion(double x,double y,double z){setDeltaMovement(x,y,z);if(xRotO==0&&yRotO==0){yRotO=(float)(Math.atan2(x,z)*180D/Math.PI);setYRot(yRotO);}}
    @Override public void tick(){
        super.tick();xOld=getX();yOld=getY();zOld=getZ();xo=getX();yo=getY();zo=getZ();xRotO=getXRot();Vec3 velocity=getDeltaMovement();setXRot((float)(getXRot()-velocity.length()*25));
        Vec3 location=position();var impact=level().clip(new ClipContext(location,location.add(velocity),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));
        if(impact.getType()!=HitResult.Type.MISS){location=location.add(impact.getLocation().subtract(location).scale(.6));velocity=switch(impact.getDirection().getAxis()){case X->velocity.multiply(-1,1,1);case Y->velocity.multiply(1,-1,1);case Z->velocity.multiply(1,1,-1);};if(velocity.length()>.05)level().playSound(null,location.x,location.y,location.z,HbmSounds.get("weapon.gBounce"),SoundSource.NEUTRAL,2,1);velocity=velocity.scale(grenade().bounce());}
        else location=location.add(velocity);
        float yaw=(float)(Math.atan2(velocity.x,velocity.z)*180D/Math.PI);while(yaw-yRotO< -180)yRotO-=360;while(yaw-yRotO>=180)yRotO+=360;setYRot(yRotO+(yaw-yRotO)*.2F);
        float drag=.99F;if(isInWater()){for(int i=0;i<4;i++)level().addParticle(ParticleTypes.BUBBLE,location.x-velocity.x*.25F,location.y-velocity.y*.25F,location.z-velocity.z*.25F,velocity.x,velocity.y,velocity.z);drag=.8F;}
        setDeltaMovement(velocity.scale((double)drag).add(0,-(double).03F,0));setPos(location);timer++;
        if(timer>=grenade().fuseTicks()&&!level().isClientSide){grenade().explode(this,getOwner());discard();if(ConventionalContent.EXTENDED_LOGGING.getAsBoolean())org.slf4j.LoggerFactory.getLogger("hbm").info("[GREN] Set off grenade at {} / {} / {} by {}!",(int)getX(),(int)getY(),(int)getZ(),getOwner() instanceof Player player?player.getDisplayName().getString():"null");}
    }
    @Override public boolean shouldRenderAtSqrDistance(double distance){double range=getBoundingBox().getSize()*4D*64D;return distance<range*range;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("timer",timer);tag.putString("grenade",BuiltInRegistries.ITEM.getKey(grenade()).toString());}
    @Override protected void readAdditionalSaveData(CompoundTag tag){timer=tag.getInt("timer");var key=ResourceLocation.tryParse(tag.getString("grenade"));var item=key==null?null:BuiltInRegistries.ITEM.get(key);entityData.set(ITEM,BuiltInRegistries.ITEM.getId(item instanceof DynamiteGrenadeItem?item:GrenadeContent.DYNAMITE.get()));}
}
