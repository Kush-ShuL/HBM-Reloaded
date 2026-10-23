package com.hbm.armor;

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

/** Original HBM_HAZMAT2/3 are distinct materials with the original repair ingredients. */
public final class HazmatVariants {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> RED=MATERIALS.register("hazmat_red",()->material("red"));
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> GREY=MATERIALS.register("hazmat_grey",()->material("grey"));
    public static final DeferredItem<HazmatVariantMaskItem> RED_HELMET=ITEMS.register("hazmat_helmet_red",()->new HazmatVariantMaskItem(RED,"textures/models/modelhazred.png"));
    public static final DeferredItem<HazmatArmorItem> RED_PLATE=ITEMS.register("hazmat_plate_red",()->new HazmatArmorItem(RED,ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<HazmatArmorItem> RED_LEGS=ITEMS.register("hazmat_legs_red",()->new HazmatArmorItem(RED,ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<HazmatArmorItem> RED_BOOTS=ITEMS.register("hazmat_boots_red",()->new HazmatArmorItem(RED,ArmorItem.Type.BOOTS));
    public static final DeferredItem<HazmatVariantMaskItem> GREY_HELMET=ITEMS.register("hazmat_helmet_grey",()->new HazmatVariantMaskItem(GREY,"textures/models/modelhazgrey.png"));
    public static final DeferredItem<HazmatArmorItem> GREY_PLATE=ITEMS.register("hazmat_plate_grey",()->new HazmatArmorItem(GREY,ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<HazmatArmorItem> GREY_LEGS=ITEMS.register("hazmat_legs_grey",()->new HazmatArmorItem(GREY,ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<HazmatArmorItem> GREY_BOOTS=ITEMS.register("hazmat_boots_grey",()->new HazmatArmorItem(GREY,ArmorItem.Type.BOOTS));
    private HazmatVariants(){}
    private static ArmorMaterial material(String color){return new ArmorMaterial(Map.of(ArmorItem.Type.HELMET,2,ArmorItem.Type.CHESTPLATE,5,ArmorItem.Type.LEGGINGS,4,ArmorItem.Type.BOOTS,1),5,BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("hazmat_cloth_"+color)),List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm","hazmat_"+color))),0,0);}
    static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(HazmatVariants::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.COMBAT){event.accept(RED_HELMET.get());event.accept(RED_PLATE.get());event.accept(RED_LEGS.get());event.accept(RED_BOOTS.get());event.accept(GREY_HELMET.get());event.accept(GREY_PLATE.get());event.accept(GREY_LEGS.get());event.accept(GREY_BOOTS.get());}}
}
