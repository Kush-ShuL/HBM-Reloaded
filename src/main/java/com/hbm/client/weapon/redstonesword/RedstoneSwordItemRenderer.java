package com.hbm.client.weapon.redstonesword;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.client.model.legacy.generated.ModelSword;
import com.hbm.client.weapon.pepperbox.GunHandTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Exact ItemRenderRedstoneSword local matrix and already source-generated seven-box ModelSword. */
public final class RedstoneSwordItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/model_sword_redstone.png");
    private final ModelSword sword = new ModelSword();
    public RedstoneSwordItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
    @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        poses.pushPose();
        try (var reflected = GunHandTransforms.heldFull3D(poses, context)) {
            if (context.firstPerson()) LegacyItemTransforms.heldFull3D(poses, context);
            else if (context == ItemDisplayContext.GROUND) {
                // Native dropped caller contributes+.25Y and ItemRenderer(-.5,-.5,-.5); source declines every entity helper.
                poses.translate(.5, .25, .5);
            }
            poses.mulPose(Axis.ZP.rotationDegrees(-135F));
            poses.translate(-.8F, .4F, -.1F);
            boolean left = context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            var vertices = buffers.getBuffer(left ? RenderType.entityCutoutNoCull(TEXTURE) : RenderType.entityCutout(TEXTURE));
            sword.renderToBuffer(poses, vertices, light, overlay, -1, .0625F);
        } finally { poses.popPose(); }
    }
}
