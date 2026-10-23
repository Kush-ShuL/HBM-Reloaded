package com.hbm.client.equipment.npcheadgear;

import com.hbm.client.armor.*;
import com.hbm.equipment.npcheadgear.*;
import net.minecraft.client.model.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class NpcHeadgearClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        // Source ArmorGasMask explicitly selects ModelM65 for olde, not a substitute model.
        event.registerItem(extension(new LegacyHazmatArmorModel(new LegacyM65Model())), NpcHeadgearContent.OLDE.get());
        event.registerItem(extension(new LegacyHazmatArmorModel(1)), NpcHeadgearContent.INFAMY.get());
    }
    private static IClientItemExtensions extension(LegacyHazmatArmorModel original) {
        return new IClientItemExtensions() {
            @Override public Model getGenericArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> parent) { original.configure(entity, slot, parent); return original; }
            @Override public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
                    Model model, float swing, float amount, float partial, float age, float yaw, float pitch) {
                ((LegacyHazmatArmorModel) model).animate(swing, amount, age, yaw, pitch);
            }
        };
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(() -> MiningHelmetItem.CLIENT_TICK = No9Light::armorTick); }
    private NpcHeadgearClient() {}
}
