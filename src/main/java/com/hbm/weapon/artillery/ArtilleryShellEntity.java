package com.hbm.weapon.artillery;
import api.hbm.entity.IRadarDetectable;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.registry.HbmSounds;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.world.chunk.TicketController;
/** Actual source artillery shell, throwable motion/impact, separate client interpolation, cargo retrieval and a real entity ticking ticket. */
public final class ArtilleryShellEntity extends EntityThrowableNT implements IRadarDetectable,LegacyTrackingRange {
    public static final TicketController TICKETS=new TicketController(ResourceLocation.parse("hbm:artillery_shell"));
    private static final EntityDataAccessor<Integer> TYPE=SynchedEntityData.defineId(ArtilleryShellEntity.class,EntityDataSerializers.INT);
    public static Consumer<ArtilleryShellEntity> clientSmoke=ignored->{};
    public double targetX,targetY,targetZ;public boolean shouldWhistle,didWhistle;public int health=50;private ItemStack cargo=ItemStack.EMPTY;private ChunkPos forced;private int turnProgress;private double syncX,syncY,syncZ,syncYaw,syncPitch;private Vec3 velocity=Vec3.ZERO;
    public ArtilleryShellEntity(EntityType<? extends ArtilleryShellEntity> type,Level level){super(type,level);}
    public ArtilleryShellEntity(Level level){this(ArtilleryAmmoContent.SHELL.get(),level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(TYPE,0);}
    public ArtilleryShellEntity shellType(int type){entityData.set(TYPE,type);return this;}public int shellType(){int type=entityData.get(TYPE);return type<0||type>=12?0:type;}
    public void setTarget(double x,double y,double z){targetX=x;targetY=y;targetZ=z;}public void setCargo(ItemStack stack){cargo=stack.copy();}public ItemStack cargo(){return cargo;}
    @Override public boolean hurt(DamageSource source,float amount){if(isInvulnerableTo(source))return false;if(health>0&&!level().isClientSide){health-=(int)amount;if(health<=0)setDead();}return true;}
    @Override public boolean shouldRenderAtSqrDistance(double distance){return true;}@Override public int legacyTrackingRange(){return 1000;}@Override public boolean isPickable(){return true;}@Override public boolean skipAttackInteraction(Entity entity){return false;}
    @Override public void tick(){if(!level().isClientSide){super.tick();if(!didWhistle&&shouldWhistle){var motion=getDeltaMovement();double speed=Math.sqrt(motion.x*motion.x+motion.z*motion.z),dx=getX()-targetX,dz=getZ()-targetZ,dist=Math.sqrt(dx*dx+dz*dz);if(speed*18>dist){level().playSound(null,targetX,targetY,targetZ,HbmSounds.get("turret.mortarWhistle"),SoundSource.NEUTRAL,15,.9F+random.nextFloat()*.2F);didWhistle=true;}}if(!isRemoved())forceCurrentChunk();ArtyShellTypes.update(shellType(),this);}else{if(turnProgress>0){double x=getX()+(syncX-getX())/turnProgress,y=getY()+(syncY-getY())/turnProgress,z=getZ()+(syncZ-getZ())/turnProgress;setYRot((float)(getYRot()+Mth.wrapDegrees(syncYaw-getYRot())/turnProgress));setXRot((float)(getXRot()+(syncPitch-getXRot())/turnProgress));--turnProgress;setPos(x,y,z);}else setPos(getX(),getY(),getZ());double dx=syncX-getX(),dy=syncY-getY(),dz=syncZ-getZ();if((double)(float)Math.sqrt(dx*dx+dy*dy+dz*dz)<.2)clientSmoke.accept(this);}}
    @Override public void lerpMotion(double x,double y,double z){velocity=new Vec3(x,y,z);setDeltaMovement(velocity);}
    @Override public void lerpTo(double x,double y,double z,float yaw,float pitch,int steps){syncX=x;syncY=y;syncZ=z;syncYaw=yaw;syncPitch=pitch;turnProgress=steps;setDeltaMovement(velocity);}
    @Override protected void onImpact(HitResult hit){if(!level().isClientSide){if(hit instanceof EntityHitResult entity&&entity.getEntity() instanceof ArtilleryShellEntity)return;ArtyShellTypes.impact(shellType(),this,hit);}}
    @Override protected float getAirDrag(){return 1;}@Override public double getGravityVelocity(){return 9.81*.05;}@Override protected int groundDespawn(){return cargo.isEmpty()?1200:0;}
    @Override public InteractionResult interact(Player player,InteractionHand hand){if(!level().isClientSide){if(!cargo.isEmpty()){player.getInventory().add(cargo.copy());player.inventoryMenu.broadcastChanges();}setDead();}return InteractionResult.PASS;}
    @Override public RadarTargetType getTargetType(){return RadarTargetType.ARTILLERY;}
    @Override public void onAddedToLevel(){super.onAddedToLevel();forceCurrentChunk();}
    private void forceCurrentChunk(){if(!(level() instanceof ServerLevel server)||isRemoved())return;var next=chunkPosition();if(next.equals(forced))return;clearChunkLoader();TICKETS.forceChunk(server,this,next.x,next.z,true,true);forced=next;}
    public void clearChunkLoader(){if(forced!=null&&level() instanceof ServerLevel server)TICKETS.forceChunk(server,this,forced.x,forced.z,false,true);forced=null;}
    public void killAndClear(){setDead();clearChunkLoader();}
    @Override public void remove(RemovalReason reason){if(reason.shouldDestroy()||reason==RemovalReason.CHANGED_DIMENSION)clearChunkLoader();super.remove(reason);}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("type",entityData.get(TYPE));tag.putBoolean("shouldWhistle",shouldWhistle);tag.putBoolean("didWhistle",didWhistle);tag.putDouble("targetX",targetX);tag.putDouble("targetY",targetY);tag.putDouble("targetZ",targetZ);if(!cargo.isEmpty())tag.put("cargo",cargo.save(registryAccess()));}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);shellType(tag.getInt("type"));shouldWhistle=tag.getBoolean("shouldWhistle");didWhistle=tag.getBoolean("didWhistle");targetX=tag.getDouble("targetX");targetY=tag.getDouble("targetY");targetZ=tag.getDouble("targetZ");cargo=ItemStack.parseOptional(registryAccess(),tag.getCompound("cargo"));}
}
