package com.hbm.item.battery.client;

import com.hbm.HbmReloaded;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.item.battery.BatteryPackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Original HFR model/groups, per-variant textures and ItemRenderBase/ItemRenderBatteryPack transforms. */
public final class BatteryPackRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation MODEL = HbmReloaded.id("models/machines/battery.obj");
    public BatteryPackRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (!(stack.getItem() instanceof BatteryPackItem battery)) return;
        var variant = battery.variant(stack);
        var model = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL);
        poses.pushPose();
        try {
            // The source pack is an ordinary (not full-3D) Item. Its renderer declines
            // EQUIPPED_BLOCK, so Forge/RenderPlayer's outer hand matrix is required
            // before ItemRenderBase's own .5/.25 translation and quarter scale.
            if (LegacyItemTransforms.isHeld(context)) LegacyItemTransforms.held(poses, context);
            else poses.translate(0.5, 0.5, 0.5);
            if (context == ItemDisplayContext.GUI) {
                // Cancel GUI's 16px scale/Y inversion before applying the original pixel-space matrix.
                // GuiGraphics centers at (8,8), whereas ItemRenderBase centered at (8,10).
                poses.scale(1F / 16F, -1F / 16F, 1F / 16F);
                poses.translate(0, 2, 0);
                poses.mulPose(Axis.XP.rotationDegrees(-30));
                poses.mulPose(Axis.YP.rotationDegrees(45));
                poses.scale(-1, -1, -1);
                poses.translate(0, -3, 0);
                poses.scale(5, 5, 5);
            } else {
                boolean entity = context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED || context == ItemDisplayContext.NONE;
                boolean equipped = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                if (entity) poses.scale(1.5F, 1.5F, 1.5F);
                else poses.translate(0.5, 0.25, 0);
                poses.scale(0.25F, 0.25F, 0.25F);
                if (!equipped) poses.mulPose(Axis.YP.rotationDegrees(90));
            }
            // entityCutout retains source culling/alpha-test semantics; HFR uses authored smooth normals.
            LegacyObjRenderer.renderOnly(model, poses, buffers.getBuffer(RenderType.entityCutout(variant.texture)),
                    light, overlay, 0xFFFFFFFF, LegacyObjRenderer.Convention.HFR, variant.isCapacitor() ? "Capacitor" : "Battery");
        } finally { poses.popPose(); }
    }
}
