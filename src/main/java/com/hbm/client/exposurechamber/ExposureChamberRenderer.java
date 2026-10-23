package com.hbm.client.exposurechamber;
import com.hbm.client.model.*;
import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.hbm.machine.exposurechamber.ExposureChamberEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
/** Source HFR Chamber/Magnets/Core, original interpolation and all seven BeamPronter calls. */
public final class ExposureChamberRenderer implements BlockEntityRenderer<ExposureChamberEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/exposure_chamber.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/exposure_chamber.png");
 public ExposureChamberRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String name,boolean unlit){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(unlit?ExposureChamberRenderTypes.CORE:RenderType.entityCutoutNoCull(TEXTURE)),unlit?LightTexture.FULL_BRIGHT:light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 @Override public void render(ExposureChamberEntity chamber,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=switch(DummyableBlock.facing(chamber.getBlockState())){case WEST->180;case SOUTH->270;case NORTH->90;default->0;};double rotation=chamber.prevRotation+(chamber.rotation-chamber.prevRotation)*partial;LegacyContactShadows.render(chamber,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));part(pose,buffers,light,overlay,"Chamber",false);pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)rotation));part(pose,buffers,light,overlay,"Magnets",false);pose.popPose();if(chamber.isOn&&chamber.getLevel()!=null){long time=chamber.getLevel().getGameTime();pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)(rotation/2D)));pose.translate(0,Math.sin((time%(Math.PI*16D)+partial)*.125)*.0625,0);part(pose,buffers,light,overlay,"Core",true);pose.popPose();var random=new Random(time/8);int color=time%8>=4?0x80d0ff:0xffffff;random.nextInt(2);if(random.nextInt(2)==0)beam(pose,buffers,0,3.675,-7.5,new Vec3(0,0,5),true,color,(int)(System.currentTimeMillis()%1000)/50,15);if(random.nextInt(2)==0)beam(pose,buffers,1.1875,2.5,-7.5,new Vec3(0,0,5),true,color,(int)(System.currentTimeMillis()%1000)/50,15);if(random.nextInt(2)==0)beam(pose,buffers,-1.1875,2.5,-7.5,new Vec3(0,0,5),true,color,(int)(System.currentTimeMillis()%1000)/50,15);beam(pose,buffers,0,1.75,0,new Vec3(0,1.5,0),true,0x80d0ff,(int)(System.currentTimeMillis()%1000)/50,10);beam(pose,buffers,0,1.75,0,new Vec3(0,1.5,0),true,0x8080ff,(int)(System.currentTimeMillis()+5%1000)/50,10);beam(pose,buffers,0,2.5,0,new Vec3(0,0,-1),false,0xffff80,(int)(System.currentTimeMillis()%360),15);beam(pose,buffers,0,2.5,0,new Vec3(0,0,-1),false,0xff8080,(int)(System.currentTimeMillis()%360)+180,15);}pose.popPose();}
 private static void beam(PoseStack pose,MultiBufferSource buffers,double x,double y,double z,Vec3 skeleton,boolean random,int color,int seed,int segments){pose.pushPose();pose.translate(x,y,z);DfcComponentBeams.draw(pose,buffers,skeleton,random,false,color,0xffffff,seed,segments,.125F,1,0);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(ExposureChamberEntity chamber){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ExposureChamberEntity chamber){var p=chamber.getBlockPos();return new AABB(p.getX()-8,p.getY(),p.getZ()-8,p.getX()+9,p.getY()+5,p.getZ()+9);}
}
