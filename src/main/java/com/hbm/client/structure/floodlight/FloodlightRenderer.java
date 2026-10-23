package com.hbm.client.structure.floodlight;
import com.hbm.block.light.SpotlightBlock;
import com.hbm.block.light.floodlight.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Literal source RenderFloodlight mount/rotation matrices, HFR UV/normals and Lamps fullbright-or-quarter tint. */
public final class FloodlightRenderer implements BlockEntityRenderer<FloodlightBlockEntity>{
 public static final ResourceLocation MODEL=FloodlightContent.id("models/blocks/floodlight.obj"),TEXTURE=FloodlightContent.id("textures/models/machines/floodlight.png");
 public FloodlightRenderer(BlockEntityRendererProvider.Context context){}
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,int color,String...parts){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,color,LegacyObjRenderer.Convention.HFR,parts);}
 public static void item(PoseStack poses,MultiBufferSource buffers,int light,int overlay){part(poses,buffers,light,overlay,-1,"Base");poses.translate(0,.5,0);poses.mulPose(Axis.ZP.rotationDegrees(-30));poses.translate(0,-.5,0);part(poses,buffers,light,overlay,-1,"Lights","Lamps");}
 @Override public void render(FloodlightBlockEntity lamp,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,.5,.5);int meta=lamp.getBlockState().getValue(SpotlightBlock.META);switch(meta){case 0,6->poses.mulPose(Axis.XP.rotationDegrees(180));case 2->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(180));}case 3->poses.mulPose(Axis.XP.rotationDegrees(90));case 4->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(90));}case 5->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(270));}default->{}}poses.translate(0,-.5,0);if(meta!=0&&meta!=1)poses.mulPose(Axis.YP.rotationDegrees(90));part(poses,buffers,light,overlay,-1,"Base");float rotation=lamp.rotation;if(meta==0||meta==6)rotation-=90;if(meta==1||meta==7)rotation+=90;poses.translate(0,.5,0);poses.mulPose(Axis.ZP.rotationDegrees(rotation));poses.translate(0,-.5,0);part(poses,buffers,light,overlay,-1,"Lights");part(poses,buffers,lamp.isOn?LightTexture.FULL_BRIGHT:light,overlay,lamp.isOn?-1:0xff404040,"Lamps");poses.popPose();}
 @Override public AABB getRenderBoundingBox(FloodlightBlockEntity lamp){return new AABB(lamp.getBlockPos()).inflate(1);}@Override public boolean shouldRenderOffScreen(FloodlightBlockEntity lamp){return true;}@Override public int getViewDistance(){return 256;}
}
