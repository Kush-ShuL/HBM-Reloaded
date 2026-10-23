package com.hbm.armor.security;

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

/** The source HBM_SECURITY material and its four real ArmorFSB identities. */
public final class SecurityContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("security",
            () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 15,
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                    () -> Ingredient.of(HbmContent.item("plate_kevlar")),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm", "security"))),
                    0, 0));
    public static final DeferredItem<SecurityArmorItem> HELMET = ITEMS.register("security_helmet",
            () -> new SecurityArmorItem(ArmorItem.Type.HELMET, "security_helmet"));
    public static final DeferredItem<SecurityArmorItem> PLATE = ITEMS.register("security_plate",
            () -> new SecurityArmorItem(ArmorItem.Type.CHESTPLATE, "security_plate"));
    public static final DeferredItem<SecurityArmorItem> LEGS = ITEMS.register("security_legs",
            () -> new SecurityArmorItem(ArmorItem.Type.LEGGINGS, "security_legs"));
    public static final DeferredItem<SecurityArmorItem> BOOTS = ITEMS.register("security_boots",
            () -> new SecurityArmorItem(ArmorItem.Type.BOOTS, "security_boots"));

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        bus.addListener(SecurityContent::creative);
    }

    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            for (var item : List.of(HELMET, PLATE, LEGS, BOOTS)) event.accept(item.get());
        }
    }

    private SecurityContent() {}
}
