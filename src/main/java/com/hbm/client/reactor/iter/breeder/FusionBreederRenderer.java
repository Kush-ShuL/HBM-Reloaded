package com.hbm.client.reactor.iter.breeder;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.iter.breeder.FusionBreederEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact original HFRBreeder group/matrix and fullbounds/offscreen rendering. */
public final class FusionBreederRenderer implements BlockEntityRenderer<FusionBreederEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/fusion/breeder.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/breeder.png");public FusionBreederRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Breeder");}
 @Override public void render(FusionBreederEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));model(pose,buffers,light,overlay);pose.popPose();}@Override public boolean shouldRenderOffScreen(FusionBreederEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(FusionBreederEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-2,pos.getY(),pos.getZ()-2,pos.getX()+3,pos.getY()+4,pos.getZ()+3);}
}
