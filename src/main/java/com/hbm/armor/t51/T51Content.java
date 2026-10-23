package com.hbm.armor.t51;
import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
public final class T51Content {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<ArmorMaterial>MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
 private static ArmorMaterial material(int enchant,String repair,String texture){return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),enchant,BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item(repair)),List.of(new ArmorMaterial.Layer(id(texture))),0,0);}
 public static final DeferredHolder<ArmorMaterial,ArmorMaterial>T51=MATERIALS.register("t51",()->material(0,"plate_armor_titanium","t51"));
 public static final DeferredHolder<ArmorMaterial,ArmorMaterial>TITANIUM=MATERIALS.register("titanium",()->material(9,"ingot_titanium","titanium"));
 public static final DeferredItem<ArmorT51>HELMET=ITEMS.register("t51_helmet",()->new ArmorT51(T51,ArmorItem.Type.HELMET)),PLATE=ITEMS.register("t51_plate",()->new ArmorT51(T51,ArmorItem.Type.CHESTPLATE)),LEGS=ITEMS.register("t51_legs",()->new ArmorT51(T51,ArmorItem.Type.LEGGINGS)),BOOTS=ITEMS.register("t51_boots",()->new ArmorT51(T51,ArmorItem.Type.BOOTS));
 public static final DeferredItem<SourceFullSetArmorItem>TI_HELMET=ITEMS.register("titanium_helmet",()->new SourceFullSetArmorItem(TITANIUM,ArmorItem.Type.HELMET,25)),TI_PLATE=ITEMS.register("titanium_plate",()->new SourceFullSetArmorItem(TITANIUM,ArmorItem.Type.CHESTPLATE,25)),TI_LEGS=ITEMS.register("titanium_legs",()->new SourceFullSetArmorItem(TITANIUM,ArmorItem.Type.LEGGINGS,25)),TI_BOOTS=ITEMS.register("titanium_boots",()->new SourceFullSetArmorItem(TITANIUM,ArmorItem.Type.BOOTS,25));
 public static final DeferredItem<ItemFusionCore>CORE=ITEMS.register("fusion_core_item",()->new ItemFusionCore(2_500_000,1));
 public static final DeferredItem<ItemModBattery>BATTERY=ITEMS.register("armor_battery",()->new ItemModBattery(1.25)),BATTERY_MK2=ITEMS.register("armor_battery_mk2",()->new ItemModBattery(1.5)),BATTERY_MK3=ITEMS.register("armor_battery_mk3",()->new ItemModBattery(2));
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
 public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(T51Content::complete);bus.addListener(T51Content::creative);T51Runtime.register();T51Network.register(bus);}
 private static void complete(FMLLoadCompleteEvent event){event.enqueueWork(()->HazmatRegistry.registerHazards(HELMET.get(),List.of(Hazard.PARTICLE_COARSE,Hazard.PARTICLE_FINE,Hazard.GAS_LUNG,Hazard.BACTERIA,Hazard.GAS_BLISTERING,Hazard.GAS_MONOXIDE,Hazard.SAND)));}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS,TI_HELMET,TI_PLATE,TI_LEGS,TI_BOOTS))event.accept(item.get());}
 public static List<ItemStack>creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(CORE.get())):tab.equals("consumable")?List.of(new ItemStack(BATTERY.get()),new ItemStack(BATTERY_MK2.get()),new ItemStack(BATTERY_MK3.get())):List.of();}
 private T51Content(){}
}
