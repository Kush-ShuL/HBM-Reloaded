package com.hbm.entity.mob.radbeast;

import com.hbm.damage.HbmDamageSources;
import com.hbm.entity.LegacyTrackingRange;
import com.hbm.entity.mob.glyphid.ai.LegacyCreatureMob;
import com.hbm.entity.mob.undead.mixin.UndeadSpawnTypeAccess;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;

/** X5846 EntityMob radiation elemental; deliberately not a modern Blaze or a goal-based replacement. */
public final class EntityRADBeast extends LegacyCreatureMob implements LegacyTrackingRange {
    private static final EntityDataAccessor<Integer> VICTIM=SynchedEntityData.defineId(EntityRADBeast.class,EntityDataSerializers.INT);
    private float heightOffset=.5F;
    private int heightOffsetUpdateTime;
    private boolean attacked;
    public EntityRADBeast(EntityType<? extends EntityRADBeast> type,Level world){super(type,world);xpReward=30;}
    public EntityRADBeast(Level world){this(RadiationBeastContent.ELEMENTAL.get(),world);}
    public static AttributeSupplier.Builder attributes(){return createMonsterAttributes().add(Attributes.MAX_HEALTH,120).add(Attributes.ATTACK_DAMAGE,16).add(Attributes.MOVEMENT_SPEED,.7).add(Attributes.FOLLOW_RANGE,16).add(Attributes.ARMOR,8);}
    @Override public int legacyTrackingRange(){return 80;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){super.defineSynchedData(data);data.define(VICTIM,0);}
    public EntityRADBeast makeLeader(){setDropChance(EquipmentSlot.MAINHAND,1F);setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(HbmContent.item("coin_radiation")));getAttribute(Attributes.MAX_HEALTH).setBaseValue(360);heal(getMaxHealth());return this;}
    @Override public boolean legacyMayDespawn(){return false;}
    @Override public float getLightLevelDependentMagicValue(){return 1F;}
    @Override protected void customServerAiStep(){attacked=false;super.customServerAiStep();}
    @Override public boolean legacyCeasedAttack(){return attacked;}
    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor world,DifficultyInstance difficulty,MobSpawnType reason,SpawnGroupData group){
        getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier(ResourceLocation.fromNamespaceAndPath("hbm","radbeast_spawn_"+UUID.randomUUID()),random.nextGaussian()*.05,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        ((UndeadSpawnTypeAccess)(Object)this).hbm$sourceSpawnType(reason);setLeftHanded(false);return group;
    }
    @Override protected SoundEvent getAmbientSound(){return HbmSounds.get("item.geiger"+(1+random.nextInt(6)));}
    @Override protected SoundEvent getHurtSound(DamageSource source){return SoundEvents.BLAZE_HURT;}
    @Override protected SoundEvent getDeathSound(){return HbmSounds.get("step.iron");}
    @Override public boolean causeFallDamage(float distance,float multiplier,DamageSource source){return false;}
    @Override public void aiStep(){
        if(!level().isClientSide){
            if(isInWaterOrRain())hurt(damageSources().drown(),1);
            if(--heightOffsetUpdateTime<=0){heightOffsetUpdateTime=100;heightOffset=.5F+(float)random.nextGaussian()*3F;}
            if(legacyTarget!=null&&legacyTarget.getEyeY()>getEyeY()+heightOffset){var motion=getDeltaMovement();setDeltaMovement(motion.x,motion.y+(.30000001192092896D-motion.y)*.30000001192092896D,motion.z);}
            entityData.set(VICTIM,legacyTarget!=null&&legacyAttackTime<10?legacyTarget.getId():0);
        }
        var motion=getDeltaMovement();if(!onGround()&&motion.y<0)setDeltaMovement(motion.x,motion.y*.6D,motion.z);
        // Keep both-side entity RNG consumption even though ServerLevel.addParticle has no local visual output.
        if(getMaxHealth()<=150){
            for(int i=0;i<6;i++)level().addParticle(ParticleTypes.MYCELIUM,getX()+(random.nextDouble()-.5D)*getBbWidth()*1.5D,getY()+random.nextDouble()*getBbHeight(),getZ()+(random.nextDouble()-.5D)*getBbWidth()*1.5D,0,0,0);
            if(random.nextInt(6)==0)level().addParticle(ParticleTypes.FLAME,getX()+(random.nextDouble()-.5D)*getBbWidth(),getY()+random.nextDouble()*getBbHeight()*.75D,getZ()+(random.nextDouble()-.5D)*getBbWidth(),0,0,0);
        }else level().addParticle(ParticleTypes.LAVA,getX()+(random.nextDouble()-.5D)*getBbWidth(),getY()+random.nextDouble()*getBbHeight()*.75D,getZ()+(random.nextDouble()-.5D)*getBbWidth(),0,0,0);
        super.aiStep();
    }
    @Override public void attackLegacyTarget(Entity target,float distance){
        if(legacyAttackTime<=0&&distance<2F&&target.getBoundingBox().maxY>getBoundingBox().minY&&target.getBoundingBox().minY<getBoundingBox().maxY){legacyAttackTime=20;doHurtTarget(target);}
        else if(distance<30F){
            double x=target.getX()-getX(),z=target.getZ()-getZ();
            if(legacyAttackTime==0&&legacyTarget!=null){HbmRadiation.incrementRad((ServerLevel)level(),blockPosition().getX(),blockPosition().getY(),blockPosition().getZ(),100);target.hurt(HbmDamageSources.source(level(),HbmDamageSources.radiation),16);swing(InteractionHand.MAIN_HAND);playAmbientSound();legacyAttackTime=20;}
            setYRot((float)(Math.atan2(z,x)*180D/Math.PI)-90F);attacked=true;
        }
    }
    public Entity getUnfortunateSoul(){return level().getEntity(entityData.get(VICTIM));}
    @Override protected void dropFromLootTable(DamageSource source,boolean recentlyHit){}
    @Override protected void dropCustomDeathLoot(ServerLevel world,DamageSource damage,boolean recentlyHit){
        if(!recentlyHit)return;int looting=0;
        if(damage.getEntity() instanceof LivingEntity attacker)looting=EnchantmentHelper.getItemEnchantmentLevel(world.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING),attacker.getMainHandItem());
        if(looting>0)spawnAtLocation(new ItemStack(HbmContent.item("nugget_polonium"),looting),0);
        int count=random.nextInt(3)+1;
        for(int i=0;i<count;i++){
            int kind=random.nextInt(3);boolean wet=isInWaterOrRain();String name=switch(kind){case 0->wet?"waste_uranium":"rod_zirnox_uranium_fuel_depleted";case 1->wet?"waste_mox":"rod_zirnox_mox_fuel_depleted";default->wet?"waste_plutonium":"rod_zirnox_plutonium_fuel_depleted";};
            spawnAtLocation(new ItemStack(HbmContent.item(name),wet?2:1),0);
        }
    }
    // The original watcher, attack cooldown and hover timers had no entity-specific NBT.
    // Native attributes/equipment persist the 360HP leader and coin; no third boss/leader table.
    // SourceAchievementEvents already awards bossMeltdown for this exact ID/maxHealth>150: no duplicate death hook.
}
