package com.hbm.client.originalgasmask;

import com.hbm.client.armor.LegacyHazmatArmorModel;
import com.hbm.item.originalgasmask.OriginalGasMaskContent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Actual original mask mesh and the six source damage overlays. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class OriginalGasMaskClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final LegacyHazmatArmorModel model = new LegacyHazmatArmorModel(new LegacyOriginalGasMaskModel());
            @Override public Model getGenericArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> parent) {
                model.configure(entity, slot, parent);
                return model;
            }
            @Override public void setupModelAnimations(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
                Model model, float swing, float amount, float partial, float age, float yaw, float pitch) {
                ((LegacyHazmatArmorModel) model).animate(swing, amount, age, yaw, pitch);
            }
            @Override public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics graphics, DeltaTracker delta) {
                int index = Math.min((int) ((double) stack.getDamageValue() / stack.getMaxDamage() * 6), 5);
                var texture = ResourceLocation.fromNamespaceAndPath("hbm", "textures/misc/overlay_gasmask_" + index + ".png");
                // Submit the existing caller's GUI work before this source GL state.
                graphics.flush();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                graphics.setColor(1, 1, 1, 1);
                graphics.pose().pushPose();
                try {
                    graphics.pose().translate(0, 0, -90);
                    graphics.blit(texture, 0, 0, 0, 0, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
                    graphics.flush();
                } finally {
                    graphics.pose().popPose();
                    RenderSystem.depthMask(true);
                    RenderSystem.enableDepthTest();
                    graphics.setColor(1, 1, 1, 1);
                }
            }
        }, OriginalGasMaskContent.MASK.get());
    }
    private OriginalGasMaskClient() { }
}
