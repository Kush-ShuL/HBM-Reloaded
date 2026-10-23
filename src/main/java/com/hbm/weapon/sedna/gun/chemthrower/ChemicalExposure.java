package com.hbm.weapon.sedna.gun.chemthrower;
import com.hbm.bomb.conventional.MistExposure;
import com.hbm.damage.*;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.hbm.exposure.HbmExposure;
import com.hbm.fluid.*;
import com.hbm.radiation.HbmRadiation;
import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
/** Source EntityChemical.affect, deliberately distinct from EntityMist's different quantities and protection rules. */
public final class ChemicalExposure {
 public static void affect(EntityChemical chemical,Entity entity,double intensity){var type=chemical.getFluidType();var style=chemical.getStyle();LivingEntity living=entity instanceof LivingEntity l?l:null;if(style==ChemicalStyle.LIQUID||style==ChemicalStyle.BURNING)intensity=1;
  if(style==ChemicalStyle.AMAT){attack(entity,HbmDamageSources.source(chemical.level(),HbmDamageSources.radiation),1);if(living!=null){HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,50F*(float)intensity);return;}}
  if(style==ChemicalStyle.LIGHTNING){attack(entity,HbmDamageSources.source(chemical.level(),HbmDamageSources.electricity),.5F);if(living!=null){effect(living,MobEffects.MOVEMENT_SLOWDOWN,60,9);effect(living,MobEffects.WEAKNESS,60,9);return;}}
  if(type.temperature>=100){attack(entity,damage(chemical,HbmDamageSources.s_boil),Math.min(.25F+(type.temperature-100)*.001F,15));if(type.temperature>=500)entity.igniteForSeconds(10);}
  if(style==ChemicalStyle.LIQUID||style==ChemicalStyle.GAS){if(type.temperature< -20&&living!=null)attack(living,damage(chemical,HbmDamageSources.s_boil),Math.min(.25F+(-type.temperature)*.01F,2));if(type.hasTrait("delicious")&&living!=null&&living.isAlive())living.heal(2F*(float)intensity);}
  if(style==ChemicalStyle.LIQUID){if(type.hasTrait(FluidTraits.Flammable.class)&&living!=null)MistExposure.setOil(living,300);if(type.hasTrait("delicious")&&living!=null&&living.isAlive())living.heal(2F*(float)intensity);}// Source deliberately heals delicious liquid twice.
  if(chemical.isExtinguishing())entity.clearFire();
  if(style==ChemicalStyle.BURNING){var combustible=type.getTrait(FluidTraits.Combustible.class);attack(entity,damage(chemical,HbmDamageSources.s_flamethrower),.2F+(combustible!=null?Math.min(combustible.getCombustionEnergy()/100000F,15):0));entity.igniteForSeconds(5);}
  if(style==ChemicalStyle.GASFLAME){var flammable=type.getTrait(FluidTraits.Flammable.class);var combustible=type.getTrait(FluidTraits.Combustible.class);float heat=Math.max(flammable!=null?flammable.getHeatEnergy()/50000F:0,combustible!=null?Math.min(combustible.getCombustionEnergy()/100000F,15):0);heat*=intensity;attack(entity,damage(chemical,HbmDamageSources.s_flamethrower),(.2F+heat)*(float)intensity);entity.igniteForSeconds((int)Math.ceil(5*intensity));}
  var corrosive=type.getTrait(FluidTraits.Corrosive.class);if(corrosive!=null&&living!=null){attack(living,damage(chemical,HbmDamageSources.s_acid),corrosive.getRating()/50F);for(int i=0;i<4;i++)MistExposure.damageSuit(living,i,corrosive.getRating()/40);}
  var radiation=type.getTrait(FluidTraits.VentRadiation.class);if(radiation!=null){if(living!=null)HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,radiation.getRadPerMB()*5);if(chemical.level()instanceof ServerLevel server)HbmRadiation.incrementRad(server,Mth.floor(entity.getX()),Mth.floor(entity.getY()),Mth.floor(entity.getZ()),radiation.getRadPerMB()*5);}
  var poison=type.getTrait(FluidTraits.Poison.class);if(poison!=null&&living!=null)effect(living,poison.isWithering()?MobEffects.WITHER:MobEffects.POISON,(int)(100*intensity),0);
  var toxin=type.getTrait(FluidTraits.Toxin.class);if(toxin!=null&&living!=null)MistExposure.affectToxin(living,toxin,intensity);
  var pheromone=type.getTrait(FluidTraits.Pheromone.class);if(pheromone!=null&&living!=null){effect(living,MobEffects.DAMAGE_RESISTANCE,2400,2);effect(living,MobEffects.MOVEMENT_SPEED,6000,1);effect(living,MobEffects.DIG_SPEED,2400,4);if(EntityMist.isGlyphid(living)&&pheromone.getType()==1){effect(living,MobEffects.DAMAGE_BOOST,6000,4);effect(living,MobEffects.FIRE_RESISTANCE,1200,0);effect(living,MobEffects.ABSORPTION,1200,19);}else if(living instanceof Player&&pheromone.getType()==2)effect(living,MobEffects.DAMAGE_BOOST,2400,2);}
  if(type==HbmFluids.XPJUICE&&entity instanceof Player player){addExperienceWithoutScore(player,1);chemical.discard();}if(type==HbmFluids.ENDERJUICE)teleportRandomly(chemical,entity);
 }
 private static void attack(Entity entity,DamageSource source,float amount){LegacyEntityDamage.attackEntityFromIgnoreIFrame(entity,source,amount);}
 private static DamageSource damage(EntityChemical chemical,String name){var key=ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.fromNamespaceAndPath("hbm",name.toLowerCase(java.util.Locale.ROOT)));var holder=chemical.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);return chemical.getThrower()!=null?new DamageSource(holder,chemical,chemical.getThrower()):new DamageSource(holder);}
 private static void effect(LivingEntity entity,Holder<MobEffect> effect,int duration,int amplifier){entity.addEffect(new MobEffectInstance(effect,duration,amplifier));}
 /** Exact EnchantmentUtil.addExperience(...false): quantity increases XP progress/total and level sounds, without increasing scoreboard score. */
 private static void addExperienceWithoutScore(Player player,int amount){amount=Math.min(amount,Integer.MAX_VALUE-player.totalExperience);player.experienceProgress+=(float)amount/player.getXpNeededForNextLevel();for(player.totalExperience+=amount;player.experienceProgress>=1;player.experienceProgress/=player.getXpNeededForNextLevel()){player.experienceProgress=(player.experienceProgress-1)*player.getXpNeededForNextLevel();player.giveExperienceLevels(1);}}
 private static void teleportRandomly(EntityChemical chemical,Entity entity){var random=chemical.sourceRandom();teleportTo(chemical,entity,chemical.getX()+(random.nextDouble()-.5)*64,chemical.getY()+random.nextInt(64)-32,chemical.getZ()+(random.nextDouble()-.5)*64);}
 private static boolean teleportTo(EntityChemical chemical,Entity entity,double x,double y,double z){var world=chemical.level();var random=chemical.sourceRandom();Vec3 previous=entity.position();entity.setPosRaw(x,y,z);boolean accepted=false;int bx=Mth.floor(x),by=Mth.floor(y),bz=Mth.floor(z);if(world.hasChunkAt(new BlockPos(bx,by,bz))){boolean ground=false;while(!ground&&by>world.getMinBuildHeight()){if(LegacyMaterials.of(world.getBlockState(new BlockPos(bx,by-1,bz))).blocksMovement())ground=true;else{entity.setPosRaw(entity.getX(),entity.getY()-1,entity.getZ());by--;}}if(ground){entity.setPos(entity.getX(),entity.getY(),entity.getZ());accepted=world.noCollision(entity,entity.getBoundingBox())&&!world.containsAnyLiquid(entity.getBoundingBox());}}if(!accepted){entity.setPos(previous);return false;}if(entity instanceof ServerPlayer player)player.connection.teleport(entity.getX(),entity.getY(),entity.getZ(),entity.getYRot(),entity.getXRot());for(int i=0;i<128;i++){double time=(double)i/127;float vx=(random.nextFloat()-.5F)*.2F,vy=(random.nextFloat()-.5F)*.2F,vz=(random.nextFloat()-.5F)*.2F;world.addParticle(ParticleTypes.PORTAL,previous.x+(entity.getX()-previous.x)*time+(random.nextDouble()-.5)*entity.getBbWidth()*2,previous.y+(entity.getY()-previous.y)*time+random.nextDouble()*entity.getBbHeight(),previous.z+(entity.getZ()-previous.z)*time+(random.nextDouble()-.5)*entity.getBbWidth()*2,vx,vy,vz);}world.playSound(null,previous.x,previous.y,previous.z,SoundEvents.ENDERMAN_TELEPORT,SoundSource.NEUTRAL,1,1);entity.playSound(SoundEvents.ENDERMAN_TELEPORT,1,1);return true;}
 private ChemicalExposure(){}
}
