package com.hbm.armor.schrabidium;

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

/** Four genuine original registrations; existing source hazard/radiation/DTDR configuration remains authoritative. */
public final class SchrabidiumArmorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("schrabidium",()->new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),50,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(com.hbm.registry.HbmContent.item("ingot_schrabidium")),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm","schrabidium"))),0,0));
    public static final DeferredItem<SchrabidiumArmorItem> HELMET=ITEMS.register("schrabidium_helmet",()->new SchrabidiumArmorItem(MATERIAL,ArmorItem.Type.HELMET));
    public static final DeferredItem<SchrabidiumArmorItem> PLATE=ITEMS.register("schrabidium_plate",()->new SchrabidiumArmorItem(MATERIAL,ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<SchrabidiumArmorItem> LEGS=ITEMS.register("schrabidium_legs",()->new SchrabidiumArmorItem(MATERIAL,ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<SchrabidiumArmorItem> BOOTS=ITEMS.register("schrabidium_boots",()->new SchrabidiumArmorItem(MATERIAL,ArmorItem.Type.BOOTS));
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(SchrabidiumArmorContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) for (var item:List.of(HELMET,PLATE,LEGS,BOOTS)) event.accept(item.get());
    }
    private SchrabidiumArmorContent() {}
}
