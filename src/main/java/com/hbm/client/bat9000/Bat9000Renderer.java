package com.hbm.client.bat9000;
import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.bat9000.Bat9000BlockEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original unrotated smooth BAT9000 HFR,4diamonds+4opaque untextured level windows. */
public final class Bat9000Renderer implements BlockEntityRenderer<Bat9000BlockEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/bat9000.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/bat9000.png");
 public Bat9000Renderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(Bat9000BlockEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);model(pose,buffers,light,overlay);var fluid=machine.tank.getTankType();if(fluid!=HbmFluids.NONE){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(45));for(int i=0;i<4;i++){pose.pushPose();pose.translate(2.5,2.25,0);pose.scale(1,.75F,.75F);LegacyDangerDiamond.render(pose,buffers,LightTexture.FULL_BRIGHT,fluid);pose.popPose();pose.mulPose(Axis.YP.rotationDegrees(90));}pose.popPose();}
  double height=machine.tank.getFill()*1.5D/machine.tank.getMaxFill();int color=0xff000000|fluid.getColor()&0xffffff;var out=buffers.getBuffer(Bat9000RenderTypes.WINDOW);window(pose,out,color,-2.2,height,false);window(pose,out,color,2.2,height,false);window(pose,out,color,-2.2,height,true);window(pose,out,color,2.2,height,true);pose.popPose();
 }
 private static void window(PoseStack poses,VertexConsumer out,int color,double fixed,double height,boolean z){for(double[] point:new double[][]{{-.5,1.5},{-.5,1.5+height},{.5,1.5+height},{.5,1.5}}){float x=(float)(z?point[0]:fixed),y=(float)point[1],d=(float)(z?fixed:point[0]);out.addVertex(poses.last(),x,y,d).setColor(color);}}
 @Override public boolean shouldRenderOffScreen(Bat9000BlockEntity m){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(Bat9000BlockEntity m){var p=m.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+5,p.getZ()+3);}
}
