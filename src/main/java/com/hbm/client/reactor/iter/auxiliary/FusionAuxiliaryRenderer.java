package com.hbm.client.reactor.iter.auxiliary;
import com.hbm.client.model.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
/** Both original complete HFR meshes, collector yaw / coupler extraY90, originalfullbounds. */
public final class FusionAuxiliaryRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
 private final boolean collector;
 public FusionAuxiliaryRenderer(BlockEntityRendererProvider.Context context,boolean collector){this.collector=collector;}
 static WavefrontMesh mesh(boolean collector){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/fusion/"+(collector?"collector":"coupler")+".obj"));}
 static void model(boolean collector,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var texture=ResourceLocation.parse("hbm:textures/models/fusion/"+(collector?"collector":"coupler")+".png");LegacyObjRenderer.renderAll(mesh(collector),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(T tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=(collector?0:90)+switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(collector),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));model(collector,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(T tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(T tile){var pos=tile.getBlockPos();int radius=collector?2:1;return new AABB(pos.getX()-radius,pos.getY(),pos.getZ()-radius,pos.getX()+radius+1,pos.getY()+4,pos.getZ()+radius+1);}
}
