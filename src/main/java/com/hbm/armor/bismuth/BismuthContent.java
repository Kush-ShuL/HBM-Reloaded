package com.hbm.armor.bismuth;

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

/** The source HBM_BISMUTH material and its four real ArmorFSB identities. */
public final class BismuthContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("bismuth",
            () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 100,
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                    () -> Ingredient.of(HbmContent.item("plate_bismuth")),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm", "bismuth"))),
                    0, 0));
    public static final DeferredItem<ArmorBismuth> HELMET = ITEMS.register("bismuth_helmet",
            () -> new ArmorBismuth(ArmorItem.Type.HELMET, "bismuth_helmet"));
    public static final DeferredItem<ArmorBismuth> PLATE = ITEMS.register("bismuth_plate",
            () -> new ArmorBismuth(ArmorItem.Type.CHESTPLATE, "bismuth_plate"));
    public static final DeferredItem<ArmorBismuth> LEGS = ITEMS.register("bismuth_legs",
            () -> new ArmorBismuth(ArmorItem.Type.LEGGINGS, "bismuth_legs"));
    public static final DeferredItem<ArmorBismuth> BOOTS = ITEMS.register("bismuth_boots",
            () -> new ArmorBismuth(ArmorItem.Type.BOOTS, "bismuth_boots"));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(BismuthContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
        }
    }

    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    private BismuthContent() {}
}
