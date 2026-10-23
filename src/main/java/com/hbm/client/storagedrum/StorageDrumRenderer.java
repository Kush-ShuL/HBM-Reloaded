package com.hbm.client.storagedrum;

import com.hbm.client.model.LegacyContactShadows;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.WavefrontMesh;
import com.hbm.machine.storagedrum.StorageDrumEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/** ResourceManager215 AdvancedModelLoader: FORGE flat faces/UV inset, source T(.5,0,.5), culling enabled. */
public final class StorageDrumRenderer implements BlockEntityRenderer<StorageDrumEntity> {
    static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/machines/drum.obj");
    static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/models/machines/drum_gray.png");
    public StorageDrumRenderer(BlockEntityRendererProvider.Context context) {}
    private static WavefrontMesh model() { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL); }
    static void all(PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        LegacyObjRenderer.renderAll(model(), pose, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay, -1, LegacyObjRenderer.Convention.FORGE);
    }
    @Override public void render(StorageDrumEntity drum, float partial, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        LegacyContactShadows.render(drum, model(), new Matrix4f().translation(.5F, 0, .5F), pose, buffers);
        pose.pushPose();
        pose.translate(.5, 0, .5);
        all(pose, buffers, light, overlay);
        pose.popPose();
    }
}
