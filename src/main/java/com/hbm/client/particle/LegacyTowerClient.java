package com.hbm.client.particle;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegacyTowerClient {
 private static ShaderInstance shader;static ShaderInstance shader(){NuclearTransparencyLayers.setupShader(shader);return shader;}
 @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{LegacyTowerRenderTypes.reset();event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:legacy_tower"),DefaultVertexFormat.PARTICLE),value->shader=value);}
 @EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
 public static final class WorldStage {
  /** Original cooling smoke is FX layer1, before the existing Muke layer3 queue. */
  @SubscribeEvent(priority=EventPriority.HIGH) public static void draw(RenderLevelStageEvent event){if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY)LegacyTowerRenderTypes.reset();else LegacyTowerRenderTypes.renderDeferred(event);}
  @SubscribeEvent public static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event){if(event.getLevel().isClientSide())LegacyTowerRenderTypes.reset();}
 }
 private LegacyTowerClient(){}
}
