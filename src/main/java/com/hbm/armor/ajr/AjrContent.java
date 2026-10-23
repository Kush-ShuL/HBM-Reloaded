package com.hbm.armor.ajr;
import com.hbm.armor.HazmatRegistry;
import com.hbm.armor.rpa.RpaContent;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Two genuine source classes/families, eight identities and exactly the original one shared AJR material. */
public final class AjrContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ArmorAjr> HELMET=ITEMS.register("ajr_helmet",()->new ArmorAjr(RpaContent.AJR,ArmorItem.Type.HELMET)),PLATE=ITEMS.register("ajr_plate",()->new ArmorAjr(RpaContent.AJR,ArmorItem.Type.CHESTPLATE)),LEGS=ITEMS.register("ajr_legs",()->new ArmorAjr(RpaContent.AJR,ArmorItem.Type.LEGGINGS)),BOOTS=ITEMS.register("ajr_boots",()->new ArmorAjr(RpaContent.AJR,ArmorItem.Type.BOOTS));
 public static final DeferredItem<ArmorAjro> O_HELMET=ITEMS.register("ajro_helmet",()->new ArmorAjro(RpaContent.AJR,ArmorItem.Type.HELMET)),O_PLATE=ITEMS.register("ajro_plate",()->new ArmorAjro(RpaContent.AJR,ArmorItem.Type.CHESTPLATE)),O_LEGS=ITEMS.register("ajro_legs",()->new ArmorAjro(RpaContent.AJR,ArmorItem.Type.LEGGINGS)),O_BOOTS=ITEMS.register("ajro_boots",()->new ArmorAjro(RpaContent.AJR,ArmorItem.Type.BOOTS));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(AjrContent::complete);bus.addListener(AjrContent::creative);}
 private static void complete(FMLLoadCompleteEvent event){event.enqueueWork(()->{var hazards=List.of(Hazard.PARTICLE_COARSE,Hazard.PARTICLE_FINE,Hazard.GAS_LUNG,Hazard.BACTERIA,Hazard.GAS_BLISTERING,Hazard.GAS_MONOXIDE,Hazard.LIGHT,Hazard.SAND);HazmatRegistry.registerHazards(HELMET.get(),hazards);HazmatRegistry.registerHazards(O_HELMET.get(),hazards);});}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS,O_HELMET,O_PLATE,O_LEGS,O_BOOTS))event.accept(item.get());}
 private AjrContent(){}
}
