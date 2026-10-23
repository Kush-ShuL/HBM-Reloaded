package com.hbm.client.arcfurnace;
import com.hbm.client.model.*;
import com.hbm.machine.arcfurnace.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Unchanged original HFR mesh/groups, lid interpolation/jitter, electrode heat states and individual cable pivots. */
public final class ArcFurnaceRenderer implements BlockEntityRenderer<ArcFurnaceBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/arc_furnace.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/arc_furnace.png");
 public ArcFurnaceRenderer(BlockEntityRendererProvider.Context context){}
 static void parts(PoseStack pose,MultiBufferSource buffers,int light,int overlay,boolean hot,boolean noCull,String... parts){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var vertices=hot?ArcFurnaceRenderTypes.unlitVertices(buffers.getBuffer(ArcFurnaceRenderTypes.unlit(TEXTURE))):buffers.getBuffer(noCull?RenderType.entityCutoutNoCull(TEXTURE):RenderType.entityCutout(TEXTURE));LegacyObjRenderer.renderOnly(mesh,pose,vertices,hot?LightTexture.FULL_BRIGHT:light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,parts);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(.5F,.5F,.5F);parts(pose,buffers,light,overlay,false,false,"Furnace","Lid","Ring1","Ring2","Ring3","Electrode1","Electrode2","Electrode3","Cable1","Cable2","Cable3");}
 @Override public void render(ArcFurnaceBlockEntity arc,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float angle=switch(DummyableBlock.facing(arc.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(arc,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(angle));float lift=arc.prevLid+(arc.lid-arc.prevLid)*partial;double time=arc.getLevel().getGameTime()+partial;
  parts(pose,buffers,light,overlay,false,false,"Furnace");if(!arc.liquids.isEmpty()){pose.pushPose();pose.translate(0,-1.75+arc.stackAmount()*1.75/ArcFurnaceBlockEntity.MAX_LIQUID,0);parts(pose,buffers,light,overlay,true,true,"ContentsHot");pose.popPose();}else if(arc.hasMaterial)parts(pose,buffers,light,overlay,false,false,"ContentsCold");
  pose.translate(0,2*lift,0);if(arc.isProgressing)pose.translate(0,0,Math.sin(time)*.005);parts(pose,buffers,light,overlay,false,!arc.liquids.isEmpty(),"Lid");
  for(int i=0;i<3;i++){int n=i+1;if(arc.electrodes[i]!=0)parts(pose,buffers,light,overlay,false,!arc.liquids.isEmpty(),"Ring"+n);if(arc.electrodes[i]==1)parts(pose,buffers,light,overlay,false,!arc.liquids.isEmpty(),"Electrode"+n);}
  for(int i=0;i<3;i++){int n=i+1;if(arc.electrodes[i]==2)parts(pose,buffers,light,overlay,true,true,"Electrode"+n+"Hot");if(arc.electrodes[i]==3)parts(pose,buffers,light,overlay,true,true,"Electrode"+n+"Short");}
  for(int i=0;i<3;i++)if(arc.electrodes[i]!=0){double z=.5-i*.5;pose.pushPose();pose.translate(0,5.5,z);if(arc.isProgressing)pose.mulPose(Axis.XP.rotationDegrees((float)(Math.sin(time/2)*30)));pose.translate(0,-5.5,-z);parts(pose,buffers,light,overlay,false,true,"Cable"+(i+1));pose.popPose();}pose.popPose();
 }
 @Override public boolean shouldRenderOffScreen(ArcFurnaceBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ArcFurnaceBlockEntity arc){var pos=arc.getBlockPos();return new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+8,pos.getZ()+4);}
}
