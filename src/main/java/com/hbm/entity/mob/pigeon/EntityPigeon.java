package com.hbm.entity.mob.pigeon;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.entity.mob.IFlyingCreature;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import com.hbm.item.fertilizer.ItemFertilizer;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Actual EntityCreature/IAnimals pigeon, not a chicken/rebreeding animal or a Monster. */
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.GAME)
public final class EntityPigeon extends PathfinderMob implements IFlyingCreature {
    private static final EntityDataAccessor<Byte> FLYING=SynchedEntityData.defineId(EntityPigeon.class,EntityDataSerializers.BYTE),FAT=SynchedEntityData.defineId(EntityPigeon.class,EntityDataSerializers.BYTE);
    private static final Set<EntityPigeon> ACTIVE=Collections.newSetFromMap(new IdentityHashMap<>());
    public float fallTime,dest,prevDest,prevFallTime,offGroundTimer=1F;
    private BlockPos pendingFertilizer;
    private NuclearChunkAccess chunkAccess;
    private boolean executing,closing;
    public EntityPigeon(EntityType<? extends EntityPigeon> type,Level world){super(type,world);}
    public EntityPigeon(Level world){this(PigeonContent.PIGEON.get(),world);}
    public static AttributeSupplier.Builder attributes(){return Mob.createMobAttributes().add(Attributes.MAX_HEALTH,20).add(Attributes.MOVEMENT_SPEED,.7).add(Attributes.FOLLOW_RANGE,16);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(FLYING,(byte)0);data.define(FAT,(byte)0);}
    @Override protected void registerGoals(){
        goalSelector.addGoal(0,new PigeonGoals.StartFlying(this));goalSelector.addGoal(0,new PigeonGoals.StopFlying(this));
        goalSelector.addGoal(1,new PigeonGoals.Swim(this));goalSelector.addGoal(2,new PigeonGoals.EatBread(this));
        goalSelector.addGoal(5,new PigeonGoals.Wander(this));goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,6));goalSelector.addGoal(7,new RandomLookAroundGoal(this));
    }
    @Override public int getFlyingState(){return entityData.get(FLYING);}
    @Override public void setFlyingState(int state){entityData.set(FLYING,(byte)state);}
    public boolean isFat(){return entityData.get(FAT)==1;}
    public void setFat(boolean fat){entityData.set(FAT,(byte)(fat?1:0));}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","pigeon_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;
    }
    @Override public boolean checkSpawnRules(LevelAccessor world,MobSpawnType reason){return true;}
    @Override public boolean checkSpawnObstruction(LevelReader world){return world.isUnobstructed(this)&&world.noCollision(this,getBoundingBox())&&!world.containsAnyLiquid(getBoundingBox());}
    @Override protected SoundEvent getAmbientSound(){return null;}
    @Override protected SoundEvent getHurtSound(DamageSource source){return null;}
    @Override protected SoundEvent getDeathSound(){return null;}
    @Override protected void playStepSound(BlockPos pos,BlockState state){playSound(SoundEvents.CHICKEN_STEP,.15F,1);}
    @Override protected MovementEmission getMovementEmission(){return MovementEmission.NONE;}
    @Override public boolean isIgnoringBlockTriggers(){return true;}
    @Override public boolean causeFallDamage(float amount,float multiplier,DamageSource source){return false;}
    @Override protected void checkFallDamage(double distance,boolean ground,BlockState state,BlockPos pos){}
    @Override public boolean hurt(DamageSource source,float amount){
        if(amount>=getMaxHealth()*2&&!level().isClientSide){
            discard();
            for(int i=0;i<10;i++){
                var motion=LegacyVector.createVectorHelper(random.nextGaussian(),random.nextGaussian(),random.nextGaussian()).normalize();
                var feather=new ItemEntity(level(),getX()+motion.xCoord,getY()+getBbHeight()/2D+motion.yCoord,getZ()+motion.zCoord,new ItemStack(Items.FEATHER));
                feather.setDeltaMovement(motion.xCoord*.5,motion.yCoord*.5,motion.zCoord*.5);feather.setPickUpDelay(0);level().addFreshEntity(feather);
            }
            return true;
        }
        return super.hurt(source,amount);
    }
    @Override protected void dropFromLootTable(DamageSource source,boolean recentlyHit){
        int looting=0;if(source.getEntity() instanceof LivingEntity killer){var enchant=registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);looting=EnchantmentHelper.getItemEnchantmentLevel(enchant,killer.getMainHandItem());}
        int count=random.nextInt(3)+random.nextInt(1+looting);for(int i=0;i<count;i++)spawnAtLocation(Items.FEATHER);
        spawnAtLocation(new ItemStack(isOnFire()?Items.COOKED_CHICKEN:Items.CHICKEN,isFat()?3:1));
    }
    @Override protected void dropCustomDeathLoot(ServerLevel world,DamageSource source,boolean recentlyHit){}
    @Override protected void customServerAiStep(){
        super.customServerAiStep();
        if(getFlyingState()==STATE_FLYING){
            int height=level().getHeight(Heightmap.Types.WORLD_SURFACE,net.minecraft.util.Mth.floor(getX()),net.minecraft.util.Mth.floor(getZ()));
            boolean ceiling=getY()-height>10;
            double vertical=random.nextGaussian()*.05+(ceiling?0:.04)+(isInWater()?.2:0);
            if(onGround())vertical=Math.abs(vertical)+.1;
            var motion=getDeltaMovement();setDeltaMovement(motion.x,vertical,motion.z);zza=1.5F;
            if(random.nextInt(20)==0)setYRot((float)(getYRot()+random.nextGaussian()*30));
            if(isFat()&&random.nextInt(50)==0){
                PigeonEffects.sweat(this);
                pendingFertilizer=BlockPos.containing(getX(),getY(),getZ()).below();
                ACTIVE.add(this);advanceFertilizer();
            }
        }else if(!onGround()&&getDeltaMovement().y<0){var motion=getDeltaMovement();setDeltaMovement(motion.x,motion.y*.8,motion.z);}
    }
    @Override public void tick(){
        boolean pause=false;
        if(pendingFertilizer!=null&&level() instanceof ServerLevel){advanceFertilizer();pause=pendingFertilizer!=null;}
        // Cold-wait only pauses this actual source AI branch, not native physics or visual flap state.
        boolean previous=isNoAi();if(pause)setNoAi(true);
        try{super.tick();}finally{if(pause)setNoAi(previous);}
    }
    private void advanceFertilizer(){
        if(pendingFertilizer==null||!(level() instanceof ServerLevel server))return;
        if(chunkAccess==null)chunkAccess=new NuclearChunkAccess();
        var selected=pendingFertilizer;
        try{if(chunkAccess.getWithNeighbors(server,selected.getX()>>4,selected.getZ()>>4)==null)return;}
        catch(RuntimeException|Error failure){release();throw failure;}
        executing=true;
        try{
            var player=FakePlayerFactory.getMinecraft(server);
            for(int i=0;i<25;i++){
                var pos=selected.below(i);
                if(ItemFertilizer.fertilize(server,pos.getX(),pos.getY(),pos.getZ(),player,true)){server.levelEvent(2005,pos,0);break;}
            }
            if(random.nextInt(10)==0)setFat(false);
            pendingFertilizer=null;
        }finally{executing=false;release();}
    }
    @Override public void aiStep(){
        super.aiStep();prevFallTime=fallTime;prevDest=dest;dest=(float)((double)dest+(double)(onGround()?-1:4)*.3D);
        if(dest<0)dest=0;if(dest>1)dest=1;if(!onGround()&&offGroundTimer<1)offGroundTimer=1;
        offGroundTimer=(float)((double)offGroundTimer*.9D);
        if(!onGround()&&getDeltaMovement().y<0){var motion=getDeltaMovement();setDeltaMovement(motion.x,motion.y*.6D,motion.z);}
        fallTime+=offGroundTimer*2F;
    }
    @Override public void remove(RemovalReason reason){super.remove(reason);release();}
    private void release(){
        if(executing){closing=true;return;}
        if(chunkAccess!=null){chunkAccess.close();chunkAccess=null;}
        ACTIVE.remove(this);if(closing||isRemoved())pendingFertilizer=null;closing=false;
    }
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);if(pendingFertilizer!=null)tag.putLong("hbm_pigeon_fertilizer",pendingFertilizer.asLong());}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);if(tag.contains("hbm_pigeon_fertilizer")){pendingFertilizer=BlockPos.of(tag.getLong("hbm_pigeon_fertilizer"));if(level() instanceof ServerLevel)ACTIVE.add(this);}}
    @SubscribeEvent public static void leave(EntityLeaveLevelEvent event){if(event.getEntity() instanceof EntityPigeon pigeon&&pigeon.level() instanceof ServerLevel)pigeon.release();}
    @SubscribeEvent public static void unload(LevelEvent.Unload event){if(event.getLevel() instanceof ServerLevel world)for(var pigeon:List.copyOf(ACTIVE))if(pigeon.level()==world)pigeon.release();}
    @SubscribeEvent public static void stopping(ServerStoppingEvent event){for(var pigeon:List.copyOf(ACTIVE))if(pigeon.level() instanceof ServerLevel world&&world.getServer()==event.getServer())pigeon.release();}
}
