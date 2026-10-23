package com.hbm.client.effect;

import com.hbm.entity.effect.BlackHoleContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BlackHoleClient {
    static ShaderInstance sphereShader,ringShader,jetsShader;
    static ShaderInstance ringShader(){com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.setupShader(ringShader);return ringShader;}
    static ShaderInstance jetsShader(){com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.setupShader(jetsShader);return jetsShader;}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(BlackHoleContent.BLACK_HOLE.get(),BlackHoleRenderer::new);event.registerEntityRenderer(BlackHoleContent.VORTEX.get(),BlackHoleRenderer::new);event.registerEntityRenderer(BlackHoleContent.RAGING_VORTEX.get(),BlackHoleRenderer::new);event.registerEntityRenderer(BlackHoleContent.QUASAR.get(),BlackHoleRenderer::new);}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:black_hole_sphere"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->sphereShader=shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:black_hole_ring"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->ringShader=shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:black_hole_jets"),DefaultVertexFormat.POSITION_COLOR),shader->jetsShader=shader);
    }
    private BlackHoleClient(){}
}
