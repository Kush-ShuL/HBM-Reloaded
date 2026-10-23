package com.hbm.client.meteorgavel;

import com.hbm.client.model.*;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.item.meteorgavel.SourceGavelAppearance;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

/** Original ItemRenderGavel: GUI stays the original icon, held/entity use original OBJ and cumulative matrices. */
public final class GavelItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation OBJ = ResourceLocation.fromNamespaceAndPath("hbm", "models/weapons/gavel.obj");
    public GavelItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var kind = (SourceGavelAppearance)stack.getItem();
        var texture = ResourceLocation.fromNamespaceAndPath("hbm", "textures/models/weapons/gavel_" + kind.sourceGavelTexture() + ".png");
        poses.pushPose();
        if (context.firstPerson() || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) LegacyItemTransforms.heldFull3D(poses, context);
        else {
            // Native ItemRenderer applies T(-.5,-.5,-.5). GROUND's caller also adds .25*groundScaleY.
            // Undo those; source ForgeHooksClient's non-BLOCK_3D ENTITY path applies scale.5 before the custom renderer.
            poses.translate(.5, context == ItemDisplayContext.GROUND ? .25 : .5, .5);
            poses.scale(.5F, .5F, .5F);
        }
        if (context.firstPerson()) {
            poses.translate(1, .5, 0);
            var player = Minecraft.getInstance().player;
            if (player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof LegacySwordBlocking) poses.translate(-.5, 0, 0);
            poses.mulPose(Axis.ZP.rotationDegrees(45)); poses.mulPose(Axis.YP.rotationDegrees(90));
            if (kind.sourceLargeGavel()) poses.scale(2, 2, 2);
        } else {
            if (context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED) poses.translate(-.5, 0, 0);
            poses.scale(.5F, .5F, .5F); poses.mulPose(Axis.ZP.rotationDegrees(45)); poses.translate(1.375, 0, 0); poses.mulPose(Axis.YP.rotationDegrees(90));
            if (kind.sourceLargeGavel()) { poses.scale(2, 2, 2); poses.translate(0, .25, 0); }
        }
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), OBJ);
        LegacyObjRenderer.render(mesh, poses, buffers.getBuffer(RenderType.entityCutoutNoCull(texture)), light, overlay, -1, LegacyObjRenderer.Convention.HFR, face -> true);
        poses.popPose();
    }
}
