package com.hbm.entity.mob.undead;

import com.hbm.armor.taurun.TaurunContent;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import com.hbm.registry.HbmContent;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 EntityUndeadSoldier: the source deliberately has no attack goal. */
public final class UndeadSoldier extends Monster implements LegacyTrackingRange {
    public static final byte TYPE_ZOMBIE=0,TYPE_SKELETON=1;
    private static final EntityDataAccessor<Byte> BODY_TYPE=SynchedEntityData.defineId(UndeadSoldier.class,EntityDataSerializers.BYTE);
    private static final String[] GUNS={"gun_heavy_revolver","gun_light_revolver","gun_carbine","gun_maresleg","gun_greasegun"};

    public UndeadSoldier(EntityType<? extends UndeadSoldier> type,Level level){super(type,level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(BODY_TYPE,TYPE_ZOMBIE);}
    public byte bodyType(){return entityData.get(BODY_TYPE);}
    @Override public int legacyTrackingRange(){return 80;}
    public static AttributeSupplier.Builder attributes(){return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE,40).add(Attributes.MOVEMENT_SPEED,.25).add(Attributes.ATTACK_DAMAGE,5);}
    @Override protected void registerGoals(){
        goalSelector.addGoal(0,new FloatGoal(this));
        goalSelector.addGoal(4,new RandomStrollGoal(this,1));
        goalSelector.addGoal(5,new LookAtPlayerGoal(this,Player.class,8));
        goalSelector.addGoal(6,new RandomLookAroundGoal(this));
        targetSelector.addGoal(1,new HurtByTargetGoal(this));
        targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,Player.class,0,true,false,null));
        targetSelector.addGoal(3,new NearestAttackableTargetGoal<>(this,Villager.class,0,true,false,null));
    }
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        // Preserve source order: armor, one of five weapons, body kind, then the
        // original EntityLiving's Gaussian follow-range bonus. Modern super would
        // instead use triangular noise and randomly make this right-handed mob left-handed.
        equipSourceArmor();
        entityData.set(BODY_TYPE,random.nextBoolean()?TYPE_ZOMBIE:TYPE_SKELETON);
        var range=getAttribute(Attributes.FOLLOW_RANGE);
        if(range!=null)range.addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","undead_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);
        setLeftHanded(false);
        return group;
    }
    private void equipSourceArmor(){
        setItemSlot(EquipmentSlot.HEAD,new ItemStack(TaurunContent.HELMET.get()));
        setItemSlot(EquipmentSlot.CHEST,new ItemStack(TaurunContent.PLATE.get()));
        setItemSlot(EquipmentSlot.LEGS,new ItemStack(TaurunContent.LEGS.get()));
        setItemSlot(EquipmentSlot.FEET,new ItemStack(TaurunContent.BOOTS.get()));
        setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(HbmContent.item(GUNS[random.nextInt(5)])));
    }
    @Override protected SoundEvent getAmbientSound(){return bodyType()==TYPE_SKELETON?SoundEvents.SKELETON_AMBIENT:SoundEvents.ZOMBIE_AMBIENT;}
    @Override protected SoundEvent getHurtSound(DamageSource damage){return bodyType()==TYPE_SKELETON?SoundEvents.SKELETON_HURT:SoundEvents.ZOMBIE_HURT;}
    @Override protected SoundEvent getDeathSound(){return bodyType()==TYPE_SKELETON?SoundEvents.SKELETON_DEATH:SoundEvents.ZOMBIE_DEATH;}
    @Override protected void playStepSound(BlockPos pos,BlockState state){playSound(bodyType()==TYPE_SKELETON?SoundEvents.SKELETON_STEP:SoundEvents.ZOMBIE_STEP,.15F,1);}
    @Override public boolean checkSpawnRules(LevelAccessor world,MobSpawnType reason){return world.getDifficulty()!=Difficulty.PEACEFUL;}
    @Override public boolean checkSpawnObstruction(LevelReader world){return world.isUnobstructed(this)&&world.noCollision(this,getBoundingBox())&&!world.containsAnyLiquid(getBoundingBox());}
    /** Future DungeonSpawner calls this exact collision/non-liquid predicate, with no light requirement. */
    public boolean sourceCanSpawnHere(){return level().getDifficulty()!=Difficulty.PEACEFUL&&checkSpawnObstruction(level());}
    @Override protected void dropFromLootTable(DamageSource damage,boolean recentlyHit){ }
    @Override protected void dropCustomDeathLoot(ServerLevel world,DamageSource damage,boolean recentlyHit){ }
    @Override protected void dropEquipment(){ }
    // BODY_TYPE intentionally has no extra NBT field: the original watcher-only
    // value was not persisted and restored entities return to the zombie default.
}
