package com.hbm.entity.mob.glyphid;

import com.hbm.damage.*;
import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.entity.mob.glyphid.ai.*;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.pollution.*;
import com.hbm.weapon.sedna.death.DeathParticlePacket;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Actual original Glyphid: the existing old non-goal mob controller, armor bits and seven task protocol. */
public class EntityGlyphid extends LegacyCreatureMob implements IResistanceProvider {
    public static final int TASK_IDLE=0,TASK_RETREAT_FOR_REINFORCEMENTS=1,TASK_BUILD_HIVE=2,TASK_INITIATE_RETREAT=3,TASK_FOLLOW=4,TASK_TERRAFORM=5,TASK_DIG=6;
    public static final int TYPE_NORMAL=0,TYPE_INFECTED=1,TYPE_RADIOACTIVE=2;
    private static final EntityDataAccessor<Byte> WALL=SynchedEntityData.defineId(EntityGlyphid.class,EntityDataSerializers.BYTE),ARMOR=SynchedEntityData.defineId(EntityGlyphid.class,EntityDataSerializers.BYTE),SUBTYPE=SynchedEntityData.defineId(EntityGlyphid.class,EntityDataSerializers.BYTE);
    public boolean hasHome,hasWaypoint,shouldDig;
    public int homeX,homeY,homeZ,taskX,taskY,taskZ;
    protected int currentTask,previousTask;
    protected EntityWaypoint taskWaypoint,previousWaypoint;
    public int blastSize=Math.min((int)(3*sourceScale())/2,5),blastResToDig=Math.min((int)(50*(sourceScale()*2)),150);
    public EntityGlyphid(EntityType<? extends EntityGlyphid> type,Level level){super(type,level);}
    public EntityGlyphid(Level level){this(GlyphidContent.GRUNT.get(),level);}
    public static AttributeSupplier.Builder attributes(GlyphidStats.StatBundle stats){return createMonsterAttributes().add(Attributes.MAX_HEALTH,stats.health()).add(Attributes.MOVEMENT_SPEED,stats.speed()).add(Attributes.ATTACK_DAMAGE,stats.damage()).add(Attributes.FOLLOW_RANGE,16);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(WALL,(byte)0);builder.define(ARMOR,(byte)31);builder.define(SUBTYPE,(byte)0);}
    public GlyphidStats.StatBundle getStats(){return GlyphidStats.getStats().getGrunt();}
    public double sourceScale(){return 1;}
    public ResourceLocation getSkin(){return ResourceLocation.parse("hbm:textures/entity/glyphid.png");}
    public int subtype(){return entityData.get(SUBTYPE);}
    /** Source spawners change watcher18 only; they do not reapply attributes afterwards. */
    public void setSubtype(int value){entityData.set(SUBTYPE,(byte)value);}
    public byte armorBits(){return entityData.get(ARMOR);}
    public boolean isScout(){return false;}
    public boolean isNuclearGlyphid(){return false;}
    @Override public float[] getCurrentDTDR(DamageSource source,float amount,float pierceDT,float pierce){
        if(DamageResistanceHandler.isAbsolute(source)||source.is(DamageTypeTags.BYPASSES_ARMOR))return new float[]{0,0};
        var stats=getStats();float threshold=stats.thresholdMultForArmor()*getGlyphidArmor()/5F;String kind=source.getMsgId();
        if(source.is(HbmDamageSources.nuclearBlast))return new float[]{threshold*.25F,0};
        if(kind.equals("laser"))return new float[]{threshold*.5F,stats.resistanceMult()*.5F};
        if(kind.equals("electric"))return new float[]{threshold*.25F,stats.resistanceMult()*.25F};
        if(kind.equals("subatomic"))return new float[]{0,stats.resistanceMult()*.1F};
        if(source.is(DamageTypeTags.IS_FIRE))return new float[]{0,stats.resistanceMult()*.2F};
        if(source.is(DamageTypeTags.IS_EXPLOSION))return new float[]{threshold*.5F,stats.resistanceMult()*.35F};
        return new float[]{threshold,stats.resistanceMult()};
    }
    @Override public void onDamageDealt(DamageSource source,float amount){if(isArmorBroken(amount))breakOffArmor();}
    @Override public boolean hurt(DamageSource source,float amount){if(source.getEntity() instanceof EntityGlyphid)return false;return super.hurt(source,amount);}
    @Override public Entity findLegacyTarget(){return hasEffect(MobEffects.BLINDNESS)?null:LegacyMobController.nearestPlayer(this,useExtendedTargeting()?128:16,true);}
    public boolean useExtendedTargeting(){return GlyphidConfig.extended()||level() instanceof ServerLevel world&&PollutionSystem.get(world,blockPosition(),PollutionType.SOOT)>=GlyphidConfig.TARGET_THRESHOLD.get();}
    @Override public boolean legacyMayWander(){return getCurrentTask()==TASK_IDLE;}
    @Override public boolean legacyMayDespawn(){return legacyTarget==null&&getCurrentTask()==TASK_IDLE&&tickCount>100;}
    @Override protected void afterLegacyActionState(){
        if(getId()%3>0&&(getId()+tickCount)%100==0){var target=findLegacyTarget();if(target!=null)legacyTarget=target;}
        if(hasEffect(MobEffects.BLINDNESS)||legacyPath!=null)return;
        if(useExtendedTargeting()&&legacyTarget!=null){legacyPath=GlyphidPaths.toEntity(this,legacyTarget,16);return;}
        if(getCurrentTask()==TASK_IDLE||isAtDestination())return;
        if(taskWaypoint!=null){taskX=(int)taskWaypoint.getX();taskY=(int)taskWaypoint.getY();taskZ=(int)taskWaypoint.getZ();if(taskWaypoint.highPriority)legacyTarget=taskWaypoint;}
        if(!hasWaypoint)return;
        var obstruction=canDig()?findWaypointObstruction():null;
        if(canDig()&&sourceScale()>=1&&getCurrentTask()!=TASK_DIG&&obstruction!=null)digToWaypoint(obstruction);
        else legacyPath=GlyphidPaths.toCoordinates(this,taskX,taskY,taskZ,(int)(Math.sqrt(distanceToSqr(taskX,taskY,taskZ))*1.2));
    }
    @Override public void tick(){
        super.tick();
        if(!level().isClientSide){
            if(!hasHome){homeX=(int)getX();homeY=(int)getY();homeZ=(int)getZ();hasHome=true;}
            if(hasEffect(MobEffects.BLINDNESS))onBlinded();
            if(getCurrentTask()==TASK_FOLLOW){if(isAtDestination()&&!hasWaypoint)setCurrentTask(TASK_IDLE,null);}
            else if(getCurrentTask()==TASK_DIG&&tickCount%20==0&&isAtDestination()){
                sourceSwing();new ExplosionVNT(level(),taskX,taskY+2,taskZ,blastSize,this).setBlockAllocator(new BlockAllocatorGlyphidDig(blastResToDig)).setBlockProcessor(new BlockProcessorStandard().setNoDrop()).setEntityProcessor(null).setPlayerProcessor(null).explode();
                setCurrentTask(previousTask,previousWaypoint);
            }
            setBesideClimbableBlock(horizontalCollision);if(tickCount%100==0)sourceSwing();
        }
    }
    protected boolean canDig(){return GlyphidConfig.dig();}
    public void onBlinded(){
        legacyClearTarget();fleeingTicks=80;
        if(sourceScale()<1.25||tickCount%20!=0)return;
        for(int i=0;i<16;i++){
            float angle=(float)Math.toRadians(360D/16*i);var delta=com.hbm.bomb.nuclear.LegacyVector.createVectorHelper(0,0,4);delta.rotateAroundY(angle);
            Vec3 start=position().add(0,1,0);var hit=level().clip(new ClipContext(start,start.add(delta.xCoord,0,delta.zCoord),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));
            if(hit.getType()==HitResult.Type.BLOCK&&BuiltInRegistries.BLOCK.getKey(level().getBlockState(hit.getBlockPos()).getBlock()).equals(ResourceLocation.parse("hbm:lantern"))){setYRot(360F/16*i);sourceSwing();level().destroyBlock(hit.getBlockPos(),false);}
        }
    }
    @Override public boolean doHurtTarget(Entity victim){
        if(swinging)return false;sourceSwing();
        if(subtype()==TYPE_INFECTED&&victim instanceof LivingEntity living){living.addEffect(new MobEffectInstance(MobEffects.POISON,100,2));living.addEffect(new MobEffectInstance(MobEffects.CONFUSION,100));}
        return super.doHurtTarget(victim);
    }
    public void sourceSwing(){swing(InteractionHand.MAIN_HAND);}
    public int swingDuration(){return 15;}
    @Override public int getCurrentSwingDuration(){return swingDuration();}
    @Override protected void updateSwingTime(){int duration=swingDuration();if(swinging){if(++swingTime>=duration){swingTime=0;swinging=false;}}else swingTime=0;attackAnim=(float)swingTime/duration;}
    @Override public void makeStuckInBlock(BlockState state,Vec3 factor){if(!state.is(Blocks.COBWEB))super.makeStuckInBlock(state,factor);}
    @Override public boolean onClimbable(){return (entityData.get(WALL)&1)!=0;}
    public void setBesideClimbableBlock(boolean value){byte current=entityData.get(WALL);entityData.set(WALL,(byte)(value?current|1:current&-2));}
    public boolean isArmorBroken(float amount){return random.nextInt(100)<=Math.min(Math.pow(amount*.6,2),100);}
    public void breakOffArmor(){
        byte armor=armorBits();var indices=Arrays.asList(0,1,2,3,4);Collections.shuffle(indices);
        for(int i:indices){int bit=1<<i;if((armor&bit)>0){armor=(byte)((armor&~bit)&31);entityData.set(ARMOR,armor);playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,1,1.25F);break;}}
    }
    public int getGlyphidArmor(){return Integer.bitCount(armorBits()&31);}
    public boolean doesInfectedSpawnMaggots(){return true;}
    @Override public void die(DamageSource source){super.die(source);if(!level().isClientSide&&doesInfectedSpawnMaggots()&&subtype()==TYPE_INFECTED){spawnMaggots(2+random.nextInt(3));level().playSound(null,getX(),getY(),getZ(),SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,getSoundSource(),2,.95F+level().random.nextFloat()*.2F);DeathParticlePacket.giblets(this,0,1);}}
    protected void spawnMaggots(int amount){for(int k=0;k<amount;k++){float x=((k%2)-.5F)*.5F,z=((k/2)-.5F)*.5F;var maggot=new ParasiteMaggot(level());maggot.moveTo(getX()+x,getY()+.5,getZ()+z,random.nextFloat()*360,0);maggot.setDeltaMovement(x,maggot.getDeltaMovement().y,z);maggot.hurtMarked=true;level().addFreshEntity(maggot);}}
    protected ItemStack sourceItem(String name,int count){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(!BuiltInRegistries.ITEM.containsKey(id))throw new IllegalStateException("Actual Glyphid item dependency missing: "+id);return new ItemStack(BuiltInRegistries.ITEM.get(id),count);}
    @Override protected void dropCustomDeathLoot(ServerLevel world,DamageSource damage,boolean recentlyHit){
        super.dropCustomDeathLoot(world,damage,recentlyHit);int looting=0;
        if(damage.getEntity() instanceof LivingEntity attacker)looting=EnchantmentHelper.getItemEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),attacker.getMainHandItem());
        sourceDrops(recentlyHit,looting);
    }
    protected void sourceDrops(boolean recentlyHit,int looting){if(random.nextInt(2)==0)spawnAtLocation(sourceItem(isOnFire()?"glyphid_meat_grilled":"glyphid_meat",((int)sourceScale()*2)+looting),0);}
    public int getCurrentTask(){return currentTask;}
    public EntityWaypoint getWaypoint(){return taskWaypoint;}
    public void setCurrentTask(int task,EntityWaypoint waypoint){currentTask=task;taskWaypoint=waypoint;hasWaypoint=waypoint!=null;if(waypoint!=null){taskX=(int)waypoint.getX();taskY=(int)waypoint.getY();taskZ=(int)waypoint.getZ();if(waypoint.highPriority)legacyClearTarget();}carryOutTask();}
    public void carryOutTask(){
        switch(getCurrentTask()){
            case TASK_RETREAT_FOR_REINFORCEMENTS->{if(taskWaypoint!=null){communicate(TASK_FOLLOW,taskWaypoint);setCurrentTask(TASK_FOLLOW,taskWaypoint);}}
            case TASK_INITIATE_RETREAT->{if(!level().isClientSide&&taskWaypoint==null){var additional=new EntityWaypoint(level());additional.moveTo(getX(),getY(),getZ(),0,0);var home=new EntityWaypoint(level());home.setWaypointType(TASK_RETREAT_FOR_REINFORCEMENTS);home.setAdditionalWaypoint(additional);home.highPriority=true;home.moveTo(homeX,homeY,homeZ,0,0);level().addFreshEntity(home);taskWaypoint=home;communicate(TASK_FOLLOW,home);setCurrentTask(TASK_FOLLOW,home);}}
            case TASK_DIG->shouldDig=true;
        }
    }
    public void communicate(int task,EntityWaypoint waypoint){int radius=waypoint!=null?waypoint.radius:4;for(var entity:level().getEntities(this,new AABB(getX(),getY(),getZ(),getX(),getY(),getZ()).inflate(radius)))if(entity instanceof EntityGlyphid glyphid&&!glyphid.isScout()&&glyphid.getCurrentTask()!=task)glyphid.setCurrentTask(task,waypoint);}
    public boolean expandHive(){return false;}
    public boolean isAtDestination(){int radius=taskWaypoint!=null?(int)Math.pow(taskWaypoint.radius,2):25;return distanceToSqr(taskX,taskY,taskZ)<=radius;}
    public BlockHitResult findWaypointObstruction(){var hit=level().clip(new ClipContext(position().add(0,getEyeHeight(),0),new Vec3(taskX,taskY,taskZ),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));return hit.getType()==HitResult.Type.BLOCK&&level().getBlockState(hit.getBlockPos()).getBlock().getExplosionResistance()<=blastResToDig?hit:null;}
    public void digToWaypoint(BlockHitResult obstruction){var target=new EntityWaypoint(level());target.moveTo(obstruction.getBlockPos().getX(),obstruction.getBlockPos().getY(),obstruction.getBlockPos().getZ(),0,0);target.radius=5;level().addFreshEntity(target);previousTask=getCurrentTask();previousWaypoint=getWaypoint();setCurrentTask(TASK_DIG,target);legacyPath=GlyphidPaths.toCoordinates(this,taskX,taskY,taskZ,(int)(Math.sqrt(distanceToSqr(taskX,taskY,taskZ))*1.2));communicate(TASK_DIG,target);}
    @Override public boolean checkSpawnRules(LevelAccessor level,MobSpawnType reason){return level.getDifficulty()!=Difficulty.PEACEFUL;}
    @Override public boolean checkSpawnObstruction(LevelReader level){return level().getDifficulty()!=Difficulty.PEACEFUL&&level.isUnobstructed(this)&&level.noCollision(this,getBoundingBox())&&!level.containsAnyLiquid(getBoundingBox());}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putByte("armor",armorBits());tag.putByte("subtype",(byte)subtype());tag.putBoolean("hasHome",hasHome);tag.putInt("homeX",homeX);tag.putInt("homeY",homeY);tag.putInt("homeZ",homeZ);tag.putBoolean("hasWaypoint",hasWaypoint);tag.putInt("taskX",taskX);tag.putInt("taskY",taskY);tag.putInt("taskZ",taskZ);tag.putInt("task",currentTask);}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);entityData.set(ARMOR,tag.getByte("armor"));setSubtype(tag.getByte("subtype"));hasHome=tag.getBoolean("hasHome");homeX=tag.getInt("homeX");homeY=tag.getInt("homeY");homeZ=tag.getInt("homeZ");hasWaypoint=tag.getBoolean("hasWaypoint");taskX=tag.getInt("taskX");taskY=tag.getInt("taskY");taskZ=tag.getInt("taskZ");currentTask=tag.getInt("task");}
}
