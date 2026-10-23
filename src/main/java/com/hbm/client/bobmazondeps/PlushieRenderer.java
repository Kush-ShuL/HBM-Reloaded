package com.hbm.client.bobmazondeps;

import com.hbm.client.model.*;
import com.hbm.client.model.horse.LegacyHorsePronter;
import com.hbm.item.bobmazondeps.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;

/** Actual four authored toys, including NumberNine's source45degree sit, No9 helmet/insignia and cigarette slab. */
public final class PlushieRenderer implements BlockEntityRenderer<PlushieEntity> {
 private static final LegacyHorsePronter HORSE=new LegacyHorsePronter();
 public PlushieRenderer(BlockEntityRendererProvider.Context context){}
 private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 @Override public void render(PlushieEntity tile,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(-22.5F*tile.getBlockState().getValue(TrinketBlock.META)-90));if(tile.squishTimer>0){double squish=tile.squishTimer-partial;poses.scale(1,(float)(1-Math.sin(squish)*squish*.025),1);}float scale=switch(tile.type){case YOMI->.5F;case NUMBERNINE->.75F;default->1;};poses.scale(scale,scale,scale);draw(tile.type,tile.squishTimer>0,poses,buffers,light,overlay);poses.popPose();
 }
 public static void draw(TrinketTypes.Plushie type,boolean squish,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  switch(type){case NONE->{}case YOMI->part(poses,buffers,light,overlay,"models/trinkets/yomi.obj","textures/models/trinkets/yomi.png",false);case HUNDUN->part(poses,buffers,light,overlay,"models/trinkets/hundun.obj","textures/models/trinkets/hundun.png",false,"goober_posed");case DERG->{part(poses,buffers,light,overlay,"models/trinkets/derg.obj","textures/models/trinkets/derg.png",false,"Derg");part(poses,buffers,light,overlay,"models/trinkets/derg.obj","textures/models/trinkets/derg.png",false,squish?"Blep":"ColonThree");}case NUMBERNINE->numberNine(poses,buffers,light,overlay);}
 }
 private static void numberNine(PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.XP.rotationDegrees(-15));poses.translate(0,-.25,.75);HORSE.reset();double r=45;HORSE.pose(LegacyHorsePronter.BODY,0,-r,0);HORSE.pose(LegacyHorsePronter.TAIL,0,60,90);HORSE.pose(LegacyHorsePronter.LBL,0,-75+r,35);HORSE.pose(LegacyHorsePronter.RBL,0,-75+r,-35);HORSE.pose(LegacyHorsePronter.LFL,0,r-25,5);HORSE.pose(LegacyHorsePronter.RFL,0,r-25,-5);HORSE.pose(LegacyHorsePronter.HEAD,0,r+15,0);HORSE.render(poses,buffers,light,overlay,id("textures/models/horse/numbernine.png"));
  poses.mulPose(Axis.XP.rotationDegrees(15));poses.pushPose();poses.translate(0,1,-.6875);float scale=.0625F*1.125F;poses.scale(scale,scale,scale);poses.mulPose(Axis.XP.rotationDegrees(180));part(poses,buffers,light,overlay,"models/armor/no9.obj","textures/armor/no9.png",true,"Helmet");part(poses,buffers,light,overlay,"models/armor/no9.obj","textures/armor/no9_insignia.png",true,"Insignia");poses.popPose();
  poses.translate(-.06,1.13,-.42);poses.scale(.25F,.25F,.25F);poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(-60));var renderType=RenderType.entityCutout(id("textures/item/cigarette.png"));LegacyItemIn2D.render(poses,buffers.getBuffer(renderType),overlay,light,-1,16,16,.0625F);if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(renderType);
 }
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String model,String texture,boolean noCull,String... parts){var renderType=noCull?RenderType.entityCutoutNoCull(id(texture)):RenderType.entityCutout(id(texture));var mesh=LegacyObjModels.INSTANCE.get(net.minecraft.client.Minecraft.getInstance().getResourceManager(),id(model));if(parts.length==0)LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(renderType),light,overlay,-1,LegacyObjRenderer.Convention.HFR);else LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(renderType),light,overlay,-1,LegacyObjRenderer.Convention.HFR,parts);if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(renderType);}
}
