package com.hbm.client.reactor.iter.torus;
import com.hbm.client.fensu.FensuRenderTypes;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.iter.*;
import com.hbm.machine.reactor.iter.torus.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original HFR Torus/Magnet/Bolts matrices and three distinct UV-scrolling additive plasma layers. */
public final class TorusRenderer implements BlockEntityRenderer<TorusEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/fusion/torus.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/torus.png");
 static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 public TorusRenderer(BlockEntityRendererProvider.Context context){}
 static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,name);}
 @Override public void render(TorusEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var transform=new org.joml.Matrix4f().translation(.5F,0,.5F);if(tile.tilted)transform.translate(0,-1,0).rotateZ((float)Math.toRadians(10)).rotateY((float)Math.toRadians(5));LegacyContactShadows.render(tile,mesh(),transform,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);if(tile.tilted){pose.translate(0,-1,0);pose.mulPose(Axis.ZP.rotationDegrees(10));pose.mulPose(Axis.YP.rotationDegrees(5));}part("Torus",pose,buffers,light,overlay);pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(tile.prevMagnet+(tile.magnet-tile.prevMagnet)*partial));part("Magnet",pose,buffers,light,overlay);pose.popPose();String[] bolts={"Bolts2","Bolts4","Bolts3","Bolts1"};for(int i=0;i<4;i++)if(tile.connections[i])part(bolts[i],pose,buffers,light,overlay);var recipe=(FusionRecipe)tile.fusionModule.getRecipe();if(tile.plasmaEnergy>0&&recipe!=null){double time=Util.getMillis()+tile.timeOffset;float alpha=.35F+(float)Math.sin(time/1000D)*.25F;double main=Math.sin(Math.PI/2D*Math.cos(time/1000D))%1D;plasma("plasma",pose,buffers,overlay,recipe.r(),recipe.g(),recipe.b(),alpha,0,main);var player=Minecraft.getInstance().player;var pos=tile.getBlockPos();if(player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+2.5,pos.getZ()+.5)<10000){plasma("plasma_glow",pose,buffers,overlay,recipe.r()*2,recipe.g()*2,recipe.b()*2,alpha*2,0,Math.sin(time/2000D)%1D+time/10000D%1D);plasma("plasma_sparkle",pose,buffers,overlay,recipe.r()*2,recipe.g()*2,recipe.b()*2,.75F,time/500D*-1%1D,Math.sin(time/1000D)*.5D%1D);}}pose.popPose();}
 private static void plasma(String texture,PoseStack pose,MultiBufferSource buffers,int overlay,float r,float g,float b,float a,double u,double v){var type=FensuRenderTypes.plasma(ResourceLocation.parse("hbm:textures/models/fusion/"+texture+".png"));var out=FensuRenderTypes.unlitVertices(buffers.getBuffer(type),(float)u,(float)v);int argb=channel(a)<<24|channel(r)<<16|channel(g)<<8|channel(b);LegacyObjRenderer.renderOnly(mesh(),pose,out,LightTexture.FULL_BRIGHT,overlay,argb,LegacyObjRenderer.Convention.HFR,"Plasma");}
 private static int channel(float value){return (int)(Mth.clamp(value,0,1)*255);}
 @Override public boolean shouldRenderOffScreen(TorusEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(TorusEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-8,pos.getY(),pos.getZ()-8,pos.getX()+9,pos.getY()+5,pos.getZ()+9);}
}
