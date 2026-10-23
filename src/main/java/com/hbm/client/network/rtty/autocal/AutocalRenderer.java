package com.hbm.client.network.rtty.autocal;
import com.hbm.client.model.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.network.rtty.autocal.AutocalEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original AdvancedModelLoader Forge OBJ, exact world orientation and full source render bounds. */
public final class AutocalRenderer implements BlockEntityRenderer<AutocalEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/autocal.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/autocal.png");public AutocalRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
 @Override public void render(AutocalEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));model(pose,buffers,light,overlay);pose.popPose();}@Override public boolean shouldRenderOffScreen(AutocalEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AutocalEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+2,pos.getZ()+1);}
}
