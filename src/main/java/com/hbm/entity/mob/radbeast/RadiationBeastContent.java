package com.hbm.entity.mob.radbeast;

import java.util.List;
import com.hbm.exposure.HbmExposure;
import com.hbm.exposure.ExposureProtection;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class RadiationBeastContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityRADBeast>> ELEMENTAL=ENTITIES.register("entity_ntm_radiation_blaze",()->EntityType.Builder.<EntityRADBeast>of(EntityRADBeast::new,MobCategory.MONSTER).sized(.6F,1.8F).eyeHeight(1.8F*.85F).fireImmune().clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_ntm_radiation_blaze"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("radiation_elemental_spawn_egg",()->new DeferredSpawnEggItem(ELEMENTAL,0x303030,0x008000,new Item.Properties()));
    public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);ElementalEncounters.register();HbmExposure.addProtection(new ExposureProtection(){@Override public boolean radiationImmune(LivingEntity entity){return entity instanceof EntityRADBeast;}});bus.addListener((EntityAttributeCreationEvent event)->event.put(ELEMENTAL.get(),EntityRADBeast.attributes().build()));bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());});}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EGG.get()));}
    private RadiationBeastContent(){}
}
