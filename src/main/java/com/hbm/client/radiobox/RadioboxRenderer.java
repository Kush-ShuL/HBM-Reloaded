package com.hbm.client.radiobox;

import com.hbm.client.model.legacy.LegacyModelRenderContext;
import com.hbm.client.model.legacy.generated.ModelRadio;
import com.hbm.machine.radiobox.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;

/** RenderDecoBlock's actual ModelRadio matrices and lever20/160, using the sole original Techne kernel. */
public final class RadioboxRenderer implements BlockEntityRenderer<RadioboxEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/modelradio.png");
    private final ModelRadio model = new ModelRadio();
    public RadioboxRenderer(BlockEntityRendererProvider.Context context) { }
    @Override public void render(RadioboxEntity box, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        int meta = box.getBlockState().getValue(RadioboxBlock.META);
        poses.pushPose();
        try {
            poses.translate(.5F, 1.5F, .5F); poses.mulPose(Axis.ZP.rotationDegrees(180));
            int yaw = switch (meta) { case 4, 8 -> 270; case 5, 9 -> 90; case 3, 7 -> 180; default -> 0; };
            if (yaw != 0) poses.mulPose(Axis.YP.rotationDegrees(yaw));
            poses.translate(0, 0, 1);
            try (var ignored = LegacyModelRenderContext.push(poses, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, overlay, -1)) {
                model.renderModel(.0625F, meta > 5 ? 160 : 20);
            }
        } finally { poses.popPose(); }
    }
    @Override public int getViewDistance() { return 256; }
}
