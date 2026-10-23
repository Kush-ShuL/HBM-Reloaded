package com.hbm.block.pipe.client;

import com.hbm.hazard.HbmHazards;
import com.hbm.fluid.HbmFluids;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class PipeItemRenderer extends BlockEntityWithoutLevelRenderer {
    public PipeItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose(); poses.translate(0.5, 0.5, 0.5); poses.mulPose(Axis.YP.rotationDegrees(180)); poses.scale(1.25F, 1.25F, 1.25F);
        PipeRenderer.draw(poses, buffers, light, overlay, Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) % 3), HbmFluids.NONE.getColor(), false, "pX", "nX", "pZ", "nZ");
        poses.popPose();
    }
}
