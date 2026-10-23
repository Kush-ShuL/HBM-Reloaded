package com.hbm.railway.train;
import com.hbm.entity.LegacyTrackingRange;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
/** Source transient2x1 hit/collision proxy, forwarding actual interaction and damage to its train. */
public final class RailBoundsEntity extends Entity implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> PARENT=SynchedEntityData.defineId(RailBoundsEntity.class,EntityDataSerializers.INT);
    private RailCarEntity train;private int increments;private double x,y,z;
    public RailBoundsEntity(EntityType<? extends RailBoundsEntity> type,Level level){super(type,level);}
    public RailBoundsEntity(Level level,RailCarEntity train){this(RailTrainContent.BOUNDS.get(),level);this.train=train;entityData.set(PARENT,train.getId());}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(PARENT,0);}
    public RailCarEntity parent(){if(train==null&&level().getEntity(entityData.get(PARENT))instanceof RailCarEntity found)train=found;return train;}
    @Override public int legacyTrackingRange(){return 250;}
    @Override public boolean isPickable(){return !isRemoved();}@Override public boolean isPushable(){return true;}@Override public boolean canBeCollidedWith(){return !isRemoved();}
    @Override public boolean hurt(DamageSource source,float amount){var car=parent();return car!=null?car.hurt(source,amount):super.hurt(source,amount);}
    @Override public InteractionResult interact(Player player,InteractionHand hand){var car=parent();return car==null?super.interact(player,hand):car.interact(player,hand);}
    @Override protected void addAdditionalSaveData(CompoundTag tag){}@Override protected void readAdditionalSaveData(CompoundTag tag){discard();}
    @Override public void tick(){xo=getX();yo=getY();zo=getZ();if(!level().isClientSide){if(parent()==null||parent().isRemoved())discard();}else if(increments>0){setPos(getX()+(x-getX())/increments,getY()+(y-getY())/increments,getZ()+(z-getZ())/increments);increments--;}else setPos(getX(),getY(),getZ());}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int increments){this.x=x;this.y=y;this.z=z;this.increments=increments+2;}
}
