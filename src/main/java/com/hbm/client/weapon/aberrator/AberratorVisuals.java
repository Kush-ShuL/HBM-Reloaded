package com.hbm.client.weapon.aberrator;
import com.hbm.client.weapon.legendary.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
/** Actual source Aberrator/EOTT two-plume fireball and the single gun's sixteen rotating vanilla golden-sword sprites. */
final class AberratorVisuals {
 static void fireball(long lastShot){long age=System.currentTimeMillis()-lastShot;if(age>=150)return;double elapsed=age/150D,height=5*elapsed,length=10*elapsed,offset=elapsed;
  var f=LegacyGunGL.frame();var out=f.buffers().getBuffer(LegacyGunSmokeType.GAP);
  for(double end:new double[]{1.125,-1.125})for(double[] v:new double[][]{{height,-offset,0,0,1},{-height,-offset,0,1,1},{-height,-offset+length,end,1,0},{height,-offset+length,end,0,0}})out.addVertex(f.pose().last(),(float)v[0],(float)v[1],(float)v[2]).setColor(-1).setUv((float)v[3],(float)v[4]).setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY).setLight(240).setNormal(f.pose().last(),0,1,0);
 }
 static void swordRing(double equip,double recoil,double roll,float partial){var f=LegacyGunGL.frame();var pose=f.pose();var sprite=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.withDefaultNamespace("item/golden_sword"));var out=f.buffers().getBuffer(SwordLayer.TYPE);
  pose.pushPose();try{pose.translate(0,2,4.5);pose.mulPose(Axis.ZP.rotationDegrees((float)-roll));pose.mulPose(Axis.XP.rotationDegrees((float)-recoil));pose.mulPose(Axis.XP.rotationDegrees((float)-equip));pose.mulPose(Axis.ZP.rotationDegrees((float)(System.currentTimeMillis()/50D%360D)));
   float aim=Math.min(1F,(ItemGunBaseNT.prevAimingProgress+(ItemGunBaseNT.aimingProgress-ItemGunBaseNT.prevAimingProgress)*partial)*2);
   for(int i=0;i<16;i++){pose.pushPose();pose.translate(0,-1.5-aim,0);pose.mulPose(Axis.XP.rotationDegrees(90*aim));pose.mulPose(Axis.ZP.rotationDegrees(-45));
    float[][] quad={{-.5F,-.5F,sprite.getU1(),sprite.getV1()},{.5F,-.5F,sprite.getU0(),sprite.getV1()},{.5F,.5F,sprite.getU0(),sprite.getV0()},{-.5F,.5F,sprite.getU1(),sprite.getV0()}};for(var v:quad)out.addVertex(pose.last(),v[0],v[1],-.5F).setColor(-1).setUv(v[2],v[3]).setLight(f.light());pose.popPose();pose.mulPose(Axis.ZP.rotationDegrees(360F/16));}
  }finally{pose.popPose();}
 }
 /** Source disabled diffuse lighting but retained the actual lightmap/depth; vanilla text shader has precisely these inputs, without a fake extruded item model. */
 private static final class SwordLayer extends RenderType {
  SwordLayer(){super("hbm_aberrator_swords_unused",DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,VertexFormat.Mode.QUADS,2048,false,false,()->{},()->{});}
  static final RenderType TYPE=create("hbm_aberrator_swords",DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS,false,false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setCullState(NO_CULL).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
 }
 private AberratorVisuals(){}
}
