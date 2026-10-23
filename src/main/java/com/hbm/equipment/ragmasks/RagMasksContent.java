package com.hbm.equipment.ragmasks;

import com.hbm.armor.robes.RobesContent;
import com.hbm.equipment.vaultclothing.VaultArmorItem;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual two ModArmor masks; not GasMask items, FSB pieces, filter attachments or another hazard backend. */
public final class RagMasksContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("rag_masks",
        () -> new ArmorMaterial(Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 1,
            ArmorItem.Type.LEGGINGS, 1, ArmorItem.Type.BOOTS, 1), 0,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.of(RobesContent.RAG.get()),
            List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:rag_masks"))), 0, 0));
    public static final DeferredItem<VaultArmorItem> RAG = ITEMS.register("mask_rag",
        () -> new VaultArmorItem(MATERIAL, ArmorItem.Type.HELMET, 150, "mask_rag", "textures/armor/rag_damp.png"));
    public static final DeferredItem<VaultArmorItem> TRENCH = ITEMS.register("mask_piss",
        () -> new VaultArmorItem(MATERIAL, ArmorItem.Type.HELMET, 150, "mask_piss", "textures/armor/rag_piss.png"));
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); bus.addListener(RagMasksContent::creative); }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) indexedItems().forEach(event::accept);
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(RAG.get()), new ItemStack(TRENCH.get())); }
    private RagMasksContent() { }
}
