package com.hbm.armor.taurun;

import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Source ModItemsArmor310..318. Identity fields are shared with the actual Undead Soldier. */
public final class TaurunContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("taurun",()->new ArmorMaterial(
        Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),10,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("plate_iron")),
        List.of(new ArmorMaterial.Layer(id("taurun"))),0,0));
    public static final DeferredItem<ArmorTaurun> HELMET=ITEMS.register("taurun_helmet",()->new ArmorTaurun(MATERIAL,ArmorItem.Type.HELMET));
    public static final DeferredItem<ArmorTaurun> PLATE=ITEMS.register("taurun_plate",()->new ArmorTaurun(MATERIAL,ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<ArmorTaurun> LEGS=ITEMS.register("taurun_legs",()->new ArmorTaurun(MATERIAL,ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<ArmorTaurun> BOOTS=ITEMS.register("taurun_boots",()->new ArmorTaurun(MATERIAL,ArmorItem.Type.BOOTS));
    public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
    public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(TaurunContent::complete);bus.addListener(TaurunContent::creative);NeoForge.EVENT_BUS.addListener(TaurunSuitEffects::tick);}
    private static void complete(FMLLoadCompleteEvent event){event.enqueueWork(()->{
        // HazmatContent.initialize clears the one shared registry during common setup.
        // The original cloneStats does NOT copy hazard classes to the other three pieces.
        HazmatRegistry.registerHazards(HELMET.get(),List.of(Hazard.PARTICLE_COARSE,Hazard.PARTICLE_FINE,Hazard.GAS_LUNG,Hazard.BACTERIA,Hazard.GAS_BLISTERING,Hazard.GAS_MONOXIDE,Hazard.LIGHT,Hazard.SAND));
    });}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT)){event.accept(HELMET.get());event.accept(PLATE.get());event.accept(LEGS.get());event.accept(BOOTS.get());}}
    private TaurunContent(){}
}
