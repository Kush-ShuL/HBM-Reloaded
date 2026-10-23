package com.hbm.armor.ncrpa;
import com.hbm.armor.HazmatRegistry;
import com.hbm.armor.rpa.RpaContent;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Original four items share the already-real AJR material and source PA controllers, never a second registry. */
public final class NcrContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ArmorNcrpa> HELMET=ITEMS.register("ncrpa_helmet",()->new ArmorNcrpa(RpaContent.AJR,ArmorItem.Type.HELMET)),PLATE=ITEMS.register("ncrpa_plate",()->new ArmorNcrpa(RpaContent.AJR,ArmorItem.Type.CHESTPLATE)),LEGS=ITEMS.register("ncrpa_legs",()->new ArmorNcrpa(RpaContent.AJR,ArmorItem.Type.LEGGINGS)),BOOTS=ITEMS.register("ncrpa_boots",()->new ArmorNcrpa(RpaContent.AJR,ArmorItem.Type.BOOTS));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus,ModContainer container){ITEMS.register(bus);NcrRocketBullets.init();container.registerConfig(ModConfig.Type.SERVER,NcrSettings.SPEC,"hbm-ncr-pa-weapons-server.toml");bus.addListener(NcrContent::complete);bus.addListener(NcrContent::creative);}
 private static void complete(FMLLoadCompleteEvent event){event.enqueueWork(()->HazmatRegistry.registerHazards(HELMET.get(),List.of(Hazard.PARTICLE_COARSE,Hazard.PARTICLE_FINE,Hazard.GAS_LUNG,Hazard.BACTERIA,Hazard.GAS_BLISTERING,Hazard.GAS_MONOXIDE,Hazard.LIGHT,Hazard.SAND)));}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
 private NcrContent(){}
}
