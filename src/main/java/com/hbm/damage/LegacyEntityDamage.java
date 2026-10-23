package com.hbm.damage;

import com.hbm.damage.mixin.LivingDamageAccess;
import java.util.List;
import java.util.Random;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.ClipContext;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

/** X5846 EntityDamageUtil SEDNA path, independent of vanilla's armor/toughness. */
public final class LegacyEntityDamage {
    private static final Random ENCHANTMENT_RANDOM=new Random();
    private LegacyEntityDamage(){}
    private static LivingDamageAccess access(LivingEntity living){return (LivingDamageAccess)living;}

    public static boolean attackEntityFromNT(LivingEntity living,DamageSource source,float amount,boolean ignoreIFrame,boolean allowSpecialCancel,double knockbackMultiplier,float pierceDT,float pierceDR){
        if(living instanceof ServerPlayer victim&&source.getEntity() instanceof Player attacker){
            if(victim.getServer()!=null&&!victim.getServer().isPvpAllowed())return false;
            if(!victim.canHarmPlayer(attacker))return false;
        }
        DamageResistanceHandler.setup(pierceDT,pierceDR);
        try{return com.hbm.config.SourceServerConfig.DAMAGE_COMPATIBILITY_MODE.get()
                ?attackEntitySuperCompatibility(living,source,amount,ignoreIFrame,allowSpecialCancel,knockbackMultiplier)
                :attackEntitySEDNAPatch(living,source,amount,ignoreIFrame,allowSpecialCancel,knockbackMultiplier);
        }finally{DamageResistanceHandler.reset();}
    }
    private static boolean attackEntitySuperCompatibility(LivingEntity living,DamageSource source,float amount,boolean ignoreIFrame,boolean allowSpecialCancel,double knockbackMultiplier){
        if(ignoreIFrame){access(living).hbm$setLastHurt(0);living.invulnerableTime=0;}
        // Preserve the original compatibility branch's X/X/X velocity cache.
        // The default SEDNA branch below does not contain this source defect.
        double x=living.getDeltaMovement().x;
        boolean result=vanillaProbe(living,source,amount);
        living.setDeltaMovement(x,x,x);
        Entity attacker=source.getEntity();
        if(attacker!=null){double[] delta=knockbackDirection(living,attacker);living.indicateDamage(delta[0],delta[1]);if(knockbackMultiplier>0)knockBack(living,attacker,amount,delta[0],delta[1],knockbackMultiplier);}
        return result;
    }
    private static boolean vanillaProbe(LivingEntity living,DamageSource source,float amount){
        // NeoForge early cancellations may leave its protected container stack
        // populated. A source zero-damage probe must not leak this transient state.
        var containers=access(living).hbm$getDamageContainers();int depth=containers.size();
        try{return living.hurt(source,amount);}finally{while(containers.size()>depth)containers.pop();}
    }
    private static boolean attackEntitySEDNAPatch(LivingEntity living,DamageSource source,float amount,boolean ignoreIFrame,boolean allowSpecialCancel,double knockbackMultiplier){
        // Original side-effect probe: lets entity-specific hurt overrides react,
        // then executes the common SEDNA body even if such an override returned false.
        vanillaProbe(living,source,0);
        LivingDamageAccess state=access(living);
        if(ignoreIFrame)state.hbm$setLastHurt(0);
        DamageContainer container=new DamageContainer(source,amount);
        var containers=state.hbm$getDamageContainers();int depth=containers.size();containers.push(container);
        try{
            if(CommonHooks.onEntityIncomingDamage(living,container)&&allowSpecialCancel)return false;
            amount=container.getNewDamage();
            if(living.isInvulnerable()||living.level().isClientSide)return false;
            if(living instanceof Player player&&player.getAbilities().invulnerable&&!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))return false;
            living.setNoActionTime(0);
            if(living.getHealth()<=0)return false;
            if(source.is(DamageTypeTags.IS_FIRE)&&living.hasEffect(MobEffects.FIRE_RESISTANCE))return false;
            living.walkAnimation.setSpeed(1.5F);
            boolean registered=true;
            if(living.invulnerableTime>living.invulnerableDuration/2F&&!ignoreIFrame){
                if(amount<=state.hbm$getLastHurt())return false;
                container.setReduction(DamageContainer.Reduction.INVULNERABILITY,state.hbm$getLastHurt());
                damageEntityNT(living,source,amount-state.hbm$getLastHurt(),container);
                state.hbm$setLastHurt(amount);registered=false;
            }else{
                state.hbm$setLastHurt(amount);living.invulnerableTime=living.invulnerableDuration;
                damageEntityNT(living,source,amount,container);living.hurtTime=living.hurtDuration=10;
            }
            Entity attacker=source.getEntity();
            if(attacker!=null){
                if(attacker instanceof LivingEntity entity)living.setLastHurtByMob(entity);
                if(attacker instanceof Player player){state.hbm$setLastHurtByPlayerTime(100);state.hbm$setLastHurtByPlayer(player);}
                else if(attacker instanceof TamableAnimal animal&&animal.isTame()){state.hbm$setLastHurtByPlayerTime(100);state.hbm$setLastHurtByPlayer(null);}
            }
            if(registered){
                living.level().broadcastDamageEvent(living,source);
                if(!source.is(DamageTypes.DROWN))setBeenAttacked(living);
                if(attacker!=null){double[] delta=knockbackDirection(living,attacker);living.indicateDamage(delta[0],delta[1]);if(knockbackMultiplier>0)knockBack(living,attacker,amount,delta[0],delta[1],knockbackMultiplier);}
            }
            if(living.getHealth()<=0){if(registered)playSound(living,state.hbm$getDeathSound());living.die(source);}
            else if(registered)playSound(living,state.hbm$getHurtSound(source));
            // Target bookkeeping replaces old CombatTracker's implicit fields.
            state.hbm$setLastDamageSource(source);state.hbm$setLastDamageStamp(living.level().getGameTime());
            return true;
        }finally{while(containers.size()>depth)containers.pop();}
    }
    private static void playSound(LivingEntity living,SoundEvent sound){if(sound!=null)living.playSound(sound,access(living).hbm$getSoundVolume(),access(living).hbm$getVoicePitch());}
    private static double[] knockbackDirection(LivingEntity living,Entity attacker){
        double x=attacker.getX()-living.getX(),z=attacker.getZ()-living.getZ();
        while(x*x+z*z<1.0E-4){x=(Math.random()-Math.random())*.01;z=(Math.random()-Math.random())*.01;}return new double[]{x,z};
    }
    public static void knockBack(LivingEntity living,Entity attacker,float damage,double motionX,double motionZ,double multiplier){
        if(living.getRandom().nextDouble()>=living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)){
            living.hasImpulse=true;double horizontal=Math.sqrt(motionX*motionX+motionZ*motionZ),magnitude=.4*multiplier;
            Vec3 previous=living.getDeltaMovement();double y=previous.y/2+magnitude;if(y>.2)y=.2*multiplier;
            living.setDeltaMovement(previous.x/2-motionX/horizontal*magnitude,y,previous.z/2-motionZ/horizontal*magnitude);
        }
    }
    public static void setBeenAttacked(LivingEntity living){living.hurtMarked=living.getRandom().nextDouble()>=living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);}
    public static void damageEntityNT(LivingEntity living,DamageSource source,float amount){
        DamageContainer container=new DamageContainer(source,amount);var values=access(living).hbm$getDamageContainers();int depth=values.size();values.push(container);
        try{damageEntityNT(living,source,amount,container);}finally{while(values.size()>depth)values.pop();}
    }
    private static void damageEntityNT(LivingEntity living,DamageSource source,float amount,DamageContainer container){
        if(living.isInvulnerable())return;
        float original=container.getNewDamage();
        float afterResistance=DamageResistanceHandler.onEntityDamaged(living,source,amount);
        container.setReduction(DamageContainer.Reduction.INNATE_RESISTANCE,original-afterResistance);
        if(afterResistance<=0)return;
        float afterArmor=applyArmorCalculationsNT(living,source,afterResistance);
        container.setReduction(DamageContainer.Reduction.ARMOR,container.getNewDamage()-afterArmor);
        float afterPotion=applyPotionDamageCalculations(living,source,afterArmor);
        container.setReduction(DamageContainer.Reduction.MOB_EFFECTS,container.getNewDamage()-afterPotion);
        float damage=CommonHooks.onLivingDamagePre(living,container);
        float amountAfterAbsorption=Math.max(damage-living.getAbsorptionAmount(),0);
        container.setReduction(DamageContainer.Reduction.ABSORPTION,damage-amountAfterAbsorption);
        float finalDamage=container.getNewDamage();
        living.setAbsorptionAmount(living.getAbsorptionAmount()-(damage-finalDamage));
        if(finalDamage!=0){
            living.setHealth(living.getHealth()-finalDamage);living.getCombatTracker().recordDamage(source,finalDamage);
            // Original second absorption subtraction is intentional source behavior.
            living.setAbsorptionAmount(living.getAbsorptionAmount()-finalDamage);
        }
        CommonHooks.onLivingDamagePost(living,container);
    }
    public static float applyArmorCalculationsNT(LivingEntity living,DamageSource source,float amount){
        if(!source.is(DamageTypeTags.BYPASSES_ARMOR)){
            float armor=25-(living.getArmorValue()*(1-DamageResistanceHandler.currentPDR()));
            amount=amount*armor/25;
            // Source damageArmorNT is empty: no armor durability damage.
        }return amount;
    }
    public static void damageArmorNT(LivingEntity living,float amount){}
    public static float applyPotionDamageCalculations(LivingEntity living,DamageSource source,float amount){
        if(DamageResistanceHandler.isAbsolute(source))return amount;
        if(living.hasEffect(MobEffects.DAMAGE_RESISTANCE)&&!source.is(DamageTypes.FELL_OUT_OF_WORLD)){
            int resistance=(living.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier()+1)*5;
            amount=amount*(25-resistance)/25;
        }
        if(amount<=0)return 0;
        int protection=Math.min(20,legacyEnchantmentProtection(living,source));
        if(protection>0)amount=amount*(25-protection)/25;
        return amount;
    }
    public static int legacyEnchantmentProtection(LivingEntity living,DamageSource source){
        if(source.is(DamageTypes.FELL_OUT_OF_WORLD))return 0;
        int sum=0;
        List<EquipmentSlot> equipment=living instanceof Player
                ?List.of(EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD)
                :List.of(EquipmentSlot.MAINHAND,EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD);
        for(EquipmentSlot slot:equipment){
            var stack=living.getItemBySlot(slot);if(stack.isEmpty())continue;
            sum+=oldProtection(enchantmentLevel(living,stack,Enchantments.PROTECTION),.75F);
            if(source.is(DamageTypeTags.IS_FIRE))sum+=oldProtection(enchantmentLevel(living,stack,Enchantments.FIRE_PROTECTION),1.25F);
            if(source.is(DamageTypes.FALL))sum+=oldProtection(enchantmentLevel(living,stack,Enchantments.FEATHER_FALLING),2.5F);
            if(source.is(DamageTypeTags.IS_EXPLOSION))sum+=oldProtection(enchantmentLevel(living,stack,Enchantments.BLAST_PROTECTION),1.5F);
            if(source.is(DamageTypeTags.IS_PROJECTILE))sum+=oldProtection(enchantmentLevel(living,stack,Enchantments.PROJECTILE_PROTECTION),1.5F);
        }
        sum=Mth.clamp(sum,0,25);
        return ((sum+1)>>1)+ENCHANTMENT_RANDOM.nextInt((sum>>1)+1);
    }
    private static int enchantmentLevel(LivingEntity living,net.minecraft.world.item.ItemStack stack,ResourceKey<Enchantment> key){return EnchantmentHelper.getItemEnchantmentLevel(living.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key),stack);}
    private static int oldProtection(int level,float multiplier){return level<=0?0:Mth.floor(((6+level*level)/3F)*multiplier);}
    @Deprecated public static boolean attackEntityFromIgnoreIFrame(Entity victim,DamageSource source,float amount){
        if(victim.hurt(source,amount))return true;
        if(victim instanceof LivingEntity living&&living.invulnerableTime>living.invulnerableDuration/2F)amount+=access(living).hbm$getLastHurt();
        return victim.hurt(source,amount);
    }
    public static HitResult getMouseOver(Player attacker,double reach){return getMouseOver(attacker,reach,0);}
    public static HitResult getMouseOver(Player attacker,double reach,double threshold){
        HitResult block=rayTrace(attacker,reach,1);Entity pointed=null;Vec3 position=getPosition(attacker),look=attacker.getViewVector(1),end=position.add(look.scale(reach)),hit=null;
        double closest=reach;
        List<Entity> entities=attacker.level().getEntities(attacker,attacker.getBoundingBox().expandTowards(look.scale(reach)).inflate(1));
        for(Entity entity:entities)if(entity.isPickable()&&entity.isAlive()){
            AABB bounds=entity.getBoundingBox().inflate(entity.getPickRadius()+threshold);var intercept=bounds.clip(position,end);
            if(bounds.contains(position)){
                if(closest>=0){pointed=entity;hit=intercept.orElse(position);closest=0;}
            }else if(intercept.isPresent()){
                double distance=position.distanceTo(intercept.get());
                if(distance<closest||closest==0){
                    if(entity==attacker.getVehicle()&&!entity.canRiderInteract()){if(closest==0){pointed=entity;hit=intercept.get();}}
                    else{pointed=entity;hit=intercept.get();closest=distance;}
                }
            }
        }
        // Preserve source ordering: entity reach is not shortened to a block hit.
        return pointed!=null&&(closest<reach||block==null)?new EntityHitResult(pointed,hit):block;
    }
    public static HitResult rayTrace(Player player,double distance,float interpolation){
        Vec3 position=getPosition(player),end=position.add(player.getViewVector(interpolation).scale(distance));
        return player.level().clip(new ClipContext(position,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
    }
    public static Vec3 getPosition(Player player){return player.getEyePosition();}
    public static boolean isRealPlayer(Entity entity){return entity instanceof Player&&!(entity instanceof FakePlayer);}
}
