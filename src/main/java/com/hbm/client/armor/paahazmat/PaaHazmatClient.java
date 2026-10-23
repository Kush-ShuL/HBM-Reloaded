package com.hbm.client.armor.paahazmat;

import com.hbm.armor.paahazmat.PaaHazmatContent;
import com.hbm.client.armor.LegacyHazmatArmorModel;
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

/** Source PAA hood has ordinary biped armor, not red/grey's M65; same real hazmat blur. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PaaHazmatClient {
    private static final ResourceLocation OVERLAY = ResourceLocation.parse("hbm:textures/misc/overlay_hazmat.png");
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private final LegacyHazmatArmorModel inner = new LegacyHazmatArmorModel(.5F), outer = new LegacyHazmatArmorModel(1F);
            @Override public Model getGenericArmorModel(LivingEntity wearer, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> parent) {
                var model = slot == EquipmentSlot.LEGS ? inner : outer;
                model.configure(wearer, slot, parent);
                return model;
            }
            @Override public void setupModelAnimations(LivingEntity wearer, ItemStack stack, EquipmentSlot slot, Model model,
                float swing, float amount, float partial, float age, float yaw, float pitch) {
                ((LegacyHazmatArmorModel) model).animate(swing, amount, age, yaw, pitch);
            }
            @Override public void renderHelmetOverlay(ItemStack stack, Player wearer, GuiGraphics graphics, DeltaTracker delta) {
                if (!stack.is(PaaHazmatContent.HELMET.get())) return;
                graphics.flush();
                RenderSystem.enableBlend(); RenderSystem.defaultBlendFunc(); RenderSystem.disableDepthTest(); RenderSystem.depthMask(false);
                graphics.setColor(1, 1, 1, 1); graphics.pose().pushPose();
                try {
                    graphics.pose().translate(0, 0, -90);
                    graphics.blit(OVERLAY, 0, 0, 0, 0, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
                    graphics.flush();
                } finally {
                    graphics.pose().popPose(); graphics.setColor(1, 1, 1, 1);
                    RenderSystem.depthMask(true); RenderSystem.enableDepthTest();
                }
            }
        }, PaaHazmatContent.HELMET.get(), PaaHazmatContent.PLATE.get(), PaaHazmatContent.LEGS.get(), PaaHazmatContent.BOOTS.get());
    }
    private PaaHazmatClient() { }
}
