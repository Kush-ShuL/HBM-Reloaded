package com.hbm.entity.mob;
import com.hbm.entity.mob.sourcecreepers.SourceCreeper;
import com.hbm.advancement.HbmAdvancements;
import com.hbm.bomb.nuclear.*;
import com.hbm.damage.HbmDamageSources;
import com.hbm.explosion.*;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.*;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
/** Exact original class name also preserves its configured native DT5/DR.35 explosion resistance entry. */
public final class EntityCreeperNuclear extends SourceCreeper {
 public EntityCreeperNuclear(EntityType<? extends EntityCreeperNuclear> type,Level level){super(type,level,75);}
 public static AttributeSupplier.Builder attributes(){return Creeper.createAttributes().add(Attributes.MAX_HEALTH,50).add(Attributes.MOVEMENT_SPEED,.3);}
 @Override public boolean hurt(DamageSource source,float amount){if(isRemoved())return false;if(source.is(HbmDamageSources.radiation)||source.is(HbmDamageSources.mudPoisoning)){if(isAlive())heal(amount);return false;}return super.hurt(source,amount);}
 @Override protected void sourceDrops(boolean byPlayer,int looting){sourceFewItems(Items.TNT,looting);if(random.nextInt(3)==0)spawnAtLocation(HbmContent.item("coin_creeper"));}
 @Override public void die(DamageSource source){if(sourceDied)return;super.die(source);if(!level().isClientSide){for(var player:level().getEntitiesOfClass(Player.class,getBoundingBox().inflate(50)))if(player instanceof ServerPlayer server)HbmAdvancements.trigger(server,"bossCreeper");var killer=source.getEntity();if(killer instanceof AbstractSkeleton||source.is(DamageTypeTags.IS_PROJECTILE)&&killer instanceof Arrow arrow&&arrow.getOwner()==null)spawnAtLocation(PistolAmmo.NUKE_STANDARD.toStack(),1);}}
 @Override public void tick(){for(var entity:level().getEntities(this,getBoundingBox().inflate(5)))if(entity instanceof LivingEntity living)HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,.25F);super.tick();if(isAlive()&&getHealth()<getMaxHealth()&&tickCount%10==0)heal(1);}
 @Override public void sourceExplosion(){if(level().isClientSide)return;discard();boolean griefing=level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);if(isPowered()){NuclearMukeEffects.sendAtOrigin(level(),getX(),getY()+.5,getZ(),"muke",false,getX(),getY(),getZ());level().playSound(null,getX(),getY()+.5,getZ(),HbmSounds.get("weapon.mukeExplosion"),SoundSource.HOSTILE,15,1);if(griefing)level().addFreshEntity(NuclearExplosionMK5.statFac(level(),50,getX(),getY(),getZ()));else ExplosionNukeGeneric.dealDamage(level(),getX(),getY()+.5,getZ(),100);}else ExplosionNukeSmall.explode(level(),getX(),getY()+.5,getZ(),griefing?ExplosionNukeSmall.PARAMS_MEDIUM:ExplosionNukeSmall.PARAMS_SAFE);}
}
