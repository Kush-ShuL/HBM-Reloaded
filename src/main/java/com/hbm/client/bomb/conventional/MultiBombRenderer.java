package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.MultiBombBlock;
import com.hbm.bomb.conventional.MultiBombBlockEntity;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Source RenderBombMulti uses the original Forge OBJ convention, not the HFR flat/smooth variant. */
public final class MultiBombRenderer implements BlockEntityRenderer<MultiBombBlockEntity> {
    public MultiBombRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(MultiBombBlockEntity bomb,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,.5,.5);poses.mulPose(Axis.XP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(switch(bomb.getBlockState().getValue(MultiBombBlock.FACING)){case EAST->90;case WEST->270;case SOUTH->180;default->0;}));mesh(poses,buffers,light,overlay);poses.popPose();}
    static void mesh(PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/bombgeneric.obj"));LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.parse("hbm:textures/models/bombgeneric.png"))),light,overlay,0xFFFFFFFF,LegacyObjRenderer.Convention.FORGE);}
    @Override public boolean shouldRenderOffScreen(MultiBombBlockEntity bomb){return true;}
    @Override public AABB getRenderBoundingBox(MultiBombBlockEntity bomb){return AABB.INFINITE;}
    @Override public int getViewDistance(){return 256;}
}
