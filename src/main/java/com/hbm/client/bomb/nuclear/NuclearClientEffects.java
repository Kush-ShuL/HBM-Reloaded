package com.hbm.client.bomb.nuclear;
import com.google.gson.JsonParser;
import com.hbm.registry.HbmSounds;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearClientEffects {
 public static long flashTimestamp,shakeTimestamp;
 private static Boolean flashEnabled;
 private NuclearClientEffects(){}
 public static Player player(){return Minecraft.getInstance().player;}
 public static void lightningFlash(){if(Minecraft.getInstance().level!=null)Minecraft.getInstance().level.setSkyFlashTime(2);}
 public static void playExplosion(double x,double y,double z){var world=Minecraft.getInstance().level;if(world!=null)world.playLocalSound(x,y,z,HbmSounds.get("weapon.nuclearExplosion"),SoundSource.BLOCKS,10000F,1F,false);}
 private static boolean enabled(){if(flashEnabled!=null)return flashEnabled;flashEnabled=true;var path=FMLPaths.CONFIGDIR.get().resolve("hbmConfig/hbmClient.json");try{if(Files.exists(path)){var config=JsonParser.parseString(Files.readString(path)).getAsJsonObject();if(config.has("NUKE_HUD_FLASH"))flashEnabled=config.get("NUKE_HUD_FLASH").getAsBoolean();}}catch(Exception exception){com.hbm.HbmReloaded.LOGGER.error("Cannot read original nuclear HUD flash option",exception);}return flashEnabled;}
 @SubscribeEvent public static void overlay(RenderGuiLayerEvent.Pre event){
  long remaining=flashTimestamp+5000-System.currentTimeMillis();if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR)||remaining<=0||!enabled())return;
  var graphics=event.getGuiGraphics();graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);RenderSystem.depthMask(false);
  var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_COLOR);float brightness=remaining/5000F;var matrix=graphics.pose().last().pose();int width=graphics.guiWidth(),height=graphics.guiHeight();buffer.addVertex(matrix,width,0,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,0,0,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,0,height,0).setColor(1F,1F,1F,brightness);buffer.addVertex(matrix,width,height,0).setColor(1F,1F,1F,brightness);BufferUploader.drawWithShader(buffer.buildOrThrow());RenderSystem.defaultBlendFunc();RenderSystem.depthMask(true);
 }
}
