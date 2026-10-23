package com.hbm.exposure;

import com.hbm.damage.HbmDamageSources;
import com.hbm.pollution.PollutionSystem;
import com.hbm.pollution.PollutionType;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmSounds;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Shared persistent contamination and lung disease for gases and later item hazards. */
public final class HbmExposure {
    public enum Contamination { RAD_BYPASS, CREATIVE }
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<ExposureData>> DATA = ATTACHMENTS.register("living_exposure",
            () -> AttachmentType.serializable(ExposureData::new).sync((holder,player)->holder==player,
                    StreamCodec.of((buf,data)-> {
                        buf.writeFloat(data.radiation);buf.writeFloat(data.digamma);buf.writeInt(data.asbestos);buf.writeInt(data.blackLung);
                        buf.writeFloat(data.radEnvironment);buf.writeFloat(data.radBuffer);
                        buf.writeInt(data.contamination.size());for(var effect:data.contamination)effect.write(buf);
                    },buf->{
                        var data=new ExposureData();data.radiation=buf.readFloat();data.digamma=buf.readFloat();data.asbestos=buf.readInt();data.blackLung=buf.readInt();
                        data.radEnvironment=buf.readFloat();data.radBuffer=buf.readFloat();
                        int count=buf.readInt();for(int i=0;i<count;i++)data.contamination.add(ContaminationEffect.read(buf));return data;
                    })).build());
    private static final TagKey<EntityType<?>> IMMUNE = TagKey.create(Registries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath("hbm","radiation_immune"));
    private static final TagKey<EntityType<?>> DIGAMMA_IMMUNE = TagKey.create(Registries.ENTITY_TYPE,ResourceLocation.fromNamespaceAndPath("hbm","digamma_immune"));
    private static final ResourceLocation DIGAMMA_HEALTH=ResourceLocation.fromNamespaceAndPath("hbm","digamma");
    private static final List<ExposureProtection> PROTECTION = new CopyOnWriteArrayList<>();
    private static ExposureSettings settings=ExposureSettings.DEFAULT;
    private static boolean registered;
    private HbmExposure() {}
    public static void register(IEventBus bus) {
        if(registered)return;registered=true;
        ATTACHMENTS.register(bus);ExposureEffects.register(bus);
        NeoForge.EVENT_BUS.addListener(HbmExposure::tick);
        NeoForge.EVENT_BUS.addListener(HbmExposure::incomingRadiationDamage);
        NeoForge.EVENT_BUS.addListener(HbmExposure::radiationEffectApplicable);
    }
    public static void configure(ExposureSettings settings) { HbmExposure.settings=Objects.requireNonNull(settings); }
    public static ExposureSettings settings() { return settings; }
    public static ExposureData data(LivingEntity entity) { return entity.getData(DATA); }
    /** Actual shared source contamination list; medicines and decontamination machinery see the same entries. */
    public static List<ContaminationEffect> contaminationEffects(LivingEntity entity) {return data(entity).contamination;}
    public static void addContamination(LivingEntity entity,float maxRad,int duration,boolean ignoreArmor) {
        if(entity.level().isClientSide||spectatorRadiationExempt(entity))return;
        data(entity).contamination.add(new ContaminationEffect(maxRad,duration,ignoreArmor));
    }
    public static void clearContamination(LivingEntity entity) {
        if(!entity.level().isClientSide&&entity.hasData(DATA))data(entity).contamination.clear();
    }
    public static void addProtection(ExposureProtection protection) { PROTECTION.add(Objects.requireNonNull(protection)); }
    public static void removeProtection(ExposureProtection protection) { PROTECTION.remove(protection); }
    public static boolean protectedFrom(LivingEntity entity, ExposureProtection.Hazard hazard) {
        return PROTECTION.stream().anyMatch(protection->protection.protects(entity,hazard));
    }
    public static boolean protectAndUseFilter(LivingEntity entity, ExposureProtection.Hazard hazard, int damage) {
        for(var protection:PROTECTION) if(protection.protects(entity,hazard)) {
            protection.damageFilter(entity,damage);return true;
        }
        return false;
    }
    public static boolean seesGas(LivingEntity entity) { return PROTECTION.stream().anyMatch(protection->protection.seesGas(entity)); }
    /** Ghosts keep their saved doses; only reception and radiation consequences pause. */
    public static boolean spectatorRadiationExempt(LivingEntity entity) { return entity instanceof Player player&&player.isSpectator(); }
    private static void incomingRadiationDamage(LivingIncomingDamageEvent event) {
        if(spectatorRadiationExempt(event.getEntity())&&(event.getSource().is(HbmDamageSources.radiation)||event.getSource().is(HbmDamageSources.digamma)))event.setCanceled(true);
    }
    private static void radiationEffectApplicable(MobEffectEvent.Applicable event) {
        if(spectatorRadiationExempt(event.getEntity())&&event.getEffectInstance().is(ExposureEffects.RADIATION))event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }
    public static boolean isRadiationImmune(LivingEntity entity) {
        return spectatorRadiationExempt(entity) || entity.hasEffect(ExposureEffects.MUTATION) || entity instanceof MushroomCow || entity instanceof Zombie || entity instanceof AbstractSkeleton || entity instanceof Ocelot
                || entity.getType().is(IMMUNE) || PROTECTION.stream().anyMatch(protection->protection.radiationImmune(entity));
    }
    /** Actual registered resistance exponent, shared with the original Geiger report. */
    public static float radiationResistance(LivingEntity entity) {
        if(!(entity instanceof Player))return 0;
        float resistance=entity.getUUID().equals(java.util.UUID.fromString("c95fdfd3-bea7-4255-a44b-d21bc3df95e3"))? .4F:0;
        for(var protection:PROTECTION)resistance=protection.accumulateRadiationResistance(entity,resistance);
        if(entity.hasEffect(ExposureEffects.RADX))resistance+=.2F;
        return resistance;
    }
    public static boolean usesCustomGeigerHud(LivingEntity entity) {
        return PROTECTION.stream().anyMatch(protection->protection.usesCustomGeigerHud(entity));
    }
    public static float radiationModifier(LivingEntity entity) {
        if(!(entity instanceof Player))return 1;
        return (float)Math.pow(10,-radiationResistance(entity));
    }
    public static float radiation(LivingEntity entity) { return settings.contamination()?data(entity).radiation:0; }
    public static float digamma(LivingEntity entity) { return data(entity).digamma; }
    public static void setDigamma(LivingEntity entity,float amount) {
        if(entity.level().isClientSide)return;
        float dose=Float.isFinite(amount)?Math.max(0,Math.min(amount,10)):0;
        if(spectatorRadiationExempt(entity)&&dose>=data(entity).digamma)return;
        if(entity.getType().is(DIGAMMA_IMMUNE)||PROTECTION.stream().anyMatch(protection->protection.digammaImmune(entity)))dose=0;
        data(entity).digamma=dose;
        var attribute=entity.getAttribute(Attributes.MAX_HEALTH);
        if(attribute!=null){
            attribute.removeModifier(DIGAMMA_HEALTH);
            float modifier=(float)Math.pow(.5,dose)-1F;
            attribute.addPermanentModifier(new AttributeModifier(DIGAMMA_HEALTH,modifier,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        if(entity.getHealth()>entity.getMaxHealth()&&entity.getMaxHealth()>0)entity.setHealth(entity.getMaxHealth());
        if((entity.getMaxHealth()<=0||dose>=10)&&entity.isAlive()){
            entity.setAbsorptionAmount(0);
            var damage=HbmDamageSources.source(entity.level(),HbmDamageSources.digamma);
            entity.hurt(damage,500);entity.setHealth(0);entity.die(damage);
        }
        com.hbm.advancement.HbmAdvancements.digammaChanged(entity,dose);
    }
    public static void incrementDigamma(LivingEntity entity,float amount){setDigamma(entity,digamma(entity)+amount);}
    /** Original ContaminationUtil DIGAMMA / DIGAMMA2, distinct from inventory applyDigammaData.
     * The terminal spear pulse bypasses ordinary Fau/DNS/stability and creative, but not the actual advanced robes provider. */
    public static boolean contaminateDigamma(LivingEntity entity,float amount,boolean advancedOnly) {
        if(entity.level().isClientSide)return false;
        if(entity instanceof Player player){
            if(player.isSpectator())return false;
            if(PROTECTION.stream().anyMatch(protection->protection.digammaImmune(player)))return false;
            if(!advancedOnly&&(player.hasEffect(ExposureEffects.STABILITY)||PROTECTION.stream().anyMatch(protection->protection.digammaProtected(player))))return false;
            if(player.isCreative()&&!advancedOnly)return false;
            if(player.tickCount<200)return false;
        }
        incrementDigamma(entity,amount);
        return true;
    }
    public static void applyDigammaData(LivingEntity entity,float amount){
        if(entity instanceof Ocelot||entity.getType().is(DIGAMMA_IMMUNE)||PROTECTION.stream().anyMatch(protection->protection.digammaImmune(entity)))return;
        if(entity instanceof Player player&&(player.isSpectator()||player.isCreative()||player.tickCount<200))return;
        if(entity.hasEffect(ExposureEffects.STABILITY))return;
        if(entity instanceof Player&&PROTECTION.stream().anyMatch(protection->protection.digammaProtected(entity)))return;
        incrementDigamma(entity,amount);
    }
    public static int asbestos(LivingEntity entity) { return settings.asbestos()?data(entity).asbestos:0; }
    public static int blackLung(LivingEntity entity) { return settings.coalDust()?data(entity).blackLung:0; }
    public static void setRadiation(LivingEntity entity,float amount) {
        if(settings.contamination()){
            float dose=Float.isFinite(amount)?Math.max(0,Math.min(amount,2500)):0;
            if(spectatorRadiationExempt(entity)&&dose>=data(entity).radiation)return;
            data(entity).radiation=dose;
        }
    }
    public static void incrementRadiation(LivingEntity entity,float amount) { setRadiation(entity,radiation(entity)+amount); }
    public static boolean contaminate(LivingEntity entity,Contamination mode,float amount) {
        return contaminate(entity,mode,amount,false);
    }
    /** Source HAZMAT/HAZMAT2 admission still records environment dose before the armor rejection. */
    public static boolean contaminate(LivingEntity entity,Contamination mode,float amount,boolean sourceProtected) {
        if(spectatorRadiationExempt(entity))return false;
        // Source records environment dose before armor, creative, spawn-grace and immunity rejection.
        data(entity).radEnvironment+=amount;
        if(sourceProtected)return false;
        if(entity instanceof Player player && (player.isCreative() || player.tickCount<200))return false;
        if(isRadiationImmune(entity))return false;
        incrementRadiation(entity,amount*(mode==Contamination.RAD_BYPASS?1:radiationModifier(entity)));
        return true;
    }
    public static void setAsbestos(LivingEntity entity,int amount) {
        if(!settings.asbestos())return;
        var data=data(entity);data.asbestos=Math.max(0,amount);
        if(data.asbestos>=ExposureData.MAX_ASBESTOS){data.asbestos=0;entity.hurt(HbmDamageSources.source(entity.level(),HbmDamageSources.asbestos),1000);}
    }
    public static void incrementAsbestos(LivingEntity entity,int amount) { if(!settings.asbestos())return;setAsbestos(entity,(int)Math.min(Integer.MAX_VALUE,(long)asbestos(entity)+amount));com.hbm.disease.DiseaseVisuals.warning(entity,true); }
    public static void setBlackLung(LivingEntity entity,int amount) {
        if(!settings.coalDust())return;
        var data=data(entity);data.blackLung=Math.max(0,amount);
        if(data.blackLung>=ExposureData.MAX_BLACK_LUNG){data.blackLung=0;entity.hurt(HbmDamageSources.source(entity.level(),HbmDamageSources.blacklung),1000);}
    }
    public static void incrementBlackLung(LivingEntity entity,int amount) { if(!settings.coalDust())return;setBlackLung(entity,(int)Math.min(Integer.MAX_VALUE,(long)blackLung(entity)+amount));com.hbm.disease.DiseaseVisuals.warning(entity,false); }
    private static void tick(EntityTickEvent.Post event) {
        if(!(event.getEntity() instanceof LivingEntity entity) || !(entity.level() instanceof ServerLevel level) || !entity.isAlive())return;
        boolean spectator=spectatorRadiationExempt(entity);
        float environment=0;
        float crater=spectator?0:com.hbm.bomb.nuclear.NuclearAmbientRadiation.rate(entity);
        if(!isRadiationImmune(entity)) {
            BlockPos pos=entity.blockPosition();
            environment=HbmRadiation.getRadiation(level,pos.getX(),pos.getY(),pos.getZ());
            if(level.dimensionType().ultraWarm())environment=Math.max(environment,settings.netherRadiation());
        }
        double soot=entity instanceof Player?PollutionSystem.get(level,BlockPos.containing(entity.getX(),entity.getEyeY(),entity.getZ()),PollutionType.SOOT):0;
        if(protectedFrom(entity,ExposureProtection.Hazard.PARTICLE_COARSE))soot=0;
        if(!entity.hasData(DATA)&&environment<=0&&crater<=0&&soot<=30)return;
        ExposureData data=data(entity);
        if(spectator){
            // Transient received-dose readings reset, but saved dose and existing
            // contamination countdowns stay intact until normal play resumes.
            data.radEnvironment=0;data.radBuffer=0;
        }else{
            if(entity.tickCount%20==0){data.radBuffer=data.radEnvironment;data.radEnvironment=0;}
            // Original biome exposure precedes body effects.
            if(crater>0)contaminate(entity,Contamination.CREATIVE,crater/20F);
            tickContamination(entity,data);
            radiationSickness(entity,level);
            if(environment>0)contaminate(entity,Contamination.CREATIVE,environment/20F);
            radiationSymptoms(entity,level);
        }
        lungDisease(entity,level,soot);
        if(entity instanceof Player && entity.tickCount%20==0)entity.syncData(DATA);
    }
    private static void tickContamination(LivingEntity entity,ExposureData data) {
        var iterator=data.contamination.iterator();
        while(iterator.hasNext()) {
            var effect=iterator.next();
            contaminate(entity,effect.ignoreArmor?Contamination.RAD_BYPASS:Contamination.CREATIVE,effect.getRad());
            if(--effect.time<=0)iterator.remove();
        }
    }
    private static void effect(LivingEntity entity,net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect,int ticks,int amplifier) {
        entity.addEffect(new MobEffectInstance(effect,ticks,amplifier));
    }
    private static void radiationSickness(LivingEntity entity,ServerLevel level) {
        if(entity instanceof Player player&&(player.isSpectator()||player.isCreative()))return;
        float dose=radiation(entity);
        if(com.hbm.entity.mob.sourcecreepers.SourceCreeperContent.transformRadiation(entity,level,dose))return;
        if(com.hbm.entity.mob.duck.DuckContent.transformRadiation(entity,level,dose))return;
        if(dose<200||isRadiationImmune(entity))return;
        if(dose>=1000) {
            var damage=HbmDamageSources.source(level,HbmDamageSources.radiation);
            entity.hurt(damage,1000);setRadiation(entity,0);
            if(entity.getHealth()>0&&!(entity instanceof Player)){entity.setHealth(0);entity.die(damage);}
            com.hbm.advancement.HbmAdvancements.radiationDeath(entity);
            return;
        }
        var random=level.random;
        if(dose>=600) {
            if(random.nextInt(300)==0)effect(entity,MobEffects.CONFUSION,150,0);
            if(random.nextInt(300)==0)effect(entity,MobEffects.MOVEMENT_SLOWDOWN,200,2);
            if(random.nextInt(300)==0)effect(entity,MobEffects.WEAKNESS,200,2);
            if(random.nextInt(500)==0)effect(entity,MobEffects.POISON,60,dose>=800?2:1);
            if(dose>=800&&random.nextInt(700)==0)effect(entity,MobEffects.WITHER,60,1);
        }else if(dose>=400) {
            if(random.nextInt(300)==0)effect(entity,MobEffects.CONFUSION,150,0);
            if(random.nextInt(500)==0)effect(entity,MobEffects.MOVEMENT_SLOWDOWN,100,0);
            if(random.nextInt(300)==0)effect(entity,MobEffects.WEAKNESS,100,1);
        }else {
            if(random.nextInt(300)==0)effect(entity,MobEffects.CONFUSION,100,0);
            if(random.nextInt(500)==0)effect(entity,MobEffects.WEAKNESS,100,0);
            com.hbm.advancement.HbmAdvancements.radiationPoison(entity);
        }
    }
    /** Source handleRadiationFX: entity-ID seeded windows, distinct normal/blood vomit and hunger. */
    private static void radiationSymptoms(LivingEntity entity,ServerLevel level){
        if(isRadiationImmune(entity)||entity instanceof Player player&&player.isCreative())return;
        var random=new java.util.Random(entity.getId());int r600=random.nextInt(600),r1200=random.nextInt(1200);float dose=radiation(entity);
        if(entity instanceof net.minecraft.world.entity.animal.WaterAnimal)return;
        int period=dose>600?600:1200,offset=dose>600?r600:r1200;
        if(dose<=200||(level.getGameTime()+offset)%period>=20)return;
        com.hbm.disease.DiseaseVisuals.vomit(entity,dose>600?com.hbm.disease.DiseaseVisuals.BLOOD:com.hbm.disease.DiseaseVisuals.NORMAL,dose>600?25:15);
        if((level.getGameTime()+offset)%period==1){var pos=entity.blockPosition();level.playSound(null,pos.getX(),pos.getY(),pos.getZ(),HbmSounds.get("player.vomit"),SoundSource.NEUTRAL,1,1);effect(entity,MobEffects.HUNGER,60,19);}
    }
    private static void lungDisease(LivingEntity entity,ServerLevel level,double soot) {
        if(entity instanceof Player player&&player.isCreative()){setAsbestos(entity,0);setBlackLung(entity,0);return;}
        int black=blackLung(entity);
        if(black>0&&black<ExposureData.MAX_BLACK_LUNG*.5)setBlackLung(entity,black-1);
        double coal=Math.min(blackLung(entity),ExposureData.MAX_BLACK_LUNG),asbestos=Math.min(asbestos(entity),ExposureData.MAX_ASBESTOS);
        if(coal/ExposureData.MAX_BLACK_LUNG<=.25&&asbestos/ExposureData.MAX_ASBESTOS<=.25&&soot<=30)return;
        double coalRemaining=1-coal/ExposureData.MAX_BLACK_LUNG,asbestosRemaining=1-asbestos/ExposureData.MAX_ASBESTOS;
        double total=1-coalRemaining*asbestosRemaining;
        if(total>.75)effect(entity,MobEffects.WEAKNESS,100,2);
        if(total>.95)effect(entity,MobEffects.CONFUSION,100,0);
        total=1-coalRemaining*asbestosRemaining*(1-Math.min(soot/100,1));
        int frequency=Math.max((int)(1000-950*total),20);
        if(level.getGameTime()%frequency==entity.getId()%frequency){
            level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("player.cough"),SoundSource.PLAYERS,1,1);
            if(asbestos/ExposureData.MAX_ASBESTOS>.75||coal/ExposureData.MAX_BLACK_LUNG>.75)com.hbm.disease.DiseaseVisuals.vomit(entity,com.hbm.disease.DiseaseVisuals.BLOOD,5);
            if(coal/ExposureData.MAX_BLACK_LUNG>.5)com.hbm.disease.DiseaseVisuals.vomit(entity,com.hbm.disease.DiseaseVisuals.SMOKE,coal/ExposureData.MAX_BLACK_LUNG>.8?50:10);
        }
    }
}
