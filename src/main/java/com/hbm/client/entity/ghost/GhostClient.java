package com.hbm.client.entity.ghost;

import com.hbm.entity.mob.ghost.GhostContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GhostClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(GhostContent.GHOST.get(),GhostRenderer::new);}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:ghost_source_alpha"),DefaultVertexFormat.NEW_ENTITY),shader->GhostRenderTypes.shader=shader);}
    private GhostClient(){}
}
