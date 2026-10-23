package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearBombBlock;
import com.hbm.bomb.nuclear.NuclearBombBlockEntity;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Same archived OBJ, authored normals, culling and original metadata transforms. */
public final class NuclearBombRenderer implements BlockEntityRenderer<NuclearBombBlockEntity> {
 public NuclearBombRenderer(BlockEntityRendererProvider.Context context){}
 private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 static void draw(String kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay){draw(kind,poses,buffers,light,overlay,false);}
 static void draw(String kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean item){
  String model=switch(kind){case "boy"->"lilboy1";case "man"->"bombs/fat_man";case "mike"->"bombs/ivymike";case "solinium"->"bombs/ufp";default->"bombs/"+kind;};
  String texture=switch(kind){case "boy"->"lilboy";case "man"->"fatman";case "mike"->"bombs/ivymike";case "solinium"->"bombs/ufp";default->"bombs/"+kind;};
  var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id("models/"+model+".obj"));var consumer=buffers.getBuffer((kind.equals("boy")||kind.equals("fleija")||kind.equals("n2")&&!item)?RenderType.entityCutout(id("textures/models/"+texture+".png")):RenderType.entityCutoutNoCull(id("textures/models/"+texture+".png")));
  var convention=kind.equals("boy")?LegacyObjRenderer.Convention.FORGE:LegacyObjRenderer.Convention.HFR;
  if(kind.equals("gadget")){LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,0xffffffff,convention,"Body");if(Minecraft.getInstance().options.graphicsMode().get()!=GraphicsStatus.FAST)LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,overlay,0xffffffff,convention,"Wires");}
  else LegacyObjRenderer.renderAll(mesh,poses,consumer,light,overlay,0xffffffff,convention);
 }
 @Override public void render(NuclearBombBlockEntity bomb,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  String kind=((NuclearBombBlock)bomb.getBlockState().getBlock()).kind();int metadata=bomb.getBlockState().getValue(NuclearBombBlock.META);poses.pushPose();poses.translate(.5,0,.5);int angle=switch(kind){case "gadget"->switch(metadata){case 2->0;case 4->90;case 3->180;case 5->270;default->0;};case "man"->switch(metadata){case 3->90;case 5->180;case 2->270;default->0;};case "mike","fleija","prototype"->switch(metadata){case 5->90;case 2->180;case 4->270;default->0;};default->switch(metadata){case 2->90;case 4->180;case 3->270;default->0;};};if(kind.equals("n2")||kind.equals("solinium"))poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.YP.rotationDegrees(angle));if(kind.equals("boy")&&metadata>=2&&metadata<=5)poses.translate(-2,0,0);draw(kind,poses,buffers,light,overlay);poses.popPose();
 }
 @Override public boolean shouldRenderOffScreen(NuclearBombBlockEntity bomb){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(NuclearBombBlockEntity bomb){return AABB.INFINITE;}
}
