package com.hbm.armor.liquidator;

import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Original four ArmorLiquidator IDs; protection remains the unique canonical Hazmat/Resistance tables. */
public final class LiquidatorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("liquidator",()->new ArmorMaterial(
        Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),10,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("plate_lead")),List.of(new ArmorMaterial.Layer(id("liquidator"))),0,0));
    public static final DeferredItem<ArmorLiquidatorMask> HELMET=ITEMS.register("liquidator_helmet",ArmorLiquidatorMask::new);
    public static final DeferredItem<ArmorLiquidator> PLATE=ITEMS.register("liquidator_plate",()->new ArmorLiquidator(ArmorItem.Type.CHESTPLATE,"liquidator_plate"));
    public static final DeferredItem<ArmorLiquidator> LEGS=ITEMS.register("liquidator_legs",()->new ArmorLiquidator(ArmorItem.Type.LEGGINGS,"liquidator_legs"));
    public static final DeferredItem<ArmorLiquidator> BOOTS=ITEMS.register("liquidator_boots",()->new ArmorLiquidator(ArmorItem.Type.BOOTS,"liquidator_boots"));
    public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(LiquidatorContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    private LiquidatorContent(){}
}
