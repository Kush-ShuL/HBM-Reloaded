package com.hbm.client.bomb.nuclear;

import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/** Fancy cloud alpha belongs in a colour layer, rather than an opaque occluder in the scene depth. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearTransparencyLayers {
 private static TextureTarget clouds,nuclear,behind,scene;
 private static boolean captured,capturing,active,drawingBehind,requested;
 private static int captureDrawFbo,captureReadFbo;
 private static final int[] captureViewport=new int[4];
 private static final ArrayList<Runnable> BEHIND_PASSES=new ArrayList<>();
 static ShaderInstance compositor;
 private NuclearTransparencyLayers(){}
 public static boolean active(){return active;}
 /** Called while a source particle queue is gathered, before native clouds draw. */
 public static void request(){requested=true;}
 public static void deferBehind(Runnable pass){if(!active)throw new IllegalStateException("No nuclear colour layer is active");BEHIND_PASSES.add(pass);}
 public static void bindNuclearLayer(){if(active)(drawingBehind?behind:nuclear).bindWrite(false);}
 public static void restoreAfterNuclear(){bindNuclearLayer();}
 public static void setupShader(ShaderInstance shader){if(shader==null)return;shader.safeGetUniform("HbmCloudLayer").set(active?(drawingBehind?2F:1F):0F);if(active)RenderSystem.setShaderTexture(5,clouds.getDepthTextureId());}
 private static boolean wanted(){var mc=Minecraft.getInstance();if(compositor==null||mc.level==null||WorldEffectsForward.pack()||Minecraft.useShaderTransparency()||mc.options.getCloudsType()==net.minecraft.client.CloudStatus.OFF)return false;if(requested)return true;for(var entity:mc.level.entitiesForRendering())if(entity instanceof com.hbm.bomb.nuclear.NuclearTorex torex&&!torex.isRemoved()&&torex.visualReady())return true;return false;}
 private static void targets(){
  var main=Minecraft.getInstance().getMainRenderTarget();
  if(clouds==null){clouds=new TextureTarget(main.width,main.height,true,Minecraft.ON_OSX);nuclear=new TextureTarget(main.width,main.height,true,Minecraft.ON_OSX);behind=new TextureTarget(main.width,main.height,true,Minecraft.ON_OSX);scene=new TextureTarget(main.width,main.height,false,Minecraft.ON_OSX);clouds.setClearColor(0,0,0,0);nuclear.setClearColor(0,0,0,0);behind.setClearColor(0,0,0,0);scene.setClearColor(0,0,0,0);}
  else if(clouds.width!=main.width||clouds.height!=main.height){clouds.resize(main.width,main.height,Minecraft.ON_OSX);nuclear.resize(main.width,main.height,Minecraft.ON_OSX);behind.resize(main.width,main.height,Minecraft.ON_OSX);scene.resize(main.width,main.height,Minecraft.ON_OSX);}
  if(main.isStencilEnabled()){clouds.enableStencil();nuclear.enableStencil();behind.enableStencil();}
 }
 /** Call-site scope survives renderer cancellation; restore the caller's actual targets, including Iris. */
 public static void beginVanillaClouds(){
  capturing=false;if(!wanted())return;
  captureDrawFbo=GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);captureReadFbo=GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
  GL11.glGetIntegerv(GL11.GL_VIEWPORT,captureViewport);capturing=true;
  try{targets();var main=Minecraft.getInstance().getMainRenderTarget();clouds.clear(Minecraft.ON_OSX);clouds.copyDepthFrom(main);clouds.bindWrite(false);}
  catch(RuntimeException|Error failure){endVanillaClouds(false);throw failure;}
 }
 public static void endVanillaClouds(){endVanillaClouds(true);}
 public static void endVanillaClouds(boolean completed){
  if(!capturing)return;capturing=false;captured=completed;
  try{com.mojang.blaze3d.platform.GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,captureDrawFbo);}
  finally{try{com.mojang.blaze3d.platform.GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,captureReadFbo);}finally{RenderSystem.viewport(captureViewport[0],captureViewport[1],captureViewport[2],captureViewport[3]);}}
 }
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void begin(RenderLevelStageEvent event){
  if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY){captured=false;active=false;requested=false;BEHIND_PASSES.clear();return;}
  if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||!captured||!wanted())return;
  var main=Minecraft.getInstance().getMainRenderTarget();
  GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,main.frameBufferId);GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,scene.frameBufferId);
  GL30.glBlitFramebuffer(0,0,main.width,main.height,0,0,scene.width,scene.height,GL11.GL_COLOR_BUFFER_BIT,GL11.GL_NEAREST);
  behind.clear(Minecraft.ON_OSX);behind.copyDepthFrom(main);nuclear.clear(Minecraft.ON_OSX);nuclear.copyDepthFrom(main);nuclear.bindWrite(false);drawingBehind=false;active=true;
 }
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void finish(RenderLevelStageEvent event){
  if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||!active)return;
  var main=Minecraft.getInstance().getMainRenderTarget();
  try{
   drawingBehind=true;for(var pass:BEHIND_PASSES){behind.bindWrite(false);pass.run();}
   main.bindWrite(false);RenderSystem.disableDepthTest();RenderSystem.depthMask(false);RenderSystem.disableCull();RenderSystem.disableBlend();RenderSystem.colorMask(true,true,true,true);RenderSystem.setShaderColor(1,1,1,1);
   RenderSystem.setShader(()->compositor);RenderSystem.setShaderTexture(0,scene.getColorTextureId());RenderSystem.setShaderTexture(1,clouds.getColorTextureId());RenderSystem.setShaderTexture(2,nuclear.getColorTextureId());RenderSystem.setShaderTexture(3,behind.getColorTextureId());
   // Depth-writing smoke must occlude farther native clouds in the final colour composite.
   RenderSystem.setShaderTexture(4,nuclear.getDepthTextureId());RenderSystem.setShaderTexture(5,clouds.getDepthTextureId());
   var quad=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX);
   quad.addVertex(-1,-1,0).setUv(0,0);quad.addVertex(1,-1,0).setUv(1,0);quad.addVertex(1,1,0).setUv(1,1);quad.addVertex(-1,1,0).setUv(0,1);BufferUploader.drawWithShader(quad.buildOrThrow());
  }finally{BEHIND_PASSES.clear();active=false;drawingBehind=false;captured=false;main.bindWrite(false);RenderSystem.enableDepthTest();RenderSystem.enableCull();RenderSystem.depthMask(false);RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();}
 }
}
