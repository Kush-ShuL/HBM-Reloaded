package com.hbm.client.geyser;
import com.hbm.entity.particle.EntityOrangeFX;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
/** Original MultiCloudRenderer: five fixed-shape100 sub-clouds, original entity-seeded shade, eight age frames. */
public final class AgentOrangeRenderer extends EntityRenderer<EntityOrangeFX> {
 public AgentOrangeRenderer(EntityRendererProvider.Context context){super(context);}
 @Override public void render(EntityOrangeFX entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){int frame=0,age=entity.particleAge,step=entity.maxAge/8;if(age<=entity.maxAge&&age>=step*7)frame=7;for(int i=6;i>=1;i--)if(age<step*(i+1)&&age>=step*i)frame=i;if(age<step&&age>=0)frame=0;var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.parse("hbm:item/orange"+(frame+1)));var vertex=buffers.getBuffer(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));poses.pushPose();poses.scale(.5F,.5F,.5F);poses.scale(7.5F,7.5F,7.5F);var shade=new Random(entity.hashCode());var shape=new Random(100);for(int i=0;i<5;i++){float color=(float)(1-shade.nextInt(10)*.05);float dx=(float)((shape.nextGaussian()-1)*.15),dy=(float)((shape.nextGaussian()-1)*.15),dz=(float)((shape.nextGaussian()-1)*.15);float size=(float)(shape.nextDouble()*.5+.25);poses.pushPose();poses.translate(dx,dy,dz);poses.scale(size,size,size);poses.mulPose(Axis.YP.rotationDegrees(180-entityRenderDispatcher.camera.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(-entityRenderDispatcher.camera.getXRot()));emit(vertex,poses,-.5F,-.25F,sprite.getU0(),sprite.getV1(),color,light);emit(vertex,poses,.5F,-.25F,sprite.getU1(),sprite.getV1(),color,light);emit(vertex,poses,.5F,.75F,sprite.getU1(),sprite.getV0(),color,light);emit(vertex,poses,-.5F,.75F,sprite.getU0(),sprite.getV0(),color,light);poses.popPose();}poses.popPose();}
 private static void emit(com.mojang.blaze3d.vertex.VertexConsumer out,PoseStack poses,float x,float y,float u,float v,float gray,int light){out.addVertex(poses.last().pose(),x,y,0).setColor(gray,gray,gray,1).setUv(u,v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(poses.last(),0,1,0);}
 @Override public ResourceLocation getTextureLocation(EntityOrangeFX entity){return TextureAtlas.LOCATION_BLOCKS;}
}
