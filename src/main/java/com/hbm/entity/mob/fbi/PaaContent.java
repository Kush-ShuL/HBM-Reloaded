package com.hbm.entity.mob.fbi;

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

/** The source HBM_PAA material and its three real no-helmet ArmorFSB identities. */
public final class PaaContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("paa",
            () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 25,
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                    () -> Ingredient.of(HbmContent.item("plate_paa")),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm", "paa"))),
                    0, 0));
    public static final DeferredItem<PaaArmorItem> PLATE = ITEMS.register("paa_plate",
            () -> new PaaArmorItem(ArmorItem.Type.CHESTPLATE, "paa_plate"));
    public static final DeferredItem<PaaArmorItem> LEGS = ITEMS.register("paa_legs",
            () -> new PaaArmorItem(ArmorItem.Type.LEGGINGS, "paa_legs"));
    public static final DeferredItem<PaaArmorItem> BOOTS = ITEMS.register("paa_boots",
            () -> new PaaArmorItem(ArmorItem.Type.BOOTS, "paa_boots"));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(PaaContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            for (var item : List.of(PLATE, LEGS, BOOTS)) event.accept(item.get());
        }
    }

    private PaaContent() {}
}
