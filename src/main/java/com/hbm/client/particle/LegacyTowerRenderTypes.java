package com.hbm.client.particle;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Source layer1 smoke participates in the existing native-cloud split without writing opaque colour-pass depth. */
final class LegacyTowerRenderTypes {
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/block/particle/particle_base.png");
 private static final ArrayList<LegacyCoolingTowerParticle> PENDING=new ArrayList<>();private static final Tesselator TESSELLATOR=new Tesselator(4096);private static BufferBuilder vertices;private static boolean drawing;private static float partial;private static float fogStart,fogEnd;private static float[] fogColor={0,0,0,0};private static com.mojang.blaze3d.shaders.FogShape fogShape=com.mojang.blaze3d.shaders.FogShape.SPHERE;
 static final ParticleRenderType ORDERED=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){PENDING.clear();fogStart=RenderSystem.getShaderFogStart();fogEnd=RenderSystem.getShaderFogEnd();fogColor=RenderSystem.getShaderFogColor().clone();fogShape=RenderSystem.getShaderFogShape();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
 static void reset(){PENDING.clear();if(vertices!=null){var mesh=vertices.build();if(mesh!=null)mesh.close();vertices=null;}drawing=false;}
 static boolean defer(LegacyCoolingTowerParticle particle,float partialTick){if(drawing)return false;PENDING.add(particle);partial=partialTick;NuclearTransparencyLayers.request();return true;}
 static VertexConsumer vertices(){return vertices;}
 static void renderDeferred(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||PENDING.isEmpty())return;if(WorldEffectsForward.defer(()->{try{renderPass(event,false);}finally{PENDING.clear();}}))return;if(NuclearTransparencyLayers.active()){NuclearTransparencyLayers.bindNuclearLayer();try{renderPass(event,false);}catch(RuntimeException|Error failure){PENDING.clear();throw failure;}finally{NuclearTransparencyLayers.restoreAfterNuclear();}NuclearTransparencyLayers.deferBehind(()->{NuclearTransparencyLayers.bindNuclearLayer();try{renderPass(event,false);}finally{PENDING.clear();NuclearTransparencyLayers.restoreAfterNuclear();}});}else{boolean fabulous=Minecraft.useShaderTransparency();if(fabulous)RenderStateShard.PARTICLES_TARGET.setupRenderState();try{renderPass(event,fabulous);}finally{PENDING.clear();if(fabulous){RenderStateShard.PARTICLES_TARGET.clearRenderState();RenderStateShard.WEATHER_TARGET.setupRenderState();}}}}
 private static void begin(boolean depthOnly){RenderSystem.setShader(LegacyTowerClient::shader);RenderSystem.setShaderTexture(0,TEXTURE);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);else RenderSystem.defaultBlendFunc();RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(depthOnly);RenderSystem.disableCull();vertices=TESSELLATOR.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}
 private static void flush(){var buffer=vertices;vertices=null;var mesh=buffer.build();if(mesh!=null)BufferUploader.drawWithShader(mesh);}
 private static void renderPass(RenderLevelStageEvent event,boolean fabulous){var mc=Minecraft.getInstance();float oldStart=RenderSystem.getShaderFogStart(),oldEnd=RenderSystem.getShaderFogEnd();float[] oldColor=RenderSystem.getShaderFogColor().clone();var oldShape=RenderSystem.getShaderFogShape();RenderSystem.setShaderFogStart(fogStart);RenderSystem.setShaderFogEnd(fogEnd);RenderSystem.setShaderFogColor(fogColor[0],fogColor[1],fogColor[2],fogColor[3]);RenderSystem.setShaderFogShape(fogShape);mc.gameRenderer.lightTexture().turnOnLightLayer();drawing=true;try{begin(false);for(var particle:PENDING)particle.render(null,event.getCamera(),partial);flush();if(fabulous){RenderSystem.colorMask(false,false,false,false);begin(true);for(var particle:PENDING)particle.render(null,event.getCamera(),partial);flush();}}finally{if(vertices!=null){var mesh=vertices.build();if(mesh!=null)mesh.close();vertices=null;}drawing=false;RenderSystem.colorMask(true,true,true,true);mc.gameRenderer.lightTexture().turnOffLightLayer();RenderSystem.enableCull();RenderSystem.depthMask(false);RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();RenderSystem.setShaderFogStart(oldStart);RenderSystem.setShaderFogEnd(oldEnd);RenderSystem.setShaderFogColor(oldColor[0],oldColor[1],oldColor[2],oldColor[3]);RenderSystem.setShaderFogShape(oldShape);}}
 private LegacyTowerRenderTypes(){}
}
