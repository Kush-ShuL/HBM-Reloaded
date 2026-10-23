package com.hbm.armor.starmetal;

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

/** Exactly the four source Starmetal registrations and the single source Zirconium leggings. */
public final class StarmetalArmorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    private static ArmorMaterial material(Map<ArmorItem.Type,Integer> defense,int enchant,String repair,String texture){return new ArmorMaterial(defense,enchant,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item(repair)),List.of(new ArmorMaterial.Layer(id(texture))),0,0);}
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> STARMETAL=MATERIALS.register("starmetal",()->material(Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),100,"ingot_starmetal","starmetal"));
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> ZIRCONIUM=MATERIALS.register("zirconium",()->material(Map.of(ArmorItem.Type.HELMET,2,ArmorItem.Type.CHESTPLATE,5,ArmorItem.Type.LEGGINGS,3,ArmorItem.Type.BOOTS,1),1000,"ingot_zirconium","zirconium"));
    public static final DeferredItem<StarmetalArmorItem> HELMET=ITEMS.register("starmetal_helmet",()->new StarmetalArmorItem(STARMETAL,ArmorItem.Type.HELMET,150,"starmetal_helmet","starmetal_1"));
    public static final DeferredItem<StarmetalArmorItem> PLATE=ITEMS.register("starmetal_plate",()->new StarmetalArmorItem(STARMETAL,ArmorItem.Type.CHESTPLATE,150,"starmetal_plate","starmetal_1"));
    public static final DeferredItem<StarmetalArmorItem> LEGS=ITEMS.register("starmetal_legs",()->new StarmetalArmorItem(STARMETAL,ArmorItem.Type.LEGGINGS,150,"starmetal_legs","starmetal_2"));
    public static final DeferredItem<StarmetalArmorItem> BOOTS=ITEMS.register("starmetal_boots",()->new StarmetalArmorItem(STARMETAL,ArmorItem.Type.BOOTS,150,"starmetal_boots","starmetal_1"));
    public static final DeferredItem<StarmetalArmorItem> ZIRCONIUM_LEGS=ITEMS.register("zirconium_legs",()->new StarmetalArmorItem(ZIRCONIUM,ArmorItem.Type.LEGGINGS,1000,"zirconium_legs","zirconium_2"));
    public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(StarmetalArmorContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS,ZIRCONIUM_LEGS))event.accept(item.get());}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    private StarmetalArmorContent(){}
}
