package com.hbm.client.network.separator;
import com.hbm.client.model.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.network.separator.NetworkSeparatorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** The original smooth HFR OBJ, actual mesh shadows and source world/held matrices. */
public final class NetworkSeparatorRenderer implements BlockEntityRenderer<NetworkSeparatorEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/network/network_separator.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/network/network_separator.png");
 public NetworkSeparatorRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(NetworkSeparatorEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));model(pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(NetworkSeparatorEntity tile){return true;}@Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(NetworkSeparatorEntity tile){var p=tile.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+2,p.getZ()+2);}
}
