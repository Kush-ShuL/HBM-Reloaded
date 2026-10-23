package com.hbm.client.generator.leviathan;
import com.hbm.client.model.*;
import com.hbm.machine.generator.leviathan.LeviathanEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Full original Body/Lever/Blades HFR mesh and pivot/matrices, actualinfinite sourcevisualbounds. */
public final class LeviathanRenderer implements BlockEntityRenderer<LeviathanEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/chungus.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/chungus.png");
 public LeviathanRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void part(String group,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,group);}
 @Override public void render(LeviathanEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=90+switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(0,0,-3),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.translate(0,0,-3);part("Body",pose,buffers,light,overlay);pose.pushPose();pose.translate(0,0,4.5);pose.mulPose(Axis.XP.rotationDegrees(15-(tile.tanks[0].getTankType().getID()-2)*10));pose.translate(0,0,-4.5);part("Lever",pose,buffers,light,overlay);pose.popPose();pose.translate(0,2.5,0);pose.mulPose(Axis.ZP.rotationDegrees(-(tile.lastRotor+(tile.rotor-tile.lastRotor)*partial)));pose.translate(0,-2.5,0);part("Blades",pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(LeviathanEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(LeviathanEntity tile){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
