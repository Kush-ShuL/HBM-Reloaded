package com.hbm.client.bomb.nuclear;
import com.hbm.registry.HbmSounds;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearClientEffects {
 public static long flashTimestamp,shakeTimestamp;
 private NuclearClientEffects(){}
 public static boolean crosshairFlashActive(){return flashTimestamp+5000-System.currentTimeMillis()>0&&com.hbm.client.config.ClientEffectsConfig.nuclearHudFlashEnabled();}
 public static Player player(){return Minecraft.getInstance().player;}
 public static void lightningFlash(){if(Minecraft.getInstance().level!=null)Minecraft.getInstance().level.setSkyFlashTime(2);}
 public static void playExplosion(double x,double y,double z){var world=Minecraft.getInstance().level;if(world!=null)world.playLocalSound(x,y,z,HbmSounds.get("weapon.nuclearExplosion"),SoundSource.BLOCKS,10000F,1F,false);}
 // GuiLayerManager owns the enclosing HUD pose; its final pop also restores this translation.
 @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST)
 public static void shakeHud(RenderGuiLayerEvent.Pre event){
  if(!event.getName().equals(VanillaGuiLayers.HOTBAR)
    ||!com.hbm.client.config.ClientEffectsConfig.nuclearHudShakeEnabled())return;
  long now=System.currentTimeMillis(),remaining=shakeTimestamp+1500L-now;
  if(remaining<=0)return;
  double multiplier=remaining/1500D*2D;
  double horizontal=net.minecraft.util.Mth.clamp(Math.sin(now*.02D),-.7D,.7D)*15D;
  double vertical=net.minecraft.util.Mth.clamp(Math.sin(now*.01D+2D),-.7D,.7D)*3D;
  event.getGuiGraphics().pose().translate(horizontal*multiplier,vertical*multiplier,0);
 }
 @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST) public static void overlay(RenderGuiLayerEvent.Pre event){
  if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR)||!crosshairFlashActive())return;long remaining=flashTimestamp+5000-System.currentTimeMillis();
  var graphics=event.getGuiGraphics();graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);RenderSystem.depthMask(false);
  var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);float brightness=remaining/5000F;var matrix=graphics.pose().last().pose();int width=graphics.guiWidth(),height=graphics.guiHeight();buffer.addVertex(matrix,width,0,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,0,0,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,0,height,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,width,height,0).setColor(1F,1F,1F,brightness);BufferUploader.drawWithShader(buffer.buildOrThrow());RenderSystem.defaultBlendFunc();RenderSystem.depthMask(true);
 }
}
