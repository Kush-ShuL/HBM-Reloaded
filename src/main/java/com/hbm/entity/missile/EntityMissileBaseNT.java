package com.hbm.entity.missile;

import api.hbm.entity.IRadarDetectableNT;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableInterp;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.item.missile.MissileItem;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.world.chunk.TicketController;

/** X5846 source NT missile, using the existing actual EntityThrowableInterp/NT simulation. */
public abstract class EntityMissileBaseNT extends EntityThrowableInterp implements IRadarDetectableNT,LegacyTrackingRange {
    public static final TicketController TICKETS=new TicketController(ResourceLocation.parse("hbm:missile_nt"));
    private static final EntityDataAccessor<Byte> ROTATION=SynchedEntityData.defineId(EntityMissileBaseNT.class,EntityDataSerializers.BYTE);
    public int startX,startZ,targetX,targetZ;
    public double velocity,decelY,accelXZ;
    public boolean isCluster;
    public int health=50;
    public int trackingRange=1000;
    private ChunkPos loaded;
    private boolean ticketRegistered;
    private double lastX,lastY,lastZ;
    /** Client-installed native particle sink; server never links client classes. */
    public static Consumer<Contrail> contrails=ignored->{};
    public record Contrail(Level level,double x,double y,double z,double mx,double my,double mz,float scale,int life){}
    protected EntityMissileBaseNT(EntityType<?> type,Level level){super(type,level);noCulling=true;startX=targetX=(int)getX();startZ=targetZ=(int)getZ();}
    protected final void initialize(float x,float y,float z,int targetX,int targetZ){
        setPos(x,y,z);setYRot(0);setXRot(0);startX=(int)x;startZ=(int)z;this.targetX=targetX;this.targetZ=targetZ;setDeltaMovement(0,2,0);
        var vector=LegacyVector.createVectorHelper(targetX-startX,0,targetZ-startZ);
        // Integer-coordinate horizontal targets have range >=1 unless directly overhead. Preserve every valid source
        // trajectory and use its minimum one-block trajectory for that undefined divide-by-zero case.
        accelXZ=decelY=1/Math.max(1,vector.lengthVector());decelY*=2;velocity=0;
        setYRot((float)(Math.atan2(targetX-getX(),targetZ-getZ())*180D/Math.PI));
    }
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(ROTATION,(byte)5);}
    public byte rotation(){return entityData.get(ROTATION);}public void setRotation(byte value){entityData.set(ROTATION,value);}
    @Override protected double motionMult(){return velocity;}
    @Override public boolean doesImpactEntities(){return false;}
    @Override public void tick(){
        lastX=getX();lastY=getY();lastZ=getZ();super.tick();
        if(velocity<4)velocity+=Mth.clamp(tickCount/60D*.05D,0,.05);
        if(!level().isClientSide){
            Vec3 motion=getDeltaMovement();double mx=motion.x,my=motion.y,mz=motion.z;
            if(hasPropulsion()){
                my-=decelY*velocity;var vector=LegacyVector.createVectorHelper(targetX-startX,0,targetZ-startZ).normalize();vector.xCoord*=accelXZ;vector.zCoord*=accelXZ;
                if(my>0){mx+=vector.xCoord*velocity;mz+=vector.zCoord*velocity;}
                if(my<0){mx-=vector.xCoord*velocity;mz-=vector.zCoord*velocity;}
            }else{mx*=.99;mz*=.99;if(my> -1.5)my-=.05;}
            setDeltaMovement(mx,my,mz);
            if(my< -1.5&&isCluster){cluster();discard();return;}
            setYRot((float)(Math.atan2(targetX-getX(),targetZ-getZ())*180D/Math.PI));float horizontal=(float)Math.sqrt(mx*mx+mz*mz);
            setXRot((float)(Math.atan2(my,horizontal)*180D/Math.PI)-90);while(getXRot()-xRotO< -180)xRotO-=360;
            // The old tracker deliberately invalidates lastYaw every tick. Modern equivalent sends the actual source rotation.
            if(level() instanceof ServerLevel server)server.getChunkSource().broadcastAndSend(this,new ClientboundMoveEntityPacket.Rot(getId(),(byte)(getYRot()*256F/360F),(byte)(getXRot()*256F/360F),onGround()));
            loadNeighboringChunks(Mth.floor(getX()/16),Mth.floor(getZ()/16));
        }else spawnContrail();
        while(getXRot()-xRotO>=180)xRotO+=360;while(getYRot()-yRotO< -180)yRotO-=360;while(getYRot()-yRotO>=180)yRotO+=360;
    }
    public boolean hasPropulsion(){return true;}
    protected void spawnContrail(){spawnContrailWithOffset(0,0,0);}
    protected void spawnContrailWithOffset(double offsetX,double offsetY,double offsetZ){
        var vector=LegacyVector.createVectorHelper(lastX-getX(),lastY-getY(),lastZ-getZ());double length=vector.lengthVector();vector=vector.normalize();
        var thrust=LegacyVector.createVectorHelper(0,1,0);thrust.rotateAroundZ(getXRot()*(float)Math.PI/180F);thrust.rotateAroundY((getYRot()+90)*(float)Math.PI/180F);
        for(int i=0;i<Math.max(Math.min(length,10),1);i++){double j=i-length;contrails.accept(new Contrail(level(),getX()-vector.xCoord*j+offsetX,getY()-vector.yCoord*j+offsetY,getZ()-vector.zCoord*j+offsetZ,-thrust.xCoord,-thrust.yCoord,-thrust.zCoord,getContrailScale(),60+random.nextInt(20)));}
    }
    protected float getContrailScale(){return 1;}
    protected static void rotateAroundX(LegacyVector vector,float angle){float c=Mth.cos(angle),s=Mth.sin(angle);double y=vector.yCoord*c+vector.zCoord*s,z=vector.zCoord*c-vector.yCoord*s;vector.yCoord=y;vector.zCoord=z;}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);setDeltaMovement(tag.getDouble("moX"),tag.getDouble("moY"),tag.getDouble("moZ"));setPos(tag.getDouble("poX"),tag.getDouble("poY"),tag.getDouble("poZ"));decelY=tag.getDouble("decel");accelXZ=tag.getDouble("accel");targetX=tag.getInt("tX");targetZ=tag.getInt("tZ");startX=tag.getInt("sX");startZ=tag.getInt("sZ");velocity=tag.getDouble("veloc");}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);Vec3 motion=getDeltaMovement();tag.putDouble("moX",motion.x);tag.putDouble("moY",motion.y);tag.putDouble("moZ",motion.z);tag.putDouble("poX",getX());tag.putDouble("poY",getY());tag.putDouble("poZ",getZ());tag.putDouble("decel",decelY);tag.putDouble("accel",accelXZ);tag.putInt("tX",targetX);tag.putInt("tZ",targetZ);tag.putInt("sX",startX);tag.putInt("sZ",startZ);tag.putDouble("veloc",velocity);}
    @Override public boolean isPickable(){return true;}
    @Override public boolean hurt(DamageSource source,float amount){if(isInvulnerableTo(source))return false;if(health>0&&!level().isClientSide){health-=amount;if(health<=0)killMissile();}return true;}
    protected void killMissile(){if(!isRemoved()){discard();Vec3 motion=getDeltaMovement();ExplosionLarge.explode(level(),getX(),getY(),getZ(),5,true,false,true);ExplosionLarge.spawnShrapnelShower(level(),getX(),getY(),getZ(),motion.x,motion.y,motion.z,15,.075);ExplosionLarge.spawnMissileDebris(level(),getX(),getY(),getZ(),motion.x,motion.y,motion.z,.25,getDebris(),getDebrisRareDrop());}}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}
    @Override protected void onImpact(HitResult hit){if(hit!=null&&hit.getType()==HitResult.Type.BLOCK){onMissileImpact(hit);discard();}}
    public abstract void onMissileImpact(HitResult hit);
    public abstract ItemStack getMissileItemForInfo();
    public abstract List<ItemStack> getDebris();public abstract ItemStack getDebrisRareDrop();
    public void cluster(){}
    @Override public double getGravityVelocity(){return 0;}
    @Override protected float getAirDrag(){return 1;}@Override protected float getWaterDrag(){return 1;}
    @Override public void onAddedToLevel(){super.onAddedToLevel();loadNeighboringChunks(Mth.floor(getX()/16),Mth.floor(getZ()/16));}
    public void loadNeighboringChunks(int x,int z){if(!(level() instanceof ServerLevel server)||isRemoved())return;var current=new ChunkPos(x,z);if(loaded!=null&&!loaded.equals(current)){TICKETS.forceChunk(server,this,loaded.x,loaded.z,false,true);ticketRegistered=false;}if(!ticketRegistered||!current.equals(loaded)){TICKETS.forceChunk(server,this,x,z,true,true);loaded=current;ticketRegistered=true;}}
    public void clearChunkLoader(){if(level() instanceof ServerLevel server&&loaded!=null)TICKETS.forceChunk(server,this,loaded.x,loaded.z,false,true);loaded=null;ticketRegistered=false;}
    @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
    public void explodeStandard(float strength,int resolution,boolean fire){ExplosionVNT explosion=new ExplosionVNT(level(),getX(),getY(),getZ(),strength);explosion.setBlockAllocator(new BlockAllocatorStandard(resolution));explosion.setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(fire?new BlockMutatorFire():null));explosion.setEntityProcessor(new EntityProcessorCross(7.5).withRangeMod(2));explosion.setPlayerProcessor(new PlayerProcessorStandard());explosion.explode();}
    @Override public boolean canBeSeenBy(Object radar){return true;}
    @Override public boolean paramsApplicable(RadarScanParams parameters){return parameters.scanMissiles;}
    @Override public boolean suppliesRedstone(RadarScanParams parameters){return !(parameters.smartMode&&getDeltaMovement().y>=0);}
    @Override public int legacyTrackingRange(){return trackingRange;}
    @Override public String getUnlocalizedName(){ItemStack stack=getMissileItemForInfo();return stack!=null&&stack.getItem() instanceof MissileItem missile?"radar.target."+missile.tier.name().toLowerCase(Locale.ROOT):"Unknown";}
    @Override public int getBlipLevel(){ItemStack stack=getMissileItemForInfo();return stack!=null&&stack.getItem() instanceof MissileItem missile?missile.tier.ordinal():IRadarDetectableNT.SPECIAL;}
}
