package com.hbm.client.centrifuge;

import com.hbm.client.model.*;
import com.hbm.machine.centrifuge.CentrifugeBlockEntity;
import com.hbm.multiblock.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original HFR whole mesh, authored normals, no culling; the solid centrifuge has no rotor animation. */
public final class CentrifugeRenderer implements BlockEntityRenderer<CentrifugeBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/centrifuge.obj");
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/centrifuge.png");
    public CentrifugeRenderer(BlockEntityRendererProvider.Context context) {}
    static void mesh(PoseStack poses,MultiBufferSource buffers,int light,int overlay) { LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR); }
    @Override public void render(CentrifugeBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose(); poses.translate(.5,0,.5);
        int angle=switch(DummyableBlock.facing(machine.getBlockState())) { case NORTH->90; case WEST->180; case SOUTH->270; default->0; };
        poses.mulPose(Axis.YP.rotationDegrees(angle)); mesh(poses,buffers,light,overlay); poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(CentrifugeBlockEntity machine) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(CentrifugeBlockEntity machine) { return MultiblockRenderBounds.of(machine.getBlockPos(),machine.getBlockState()); }
}
