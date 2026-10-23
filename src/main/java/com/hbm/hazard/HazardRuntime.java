package com.hbm.hazard;

import com.hbm.exposure.ExposureProtection;
import com.hbm.exposure.HbmExposure;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Source hazard effects and actual event scheduling. No extra radiation aura is invented for drops. */
public final class HazardRuntime {
    @FunctionalInterface public interface ArmorMods { Iterable<ItemStack> modifications(ItemStack armor,LivingEntity holder); }
    private static final List<ArmorMods> ARMOR_MODS=new CopyOnWriteArrayList<>();
    private static final EquipmentSlot[] SOURCE_EQUIPMENT={EquipmentSlot.MAINHAND,EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final ResourceLocation REACHER=ResourceLocation.fromNamespaceAndPath("hbm","reacher");
    private static HazardSettings settings=HazardSettings.DEFAULT;
    private static boolean registered;
    private HazardRuntime(){}
    public static HazardSettings settings(){var value=settings;return new HazardSettings(value.hot(),value.blinding(),value.hydroactive(),value.explosive(),com.hbm.config.GeneralModes.mode528(),com.hbm.config.GeneralModes.lbsm(),com.hbm.config.GeneralModes.flag("LBSM_safeCrates"),com.hbm.config.GeneralModes.flag("LBSM_safeMEDrives"),value.droppedItemTickRate(),value.offhand());}
    public static void configure(HazardSettings settings){HazardRuntime.settings=Objects.requireNonNull(settings);}
    public static void addArmorModProvider(ArmorMods provider){ARMOR_MODS.add(Objects.requireNonNull(provider));}
    public static void removeArmorModProvider(ArmorMods provider){ARMOR_MODS.remove(provider);}
    static void register(){
        if(registered)return;registered=true;
        ARMOR_MODS.add(HazardArmorMods::read);
        NeoForge.EVENT_BUS.addListener(HazardRuntime::playerStart);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH,HazardRuntime::armorBeforeEffects);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW,HazardRuntime::livingAfterEffects);
        NeoForge.EVENT_BUS.addListener(HazardRuntime::worldEnd);
    }
    private static void playerStart(PlayerTickEvent.Pre event){
        Player player=event.getEntity();if(player.level().isClientSide)return;
        HazardContext.withLookup(player.level().registryAccess(),()->{
            for(int i=0;i<player.getInventory().items.size();i++){
                ItemStack stack=player.getInventory().items.get(i);
                if(!stack.isEmpty()){applyHazards(stack,player);if(stack.getCount()<=0)player.getInventory().items.set(i,ItemStack.EMPTY);}
            }
            for(ItemStack stack:player.getInventory().armor)if(!stack.isEmpty())applyHazards(stack,player);
            if(settings.offhand())for(ItemStack stack:player.getInventory().offhand)if(!stack.isEmpty())applyHazards(stack,player);
            return null;
        });
    }
    private static void armorBeforeEffects(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof LivingEntity living)||living.level().isClientSide)return;
        HazardContext.withLookup(living.level().registryAccess(),()->{
            for(ItemStack armor:living.getArmorSlots())for(var provider:ARMOR_MODS)
                for(ItemStack mod:provider.modifications(armor,living))if(!mod.isEmpty())applyHazards(mod,living);
            return null;
        });
    }
    private static void livingAfterEffects(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof LivingEntity living)||living instanceof Player||living.level().isClientSide)return;
        HazardContext.withLookup(living.level().registryAccess(),()->{
            for(var slot:SOURCE_EQUIPMENT){var stack=living.getItemBySlot(slot);if(!stack.isEmpty())applyHazards(stack,living);}
            if(settings.offhand()&&!living.getOffhandItem().isEmpty())applyHazards(living.getOffhandItem(),living);
            return null;
        });
    }
    private static void worldEnd(LevelTickEvent.Post event){
        if(!(event.getLevel() instanceof ServerLevel level)||level.getGameTime()%settings.droppedItemTickRate()!=0)return;
        List<ItemEntity> items=new ArrayList<>();
        for(Entity entity:level.getAllEntities())if(entity instanceof ItemEntity item)items.add(item);
        HazardContext.withLookup(level.registryAccess(),()->{for(ItemEntity item:items)updateDroppedItem(item);return null;});
    }
    public static void applyHazards(ItemStack stack,LivingEntity holder){
        if(holder.level().isClientSide||stack.isEmpty())return;
        var registry=HbmHazards.registry();
        for(var hazard:registry.entries(HazardStack.from(stack))){
            var evaluated=hazard.evaluate(HazardStack.from(stack));
            if(evaluated.level()!=null)apply(evaluated.type(),evaluated.level(),stack,holder);
        }
    }
    private static boolean wet(Entity entity){return entity.isInWaterRainOrBubble();}
    private static boolean hasReacher(LivingEntity holder){
        if(!(holder instanceof Player player)||!BuiltInRegistries.ITEM.containsKey(REACHER))return false;
        var reacher=BuiltInRegistries.ITEM.get(REACHER);
        for(var stack:player.getInventory().items)if(stack.is(reacher))return true;
        return false;
    }
    private static void apply(HazardType type,float level,ItemStack stack,LivingEntity target){
        switch(type){
            case RADIATION->{
                level*=stack.getCount();if(level<=0)return;float rad=level/20F;
                if(hasReacher(target))rad=com.hbm.config.GeneralModes.mode528()?rad/49F:(float)HazardContainerRadiation.squirt(rad);
                HbmExposure.contaminate(target,HbmExposure.Contamination.CREATIVE,rad);
            }
            case HOT->{if(settings.hot()&&!wet(target)&&level>0&&(com.hbm.config.GeneralModes.mode528()||!hasReacher(target)))target.igniteForSeconds((int)Math.ceil(level));}
            case BLINDING->{if(settings.blinding()&&!HbmExposure.protectedFrom(target,ExposureProtection.Hazard.LIGHT))target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,(int)Math.ceil(level),0));}
            case ASBESTOS->{
                if(!HbmExposure.settings().asbestos())return;
                if(!HbmExposure.protectAndUseFilter(target,ExposureProtection.Hazard.PARTICLE_FINE,(int)level))HbmExposure.incrementAsbestos(target,(int)Math.min(level,10));
            }
            case COAL->{
                if(!HbmExposure.settings().coalDust())return;
                if(!HbmExposure.protectedFrom(target,ExposureProtection.Hazard.PARTICLE_COARSE))HbmExposure.incrementBlackLung(target,(int)Math.min(level*stack.getCount(),10));
                else if(target.getRandom().nextInt(Math.max(65-stack.getCount(),1))==0)HbmExposure.protectAndUseFilter(target,ExposureProtection.Hazard.PARTICLE_COARSE,(int)level);
            }
            case DIGAMMA->HbmExposure.applyDigammaData(target,level/20F);
            case HYDROACTIVE->{if(settings.hydroactive()&&wet(target)&&stack.getCount()>0){stack.setCount(0);explode(target,level,target.getEyeY());}}
            case EXPLOSIVE->{if(settings.explosive()&&target.isOnFire()&&stack.getCount()>0){stack.setCount(0);explode(target,level,target.getEyeY());}}
        }
    }
    private static void explode(Entity entity,float level,double y){
        entity.level().explode(null,entity.getX(),y,entity.getZ(),level,false,Level.ExplosionInteraction.BLOCK);
    }
    public static void updateDroppedItem(ItemEntity item){
        if(item.isRemoved()||item.getItem().isEmpty())return;
        ItemStack stack=item.getItem();
        for(var hazard:HbmHazards.registry().evaluate(stack).entries()){
            if(hazard.level()==null)continue;
            if(hazard.type()==HazardType.EXPLOSIVE&&settings.explosive()&&item.isOnFire()){
                item.discard();explode(item,hazard.level(),item.getY()+item.getBbHeight()*.5);
            }
            if(hazard.type()==HazardType.HYDROACTIVE&&settings.hydroactive()&&(wet(item)||item.level().getFluidState(item.blockPosition()).is(FluidTags.WATER))){
                item.discard();explode(item,hazard.level(),item.getY()+item.getBbHeight()*.5);
            }
        }
    }
}
