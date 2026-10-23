package com.hbm.client.geiger;

import com.hbm.HbmReloaded;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.geiger.GeigerBlock;
import com.hbm.geiger.GeigerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original RenderGeiger uses the authored smooth OBJ, not the unbound old ModelGeiger boxes. */
public final class GeigerRenderer implements BlockEntityRenderer<GeigerBlockEntity> {
    static final ResourceLocation MODEL=HbmReloaded.id("models/blocks/geiger_counter.obj");
    static final ResourceLocation TEXTURE=HbmReloaded.id("textures/block/geiger.png");
    public GeigerRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(GeigerBlockEntity geiger,float partialTick,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();poses.translate(.5,0,.5);
        float angle=switch(geiger.getBlockState().getValue(GeigerBlock.FACING)) { case WEST->90;case SOUTH->180;case EAST->270;default->0; };
        poses.mulPose(Axis.YP.rotationDegrees(angle));mesh(poses,buffers,light,overlay);poses.popPose();
    }
    static void mesh(PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
        LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.HFR);
    }
    @Override public AABB getRenderBoundingBox(GeigerBlockEntity geiger) { return new AABB(geiger.getBlockPos()); }
    @Override public int getViewDistance() { return 256; }
}
