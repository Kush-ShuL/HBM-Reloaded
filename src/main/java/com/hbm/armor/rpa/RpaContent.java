package com.hbm.armor.rpa;
import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.registry.HbmContent;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Source five prerequisite items and their genuine two PA controllers. */
public final class RpaContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<ArmorMaterial>MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
 /** The original material is shared with future AJR/AJRO/NCRPA; those families must reuse this same holder. */
 public static final DeferredHolder<ArmorMaterial,ArmorMaterial> AJR=MATERIALS.register("source_ajr",()->new ArmorMaterial(Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),0,BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("plate_armor_ajr")),List.of(new ArmorMaterial.Layer(id("starmetal"))),0,0));
 public static final DeferredItem<OrbitalAssemblyItem> ORBITAL=ITEMS.register("orbital_assembly",OrbitalAssemblyItem::new);
 public static final DeferredItem<ArmorRpa> HELMET=ITEMS.register("rpa_helmet",()->new ArmorRpa(AJR,ArmorItem.Type.HELMET)),PLATE=ITEMS.register("rpa_plate",()->new ArmorRpa(AJR,ArmorItem.Type.CHESTPLATE)),LEGS=ITEMS.register("rpa_legs",()->new ArmorRpa(AJR,ArmorItem.Type.LEGGINGS)),BOOTS=ITEMS.register("rpa_boots",()->new ArmorRpa(AJR,ArmorItem.Type.BOOTS));
 public static final DeferredItem<ItemGunBaseNT> MELEE=ITEMS.register("gun_pa_melee",PowerArmorGuns::melee),RANGED=ITEMS.register("gun_pa_ranged",PowerArmorGuns::ranged);
 public static ResourceLocation id(String value){return ResourceLocation.fromNamespaceAndPath("hbm",value);}
 public static void register(IEventBus bus,ModContainer container){ITEMS.register(bus);MATERIALS.register(bus);container.registerConfig(ModConfig.Type.SERVER,RpaSettings.SPEC,"hbm-pa-weapons-server.toml");bus.addListener(RpaContent::complete);bus.addListener(RpaContent::creative);}
 private static void complete(FMLLoadCompleteEvent event){event.enqueueWork(()->HazmatRegistry.registerHazards(HELMET.get(),List.of(Hazard.PARTICLE_COARSE,Hazard.PARTICLE_FINE,Hazard.GAS_LUNG,Hazard.BACTERIA,Hazard.GAS_BLISTERING,Hazard.GAS_MONOXIDE,Hazard.LIGHT,Hazard.SAND)));}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?List.of(new ItemStack(ORBITAL.get())):tab.equals("weapon")?List.of(new ItemStack(MELEE.get()),new ItemStack(RANGED.get())):List.of();}
 private RpaContent(){}
}
