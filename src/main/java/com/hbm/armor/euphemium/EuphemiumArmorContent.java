package com.hbm.armor.euphemium;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Source ModItemsArmor37..40: four hidden epic items and real original HBM_EUPHEMIUM armor material. */
public final class EuphemiumArmorContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "hbm");
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> MATERIAL = MATERIALS.register("euphemium", () -> new ArmorMaterial(
            Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 8, ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3), 100,
            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY), () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:euphemium"))), 0, 0));
    public static final DeferredItem<ArmorEuphemium> HELMET = ITEMS.register("euphemium_helmet", () -> new ArmorEuphemium(MATERIAL, ArmorItem.Type.HELMET));
    public static final DeferredItem<ArmorEuphemium> PLATE = ITEMS.register("euphemium_plate", () -> new ArmorEuphemium(MATERIAL, ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<ArmorEuphemium> LEGS = ITEMS.register("euphemium_legs", () -> new ArmorEuphemium(MATERIAL, ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<ArmorEuphemium> BOOTS = ITEMS.register("euphemium_boots", () -> new ArmorEuphemium(MATERIAL, ArmorItem.Type.BOOTS));
    public static void register(IEventBus bus) { MATERIALS.register(bus); ITEMS.register(bus); }
    public static boolean fullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(HELMET.get()) && player.getItemBySlot(EquipmentSlot.CHEST).is(PLATE.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(LEGS.get()) && player.getItemBySlot(EquipmentSlot.FEET).is(BOOTS.get());
    }
    // Source ArmorEuphemium.setCreativeTab(null): deliberately no creative hook/stack factory.
    private EuphemiumArmorContent() { }
}
