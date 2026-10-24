package com.hbm.item.medicine;

import com.hbm.damage.HbmDamageSources;
import com.hbm.exposure.ExposureProtection;
import com.hbm.exposure.HbmExposure;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Original MKU timer, armor/item and aerial contagion, damage amplification and symptoms. */
public final class MedicineDisease {
    private static final int MINUTE=60*20,HOUR=60*MINUTE;
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS=DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,"hbm");
    public static final DeferredHolder<AttachmentType<?>,AttachmentType<DiseaseData>> DATA=ATTACHMENTS.register("medicine_disease",()->AttachmentType.serializable(DiseaseData::new).sync((holder,player)->holder==player,net.minecraft.network.codec.StreamCodec.of((buffer,data)->buffer.writeInt(data.contagion),buffer->{var data=new DiseaseData();data.contagion=buffer.readInt();return data;})).build());
    private static final EquipmentSlot[] ARMOR={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final List<String> SEALED_SETS=List.of("hazmat_paa","liquidator","euphemium","rpa","fau","dns");
    private MedicineDisease() {}
    static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);NeoForge.EVENT_BUS.addListener(MedicineDisease::tick);NeoForge.EVENT_BUS.addListener(MedicineDisease::drops);
    }
    public static int contagion(LivingEntity entity) {return MedicineConfig.ENABLE_MKU.get()&&entity.hasData(DATA)?entity.getData(DATA).contagion:0;}
    public static void setContagion(LivingEntity entity,int ticks) {entity.getData(DATA).contagion=ticks;if(entity instanceof net.minecraft.server.level.ServerPlayer)entity.syncData(DATA);}
    public static boolean contaminated(ItemStack stack) {return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("ntmContagion");}
    public static void contaminate(ItemStack stack) {CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putBoolean("ntmContagion",true));}
    public static void decontaminate(ItemStack stack) {CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.remove("ntmContagion"));}
    private static boolean sealed(LivingEntity entity) {
        if(!HbmExposure.protectedFrom(entity,ExposureProtection.Hazard.BACTERIA))return false;
        for(String set:SEALED_SETS) {
            String[] suffix={"boots","legs","plate","helmet"};boolean match=true;
            for(int i=0;i<4;i++) {
                var item=BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("hbm",set+"_"+suffix[i]));
                if(item.isEmpty()||!entity.getItemBySlot(ARMOR[i]).is(item.get())){match=false;break;}
            }
            if(match)return true;
        }
        return false;
    }
    private static void tick(EntityTickEvent.Post event) {
        if(!(event.getEntity() instanceof LivingEntity entity)||!(entity.level() instanceof ServerLevel level)||!MedicineConfig.ENABLE_MKU.get())return;
        var random=entity.getRandom();int contagion=contagion(entity);
        if(entity instanceof Player player) {
            ItemStack stack=player.getInventory().getItem(random.nextInt(player.getInventory().items.size()));
            if(random.nextInt(100)==0)stack=player.getItemBySlot(ARMOR[random.nextInt(4)]);
            if(!stack.isEmpty()&&stack.getMaxStackSize()==1) {
                if(contagion>0)contaminate(stack);
                else if(contaminated(stack)&&!sealed(entity))setContagion(entity,3*HOUR);
            }
        }
        if(contagion<=0)return;
        setContagion(entity,contagion-1);
        if(contagion<2*HOUR+55*MINUTE&&contagion%20==0) {
            double range=entity.isInWaterOrRain()?16:2;
            for(var nearby:level.getEntities(entity,entity.getBoundingBox().inflate(range))) {
                if(nearby instanceof LivingEntity living&&contagion(living)<=0&&!sealed(living))setContagion(living,3*HOUR);
                if(nearby instanceof ItemEntity item)contaminate(item.getItem());
            }
        }
        if(contagion<2*HOUR&&random.nextInt(1000)==0)entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,20,0));
        if(contagion<HOUR&&random.nextInt(100)==0) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,60,0));entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,300,4));
        }
        if(contagion<30*MINUTE&&random.nextInt(400)==0)entity.hurt(HbmDamageSources.source(level,HbmDamageSources.mku),1);
        if(contagion<5*MINUTE&&random.nextInt(100)==0)entity.hurt(HbmDamageSources.source(level,HbmDamageSources.mku),2);
        if(contagion<30*MINUTE&&(contagion+entity.getId())%200<20&&!(entity instanceof WaterAnimal)) {
            MedicineVisuals.vomit(entity);
            if((contagion+entity.getId())%200==19)level.playSound(null,entity.getX(),entity.getY(),entity.getZ(),com.hbm.registry.HbmSounds.get("player.vomit"),net.minecraft.sounds.SoundSource.NEUTRAL,1,1);
        }
        // The original final lethal check tests the unchanged positive local timer, so it is unreachable.
        // Preserve that behavior rather than turning the source comment into a new lethal effect.
    }
    private static void drops(LivingDropsEvent event) {
        if(!event.getEntity().level().isClientSide&&contagion(event.getEntity())>0)event.getDrops().forEach(item->contaminate(item.getItem()));
    }
    /** Source damage order: after player shield absorption, before every armor mod callback. */
    public static void applyDamageAmplification(LivingIncomingDamageEvent event) {
        if(contagion(event.getEntity())>0&&event.getAmount()<100)event.setAmount(event.getAmount()*2);
    }
    public static final class DiseaseData implements INBTSerializable<CompoundTag> {
        int contagion;
        @Override public CompoundTag serializeNBT(HolderLookup.Provider lookup) {var tag=new CompoundTag();if(MedicineConfig.ENABLE_MKU.get())tag.putInt("hfr_contagion",contagion);return tag;}
        @Override public void deserializeNBT(HolderLookup.Provider lookup,CompoundTag tag) {if(MedicineConfig.ENABLE_MKU.get())contagion=tag.getInt("hfr_contagion");}
    }
}
