package com.hbm.client.entity.glyphid;
import com.hbm.entity.mob.glyphid.GlyphidContent;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.ShaderInstance;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GlyphidClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(GlyphidContent.GRUNT.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.BRAWLER.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.BOMBARDIER.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.BLASTER.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.BEHEMOTH.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.BRENDA.get(),GlyphidRenderer::new);event.registerEntityRenderer(GlyphidContent.WAYPOINT.get(),NoopRenderer::new);event.registerEntityRenderer(GlyphidContent.ACID.get(),ThrownItemRenderer::new);}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:glyphid_infestation"),DefaultVertexFormat.NEW_ENTITY),shader->GlyphidInfestationRenderType.shader=shader);}
    private GlyphidClient(){}
}
