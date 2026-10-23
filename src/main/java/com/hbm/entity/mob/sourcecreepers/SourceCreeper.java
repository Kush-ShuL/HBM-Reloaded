package com.hbm.entity.mob.sourcecreepers;
import com.hbm.entity.mob.sourcecreepers.mixin.SourceCreeperAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
/** The four source subclasses share the real old Creeper defaults. The private modern explosion is intercepted only for this base. */
public abstract class SourceCreeper extends Creeper {
 protected boolean sourceDied;
 protected SourceCreeper(EntityType<? extends SourceCreeper> type,Level level,int fuse){super(type,level);((SourceCreeperAccess)this).hbm$setFuseTime(fuse);}
 public abstract void sourceExplosion();
 @Override public boolean checkSpawnRules(LevelAccessor world,MobSpawnType reason){if(!(this instanceof com.hbm.entity.mob.EntityCreeperNuclear)){if(!level().dimension().equals(Level.OVERWORLD))return false;if((this instanceof com.hbm.entity.mob.EntityCreeperVolatile||this instanceof com.hbm.entity.mob.EntityCreeperGold)&&getY()>40)return false;}return super.checkSpawnRules(world,reason); }
 @Override protected void registerGoals(){goalSelector.addGoal(1,new FloatGoal(this));goalSelector.addGoal(2,new SwellGoal(this));goalSelector.addGoal(3,new AvoidEntityGoal<>(this,Ocelot.class,6,1,1.2));goalSelector.addGoal(4,new MeleeAttackGoal(this,1,false));goalSelector.addGoal(5,new RandomStrollGoal(this,.8));goalSelector.addGoal(6,new LookAtPlayerGoal(this,Player.class,8));goalSelector.addGoal(6,new RandomLookAroundGoal(this));targetSelector.addGoal(1,new NearestAttackableTargetGoal<>(this,Player.class,0,true,false,null));targetSelector.addGoal(2,new HurtByTargetGoal(this));}
 @Override protected void dropFromLootTable(DamageSource source,boolean recentlyHit){int looting=0;if(source.getEntity()instanceof LivingEntity killer){var enchant=registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);looting=EnchantmentHelper.getItemEnchantmentLevel(enchant,killer.getMainHandItem());}sourceDrops(recentlyHit,looting);}
 protected void sourceDrops(boolean byPlayer,int looting){sourceFewItems(Items.GUNPOWDER,looting);}
 protected final void sourceFewItems(Item item,int looting){int count=random.nextInt(3);if(looting>0)count+=random.nextInt(looting+1);for(int i=0;i<count;i++)spawnAtLocation(item);}
 /** Native modern charged-creeper skull drop did not exist in1.7; source disc is instead the exact12-record choice in Creeper.onDeath. */
 @Override protected void dropCustomDeathLoot(ServerLevel level,DamageSource source,boolean recentlyHit){}
 @Override public void die(DamageSource source){if(sourceDied)return;sourceDied=true;super.die(source);if(!level().isClientSide&&source.getEntity()instanceof AbstractSkeleton)spawnAtLocation(RECORDS[random.nextInt(RECORDS.length)]);}
 private static final Item[] RECORDS={Items.MUSIC_DISC_13,Items.MUSIC_DISC_CAT,Items.MUSIC_DISC_BLOCKS,Items.MUSIC_DISC_CHIRP,Items.MUSIC_DISC_FAR,Items.MUSIC_DISC_MALL,Items.MUSIC_DISC_MELLOHI,Items.MUSIC_DISC_STAL,Items.MUSIC_DISC_STRAD,Items.MUSIC_DISC_WARD,Items.MUSIC_DISC_11,Items.MUSIC_DISC_WAIT};
}
