package com.hbm.railway.train;

import com.hbm.entity.LegacyTrackingRange;
import com.hbm.item.battery.HbmBattery;
import com.hbm.railway.track.*;
import com.hbm.railway.track.IRailNTM.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.Level;
import static com.hbm.railway.train.RailTrainMath.*;

/** X5846 actual RailCarBase/Cargo/Ridable/Electric lifecycle; its LTU algorithm is kept in RailTrainMotion. */
public abstract class RailCarEntity extends Entity implements Container,MenuProvider,LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> OCCUPIED=SynchedEntityData.defineId(RailCarEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> POWER=SynchedEntityData.defineId(RailCarEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> YAW=SynchedEntityData.defineId(RailCarEntity.class,EntityDataSerializers.FLOAT),PITCH=SynchedEntityData.defineId(RailCarEntity.class,EntityDataSerializers.FLOAT);
    public RailTrainMotion.LogicalTrainUnit ltu;public int ltuIndex;
    public boolean isOnRail=true;public RailCarEntity coupledFront,coupledBack;
    public final boolean trailer;private final NonNullList<ItemStack> slots;
    public double lastRenderX,lastRenderY,lastRenderZ,renderX,renderY,renderZ,cachedSpeed,engineSpeed;
    public float rotationYaw,rotationPitch,prevRotationYaw,prevRotationPitch,movementYaw,movementPitch;
    private int turnProgress;private double trainX,trainY,trainZ,trainYaw,trainPitch;
    private boolean initDummies;private RailBoundsEntity[] dummies=new RailBoundsEntity[0];
    private final RailSeatEntity[] passengerSeats=new RailSeatEntity[2];
    protected RailCarEntity(EntityType<? extends RailCarEntity> type,Level level,boolean trailer){super(type,level);this.trailer=trailer;slots=NonNullList.withSize(trailer?45:29,ItemStack.EMPTY);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){builder.define(OCCUPIED,0);builder.define(POWER,0);builder.define(YAW,0F);builder.define(PITCH,0F);}
    @Override public int legacyTrackingRange(){return 250;}
    @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
    @Override protected boolean canAddPassenger(Entity passenger){return !trailer&&getPassengers().isEmpty();}
    @Override public LivingEntity getControllingPassenger(){return null;}
    @Override public boolean shouldRiderSit(){return false;}
    public TrackGauge getGauge(){return TrackGauge.STANDARD;}
    public double getLengthSpan(){return 1.5;}
    public double getCollisionSpan(){return 2.5;}
    public double getMaxRailSpeed(){return 1;}
    public int getMaxPower(){return 1000;}
    public int getPowerConsumption(){return 10;}
    public int getPower(){return entityData.get(POWER);}
    public void setPower(int power){entityData.set(POWER,power);}
    public int occupiedSlots(){return entityData.get(OCCUPIED);}
    public RailVector getRiderSeatPosition(){return vector(.375,2.375,.5);}
    public RailVector[] getPassengerSeats(){return new RailVector[]{vector(.5,1.75,-1.5),vector(-.5,1.75,-1.5)};}
    public boolean canAccelerate(){return true;} // Actual source return; power gate was commented out.
    public void consumeFuel(){} // Actual source power deduction was commented out.
    public double getCurrentSpeed(){if(trailer)return 0;var rider=getFirstPassenger();if(rider instanceof Player player){if(canAccelerate()){if(player.zza>0){engineSpeed+=.01;consumeFuel();}else if(player.zza<0){engineSpeed-=.01;consumeFuel();}else if(Math.abs(engineSpeed)<.1)engineSpeed*=.95;else consumeFuel();}else engineSpeed*=.95;}else engineSpeed*=.95;engineSpeed=Mth.clamp(engineSpeed,-.5,.5);return engineSpeed;}
    public BlockPos getCurrentAnchorPos(){return BlockPos.containing(getX(),getY()+.25,getZ());}
    public RailVector getRelPosAlongRail(BlockPos anchor,double distance,MoveContext context){return RailTrainMotion.getRelPosAlongRail(anchor,distance,getGauge(),level(),vector(getX(),getY(),getZ()),rotationYaw,context);}
    public void derail(){isOnRail=false;}
    public enum TrainCoupling {FRONT,BACK}
    public double getCouplingDist(TrainCoupling coupling){return coupling==null?0:2.75;}
    public RailVector getCouplingPos(TrainCoupling coupling){double distance=getCouplingDist(coupling);if(distance<=0)return null;if(coupling==TrainCoupling.BACK)distance=-distance;var result=vector(0,0,distance);result.rotateAroundY((float)(-rotationYaw*Math.PI/180D));result.xCoord+=renderX;result.yCoord+=renderY;result.zCoord+=renderZ;return result;}
    public RailCarEntity getCoupledTo(TrainCoupling coupling){return coupling==TrainCoupling.FRONT?coupledFront:coupling==TrainCoupling.BACK?coupledBack:null;}
    public TrainCoupling getCouplingFrom(RailCarEntity other){return other==coupledFront?TrainCoupling.FRONT:other==coupledBack?TrainCoupling.BACK:null;}
    public void couple(TrainCoupling coupling,RailCarEntity other){if(coupling==TrainCoupling.FRONT)coupledFront=other;if(coupling==TrainCoupling.BACK)coupledBack=other;}
    public boolean coupleWithTool(Player player,InteractionHand hand){if(!BuiltInRegistries.ITEM.getKey(player.getItemInHand(hand).getItem()).equals(RailTrainContent.id("coupling_tool")))return false;
        for(var neighbor:level().getEntitiesOfClass(RailCarEntity.class,getBoundingBox().inflate(2,0,2))){if(neighbor==this||neighbor.getGauge()!=getGauge())continue;TrainCoupling own=null,other=null;double nearest=Double.POSITIVE_INFINITY;for(var a:TrainCoupling.values())for(var b:TrainCoupling.values()){var pa=getCouplingPos(a);var pb=neighbor.getCouplingPos(b);if(pa==null||pb==null)continue;double length=vector(pa.xCoord-pb.xCoord,pa.yCoord-pb.yCoord,pa.zCoord-pb.zCoord).lengthVector();if(length<1&&length<nearest){nearest=length;own=a;other=b;}}if(own==null||other==null||getCoupledTo(own)!=null||neighbor.getCoupledTo(other)!=null)continue;couple(own,neighbor);neighbor.couple(other,this);if(ltu!=null)ltu.dissolveTrain();if(neighbor.ltu!=null)neighbor.ltu.dissolveTrain();player.swing(hand);player.displayClientMessage(Component.translatable("message.hbm.source_train.coupled",hashCode(),own.name(),neighbor.hashCode(),other.name()),false);return true;}return false;
    }
    @Override public InteractionResult interact(Player player,InteractionHand hand){if(coupleWithTool(player,hand))return trailer?InteractionResult.PASS:InteractionResult.SUCCESS; // Trailer's source super-success really returns false.
        if(level().isClientSide)return InteractionResult.SUCCESS;
        if(trailer){if(player instanceof ServerPlayer server)server.openMenu(this);return InteractionResult.CONSUME;}
        int nearest=getNearestSeat(player);if(nearest==-1){var old=getFirstPassenger();if(old!=null)old.stopRiding();player.startRiding(this,true);}else if(nearest>=0){var seat=new RailSeatEntity(level(),this,nearest);var offset=getPassengerSeats()[nearest];offset.rotateAroundY((float)(-rotationYaw*Math.PI/180));seat.setPos(renderX+offset.xCoord,renderY+offset.yCoord-1,renderZ+offset.zCoord);if(!RailTrainMotion.readyBox(level(),seat.getBoundingBox()))return InteractionResult.CONSUME;passengerSeats[nearest]=seat;level().addFreshEntity(seat);player.startRiding(seat,true);}return InteractionResult.CONSUME;
    }
    public int getNearestSeat(Player player){double nearestDistance=Double.POSITIVE_INFINITY;int nearest=-3;var look=player.getViewVector(2);double lx=look.x+player.getX(),ly=look.y+player.getEyeY(),lz=look.z+player.getZ();var seats=getPassengerSeats();for(int i=0;i<seats.length;i++){if(seats[i]==null||passengerSeats[i]!=null)continue;var seat=seats[i];seat.rotateAroundY((float)(-rotationYaw*Math.PI/180));double distance=vector(lx-(renderX+seat.xCoord),ly-(renderY+seat.yCoord),lz-(renderZ+seat.zCoord)).lengthVector();if(distance<nearestDistance){nearestDistance=distance;nearest=i;}}if(getFirstPassenger()==null){var seat=getRiderSeatPosition();seat.rotateAroundY((float)(-rotationYaw*Math.PI/180));double distance=vector(lx-(renderX+seat.xCoord),ly-(renderY+seat.yCoord),lz-(renderZ+seat.zCoord)).lengthVector();if(distance<nearestDistance){nearestDistance=distance;nearest=-1;}}return nearestDistance>180?-2:nearest;}
    @Override public boolean hurt(DamageSource source,float amount){if(!level().isClientSide&&!isRemoved())discard();return true;}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int increments){trainX=x;trainY=y;trainZ=z;trainYaw=movementYaw;trainPitch=movementPitch;turnProgress=increments+2;}
    @Override public void onSyncedDataUpdated(EntityDataAccessor<?> accessor){super.onSyncedDataUpdated(accessor);if(YAW.equals(accessor)){movementYaw=entityData.get(YAW);trainYaw=movementYaw;}if(PITCH.equals(accessor)){movementPitch=entityData.get(PITCH);trainPitch=movementPitch;}}
    public void publishAngles(){setYRot(rotationYaw);setXRot(rotationPitch);entityData.set(YAW,movementYaw);entityData.set(PITCH,movementPitch);hasImpulse=true;}
    @Override public void tick(){xo=getX();yo=getY();zo=getZ();prevRotationYaw=rotationYaw;prevRotationPitch=rotationPitch;yRotO=getYRot();xRotO=getXRot();
        if(level().isClientSide){if(turnProgress>0){double x=getX()+(trainX-getX())/turnProgress,y=getY()+(trainY-getY())/turnProgress,z=getZ()+(trainZ-getZ())/turnProgress;rotationYaw=(float)(rotationYaw+Mth.wrapDegrees(trainYaw-rotationYaw)/turnProgress);rotationPitch=(float)(rotationPitch+(trainPitch-rotationPitch)/turnProgress);turnProgress--;setPos(x,y,z);}else setPos(getX(),getY(),getZ());setYRot(rotationYaw);setXRot(rotationPitch);lastRenderX=renderX;lastRenderY=renderY;lastRenderZ=renderZ;try{var anchor=getCurrentAnchorPos();var front=getRelPosAlongRail(anchor,getLengthSpan(),new MoveContext(RailCheckType.FRONT,getCollisionSpan()-getLengthSpan()));var back=getRelPosAlongRail(anchor,-getLengthSpan(),new MoveContext(RailCheckType.BACK,getCollisionSpan()-getLengthSpan()));if(front!=null&&back!=null){renderX=(front.xCoord+back.xCoord)/2;renderY=(front.yCoord+back.yCoord)/2;renderZ=(front.zCoord+back.zCoord)/2;}else{renderX=getX();renderY=getY();renderZ=getZ();}}catch(RailTrainMotion.Unavailable ignored){/* Native unloaded rendering neighborhood: retain the last actual center. */}return;}
        if(!isOnRail){if(coupledFront!=null)coupledFront.couple(coupledFront.getCouplingFrom(this),null);if(coupledBack!=null)coupledBack.couple(coupledBack.getCouplingFrom(this),null);coupledFront=null;coupledBack=null;}
        if(coupledFront!=null&&coupledFront.isRemoved()){coupledFront=null;if(ltu!=null)ltu.dissolveTrain();}if(coupledBack!=null&&coupledBack.isRemoved()){coupledBack=null;if(ltu!=null)ltu.dissolveTrain();}
        if(ltu==null&&(coupledFront==null||coupledBack==null)&&isOnRail)RailTrainMotion.LogicalTrainUnit.generateTrain(this);
        if(!isOnRail){var motion=vector(0,0,cachedSpeed);motion.rotateAroundY((float)(-rotationYaw*Math.PI/180D));if(RailTrainMotion.readyBox(level(),getBoundingBox().expandTowards(motion.xCoord,motion.yCoord-.04,motion.zCoord))){move(MoverType.SELF,new Vec3(motion.xCoord,motion.yCoord-.04,motion.zCoord));renderX=getX();renderY=getY();renderZ=getZ();cachedSpeed*=.95;}}
        if(!initDummies&&RailTrainMotion.readyBox(level(),getBoundingBox())){dummies=new RailBoundsEntity[3];for(int i=0;i<3;i++){var dummy=new RailBoundsEntity(level(),this);var offset=vector(0,0,1.5-i*1.5);offset.rotateAroundY((float)(-rotationYaw*Math.PI/180));dummy.setPos(getX()+offset.xCoord,getY()+offset.yCoord,getZ()+offset.zCoord);level().addFreshEntity(dummy);dummies[i]=dummy;}initDummies=true;}
        if(initDummies&&renderY!=0)for(int i=0;i<3;i++){var offset=vector(0,0,1.5-i*1.5);rotateX(offset,(float)(rotationPitch*Math.PI/180D));offset.rotateAroundY((float)(-rotationYaw*Math.PI/180));dummies[i].setPos(renderX+offset.xCoord,renderY+offset.yCoord,renderZ+offset.zCoord);}
        entityData.set(OCCUPIED,countOccupiedSlots());
        if(!trailer){var seats=getPassengerSeats();for(int i=0;i<2;i++){var seat=passengerSeats[i];if(seat==null)continue;if(seat.getFirstPassenger()==null){passengerSeats[i]=null;seat.discard();}else{var offset=seats[i];rotateX(offset,(float)(rotationPitch*Math.PI/180));offset.rotateAroundY((float)(-rotationYaw*Math.PI/180));seat.setPos(renderX+offset.xCoord,renderY+offset.yCoord-1,renderZ+offset.zCoord);}}var stack=slots.get(28);if(!stack.isEmpty()&&stack.getItem() instanceof HbmBattery battery){int needed=getMaxPower()-getPower();long available=Math.min(battery.getDischargeRate(stack),battery.getCharge(stack));int amount=(int)Math.min(needed,available);if(amount>0){battery.dischargeBattery(stack,amount);setPower(getPower()+amount);}}else if(!stack.isEmpty()&&BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(RailTrainContent.id("battery_creative")))setPower(getMaxPower());}
    }
    @Override protected void positionRider(Entity passenger,MoveFunction move){if(!hasPassenger(passenger))return;var offset=getRiderSeatPosition();rotateX(offset,(float)(rotationPitch*Math.PI/180D));offset.rotateAroundY((float)(-rotationYaw*Math.PI/180D));move.accept(passenger,renderX+offset.xCoord,renderY+offset.yCoord-(passenger instanceof Player?(double)1.62F:0),renderZ+offset.zCoord);}
    public int countOccupiedSlots(){int count=0;for(var stack:slots)if(!stack.isEmpty())count++;return count;}
    @Override public int getContainerSize(){return slots.size();}@Override public boolean isEmpty(){return slots.stream().allMatch(ItemStack::isEmpty);}
    @Override public ItemStack getItem(int slot){return slots.get(slot);}@Override public ItemStack removeItem(int slot,int count){return ContainerHelper.removeItem(slots,slot,count);}@Override public ItemStack removeItemNoUpdate(int slot){return ContainerHelper.takeItem(slots,slot);}
    @Override public void setItem(int slot,ItemStack stack){slots.set(slot,stack);if(stack.getCount()>getMaxStackSize())stack.setCount(getMaxStackSize());}@Override public void setChanged(){}@Override public int getMaxStackSize(){return 64;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}@Override public void clearContent(){slots.clear();}
    @Override public boolean stillValid(Player player){return !isRemoved()&&player.distanceToSqr(this)<=64D;}
    @Override public Component getDisplayName(){return hasCustomName()?getCustomName():Component.translatable(trailer?"container.trainTramTrailer":"container.trainTram");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new RailTrainMenu(id,inventory,this);}
    @Override public void writeClientSideData(AbstractContainerMenu menu,RegistryFriendlyByteBuf data){data.writeInt(getId());}
    @Override protected void addAdditionalSaveData(CompoundTag tag){var list=new ListTag();for(int i=0;i<slots.size();i++)if(!slots.get(i).isEmpty()){var item=(CompoundTag)slots.get(i).save(registryAccess());item.putByte("Slot",(byte)i);list.add(item);}tag.put("Items",list);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){Collections.fill(slots,ItemStack.EMPTY);for(var value:tag.getList("Items",Tag.TAG_COMPOUND)){var item=(CompoundTag)value;int slot=item.getByte("Slot")&255;if(slot<slots.size())slots.set(slot,ItemStack.parseOptional(registryAccess(),item));}entityData.set(OCCUPIED,countOccupiedSlots());}
}
