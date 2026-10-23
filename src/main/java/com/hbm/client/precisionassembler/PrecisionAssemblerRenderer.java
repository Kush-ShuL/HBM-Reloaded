package com.hbm.client.precisionassembler;
import com.hbm.client.model.*;
import com.hbm.machine.precisionassembler.PrecisionAssemblerBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.AABB;
/** Original RenderPrecAss matrices, Ring2 and four copies of the actual first arm OBJ hierarchy. */
public final class PrecisionAssemblerRenderer implements BlockEntityRenderer<PrecisionAssemblerBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/assembly_machine.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/precass.png");
 public PrecisionAssemblerRenderer(BlockEntityRendererProvider.Context context){}
 private static void part(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String name){LegacyObjRenderer.renderOnly(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 private static void pivot(PoseStack poses,double y,double z,double angle){poses.translate(0,y,z);poses.mulPose(Axis.XP.rotationDegrees((float)angle));poses.translate(0,-y,-z);}
 private static void arm(PoseStack poses,MultiBufferSource buffers,int light,int overlay,double[] angles,double striker){poses.pushPose();pivot(poses,1.625,.9375,angles[0]);part(poses,buffers,light,overlay,"ArmLower1");pivot(poses,2.375,.9375,angles[1]);part(poses,buffers,light,overlay,"ArmUpper1");pivot(poses,2.375,.4375,angles[2]);part(poses,buffers,light,overlay,"Head1");poses.translate(0,striker,0);part(poses,buffers,light,overlay,"Spike1");poses.popPose();}
 static void all(PoseStack poses,MultiBufferSource buffers,int light,int overlay){part(poses,buffers,light,overlay,"Base");part(poses,buffers,light,overlay,"Frame");part(poses,buffers,light,overlay,"Ring");part(poses,buffers,light,overlay,"Ring2");double[] angles={45,-30,45};for(int i=0;i<4;i++){arm(poses,buffers,light,overlay,angles,0);poses.mulPose(Axis.YP.rotationDegrees(90));}}
 @Override public void render(PrecisionAssemblerBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
  int angle=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};
  LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);
  var state=PrecisionAssemblerAnimation.get(machine);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));part(poses,buffers,light,overlay,"Base");if(state.frame)part(poses,buffers,light,overlay,"Frame");
  poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees((float)(state.prevRing+(state.ring-state.prevRing)*partial)));part(poses,buffers,light,overlay,"Ring");part(poses,buffers,light,overlay,"Ring2");double[] angles={state.arm(0,partial),state.arm(1,partial),state.arm(2,partial)};for(int i=0;i<4;i++){arm(poses,buffers,light,overlay,angles,state.striker(i,partial));poses.mulPose(Axis.YP.rotationDegrees(-90));}poses.popPose();
  var recipe=machine.module.getRecipe();var player=Minecraft.getInstance().player;var pos=machine.getBlockPos();
  if(recipe!=null&&player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5)<1225){poses.mulPose(Axis.YP.rotationDegrees(90));poses.translate(0,1.0625,0);var stack=recipe.icon().copyWithCount(1);if(stack.getItem() instanceof BlockItem){boolean model3d=Minecraft.getInstance().getItemRenderer().getModel(stack,machine.getLevel(),null,0).isGui3d();poses.translate(0,model3d?-.0625:-.125,0);if(!model3d)poses.scale(.5F,.5F,.5F);}else{poses.mulPose(Axis.XP.rotationDegrees(-90));poses.translate(0,-.25,0);}poses.scale(1.25F,1.25F,1.25F);Minecraft.getInstance().getItemRenderer().renderStatic(stack,ItemDisplayContext.FIXED,light,overlay,poses,buffers,machine.getLevel(),0);}
  poses.popPose();
 }
 @Override public boolean shouldRenderOffScreen(PrecisionAssemblerBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PrecisionAssemblerBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
