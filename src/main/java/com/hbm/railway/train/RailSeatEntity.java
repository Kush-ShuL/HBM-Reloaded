package com.hbm.railway.train;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import static com.hbm.railway.train.RailTrainMath.*;
/** Real transient passenger seat and source tick-order-independent rider positioning. */
public final class RailSeatEntity extends Entity implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> PARENT=SynchedEntityData.defineId(RailSeatEntity.class,EntityDataSerializers.INT),INDEX=SynchedEntityData.defineId(RailSeatEntity.class,EntityDataSerializers.INT);
    private RailCarEntity train;private int increments;private double x,y,z;
    public RailSeatEntity(EntityType<? extends RailSeatEntity> type,Level level){super(type,level);}
    public RailSeatEntity(Level level,RailCarEntity train,int index){this(RailTrainContent.SEAT.get(),level);this.train=train;entityData.set(PARENT,train.getId());entityData.set(INDEX,index);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(PARENT,0);builder.define(INDEX,0);}
    public RailCarEntity parent(){if(train==null&&level().getEntity(entityData.get(PARENT))instanceof RailCarEntity found)train=found;return train;}
    @Override public int legacyTrackingRange(){return 250;}
    @Override protected boolean canAddPassenger(Entity passenger){return getPassengers().isEmpty();}
    @Override public LivingEntity getControllingPassenger(){return null;}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}@Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
    @Override public void tick(){xo=getX();yo=getY();zo=getZ();if(!level().isClientSide){if(parent()==null||parent().isRemoved())discard();}else if(increments>0){setPos(getX()+(x-getX())/increments,getY()+(y-getY())/increments,getZ()+(z-getZ())/increments);increments--;}else setPos(getX(),getY(),getZ());}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int increments){this.x=x;this.y=y;this.z=z;this.increments=increments+2;}
    @Override protected void positionRider(Entity passenger,MoveFunction move){if(!hasPassenger(passenger))return;double oldYOffset=passenger instanceof Player?(double)1.62F:0;if(parent()==null){move.accept(passenger,getX(),getY()+1-oldYOffset,getZ());return;}var offset=parent().getPassengerSeats()[entityData.get(INDEX)];rotateX(offset,(float)(train.rotationPitch*Math.PI/180));offset.rotateAroundY((float)(-train.rotationYaw*Math.PI/180));move.accept(passenger,train.renderX+offset.xCoord,train.renderY+offset.yCoord-oldYOffset,train.renderZ+offset.zCoord);}
}
