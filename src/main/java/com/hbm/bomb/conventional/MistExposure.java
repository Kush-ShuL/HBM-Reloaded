package com.hbm.bomb.conventional;

import com.hbm.armor.GasMaskFilters;
import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.fluid.FluidTraits;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.*;

/** Original mist toxin protection, ArmorUtil's exact suit whitelist, and the shared HbmLivingProps oil state. */
public final class MistExposure {
    private static final EquipmentSlot[] SLOTS={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final List<String[]> SUITS=List.of(
        new String[]{"hazmat_helmet","hazmat_plate","hazmat_legs","hazmat_boots"},
        new String[]{"hazmat_helmet_red","hazmat_plate_red","hazmat_legs_red","hazmat_boots_red"},
        new String[]{"hazmat_helmet_grey","hazmat_plate_grey","hazmat_legs_grey","hazmat_boots_grey"},
        new String[]{"schrabidium_helmet","schrabidium_plate","schrabidium_legs","schrabidium_boots"},
        new String[]{"hazmat_paa_helmet","hazmat_paa_plate","hazmat_paa_legs","hazmat_paa_boots"},
        new String[]{"liquidator_helmet","liquidator_plate","liquidator_legs","liquidator_boots"},
        new String[]{"euphemium_helmet","euphemium_plate","euphemium_legs","euphemium_boots"},
        new String[]{"rpa_helmet","rpa_plate","rpa_legs","rpa_boots"},
        new String[]{"fau_helmet","fau_plate","fau_legs","fau_boots"},
        new String[]{"dns_helmet","dns_plate","dns_legs","dns_boots"});
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    private static final DeferredHolder<AttachmentType<?>,AttachmentType<OilData>> OIL=ATTACHMENTS.register("living_oil",()->AttachmentType.serializable(OilData::new).build());
    private MistExposure(){}
    public static void register(IEventBus bus){ATTACHMENTS.register(bus);bus.addListener(OilDripEffects::register);NeoForge.EVENT_BUS.addListener(MistExposure::tick);}
    public static int oil(LivingEntity entity){return entity.hasData(OIL)?entity.getData(OIL).ticks:0;}
    public static void setOil(LivingEntity entity,int ticks){entity.getData(OIL).ticks=ticks;}
    public static void damageSuit(LivingEntity entity,int slot,int amount){var equipment=SLOTS[slot];var stack=entity.getItemBySlot(equipment);if(!stack.isEmpty())stack.hurtAndBreak(amount,entity,equipment);}
    public static boolean checkForHazmat(LivingEntity entity){
        for(var suit:SUITS){boolean complete=true;for(int i=0;i<4;i++){var id=ResourceLocation.fromNamespaceAndPath("hbm",suit[i]);var stack=entity.getItemBySlot(SLOTS[3-i]);if(stack.isEmpty()||!BuiltInRegistries.ITEM.containsKey(id)||stack.getItem()!=BuiltInRegistries.ITEM.get(id)){complete=false;break;}}if(complete)return true;}
        return entity.hasEffect(ExposureEffects.MUTATION);
    }
    private static boolean protectedFrom(LivingEntity entity,String maskType,boolean fullBody){boolean hasMask=maskType==null||maskType.isEmpty()||maskType.equals("null");if(!hasMask&&HazmatRegistry.protection(entity.getItemBySlot(EquipmentSlot.HEAD),entity).contains(Hazard.valueOf(maskType))){GasMaskFilters.damage(entity,1);hasMask=true;}return hasMask&&(!fullBody||checkForHazmat(entity));}
    public static void affectToxin(LivingEntity entity,FluidTraits.Toxin toxin,double intensity){for(var entry:toxin.entries()){
        if(entry instanceof FluidTraits.DirectDamage damage){if(protectedFrom(entity,damage.maskType(),damage.fullBody()))continue;if(damage.delay()==0||entity.level().getGameTime()%damage.delay()==0)entity.hurt(source(entity.level(),damage.source()),(float)(damage.amount()*intensity));}
        else if(entry instanceof FluidTraits.Effects effects){if(protectedFrom(entity,effects.maskType(),effects.fullBody()))continue;for(var effect:effects.effects())entity.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(ResourceKey.create(Registries.MOB_EFFECT,ResourceLocation.parse(effect.resourceName()))),(int)(effect.duration()*intensity),effect.amplifier()));}
    }}
    public static DamageSource source(Level level,String id){var registry=level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);var location=ResourceLocation.parse(id);var key=ResourceKey.create(Registries.DAMAGE_TYPE,location);var registered=registry.getHolder(key);if(registered.isPresent())return new DamageSource(registered.get());return new NamedSource(registry.getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,ResourceLocation.parse("hbm:mist_toxin"))),id);}
    private static final class NamedSource extends DamageSource {
        private final String name;
        NamedSource(net.minecraft.core.Holder<DamageType> type,String name){super(type);this.name=name;}
        @Override public String getMsgId(){return name;}
        @Override public Component getLocalizedDeathMessage(LivingEntity victim){var credit=victim.getKillCredit();String key="death.attack."+name;return credit==null?Component.translatable(key,victim.getDisplayName()):Component.translatable(key+".player",victim.getDisplayName(),credit.getDisplayName());}
    }
    private static void tick(EntityTickEvent.Post event){if(!(event.getEntity() instanceof LivingEntity entity)||entity.level().isClientSide)return;int oil=oil(entity);if(oil<=0)return;if(entity.isOnFire()){setOil(entity,0);entity.level().explode(null,entity.getX(),entity.getY()+entity.getBbHeight()/2,entity.getZ(),3,false,Level.ExplosionInteraction.TNT);}else setOil(entity,oil-1);if(entity.tickCount%5==0)OilDripEffects.send(entity);}
    public static final class OilData implements INBTSerializable<CompoundTag>{int ticks;@Override public CompoundTag serializeNBT(HolderLookup.Provider lookup){var tag=new CompoundTag();tag.putInt("hfr_oil",ticks);return tag;}@Override public void deserializeNBT(HolderLookup.Provider lookup,CompoundTag tag){ticks=tag.getInt("hfr_oil");}}
}
