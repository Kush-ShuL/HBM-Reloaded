package com.hbm.client.armor.plainfsb;

import com.hbm.client.armor.LegacyHazmatArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Curated source plain ArmorFSB/ModArmor only; no copied model math or replacement equipment behavior. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class LegacyFsbBipedClient {
    private static final String[] SOURCE_ITEMS = {
        "hbm:euphemium_helmet",
        "hbm:euphemium_plate",
        "hbm:euphemium_legs",
        "hbm:euphemium_boots",
        "hbm:robes_helmet",
        "hbm:robes_plate",
        "hbm:robes_legs",
        "hbm:robes_boots",
        "hbm:alloy_helmet",
        "hbm:alloy_plate",
        "hbm:alloy_legs",
        "hbm:alloy_boots",
        "hbm:security_helmet",
        "hbm:security_plate",
        "hbm:security_legs",
        "hbm:security_boots",
        "hbm:cobalt_helmet",
        "hbm:cobalt_plate",
        "hbm:cobalt_legs",
        "hbm:cobalt_boots",
        "hbm:steel_helmet",
        "hbm:steel_plate",
        "hbm:steel_legs",
        "hbm:steel_boots",
        "hbm:jackt",
        "hbm:jackt2",
        "hbm:starmetal_helmet",
        "hbm:starmetal_plate",
        "hbm:starmetal_legs",
        "hbm:starmetal_boots",
        "hbm:zirconium_legs",
        "hbm:schrabidium_helmet",
        "hbm:schrabidium_plate",
        "hbm:schrabidium_legs",
        "hbm:schrabidium_boots"
    };

    private static final IClientItemExtensions BIPED = new IClientItemExtensions() {
        private final LegacyHazmatArmorModel inner = new LegacyHazmatArmorModel(.5F);
        private final LegacyHazmatArmorModel outer = new LegacyHazmatArmorModel(1F);

        @Override
        public Model getGenericArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> parent) {
            var model = slot == EquipmentSlot.LEGS ? inner : outer;
            model.configure(living, slot, parent);
            return model;
        }

        @Override
        public void setupModelAnimations(LivingEntity living, ItemStack stack, EquipmentSlot slot, Model model,
                                         float swing, float amount, float partial, float age, float yaw, float pitch) {
            ((LegacyHazmatArmorModel) model).animate(swing, amount, age, yaw, pitch);
        }
    };

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void extensions(RegisterClientExtensionsEvent event) {
        registerPlainBiped(event, SOURCE_ITEMS);
    }

    /** Future plain source armor may use the same adapter, without overriding an existing dedicated extension. */
    public static void registerPlainBiped(RegisterClientExtensionsEvent event, String... actualIds) {
        for (String text : actualIds) {
            var id = ResourceLocation.parse(text);
            if (!BuiltInRegistries.ITEM.containsKey(id)) continue;
            var item = BuiltInRegistries.ITEM.get(id);
            if (!(item instanceof ArmorItem)) throw new IllegalStateException("Non-armor registered for source biped: " + id);
            if (!event.isItemRegistered(item)) event.registerItem(BIPED, item);
        }
    }

    private LegacyFsbBipedClient() {}
}
