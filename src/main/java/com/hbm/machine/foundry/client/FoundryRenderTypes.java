package com.hbm.machine.foundry.client;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
/** Original cast surface uses an unlit opaque base and SRC_ALPHA/ONE white glow without depth writes. */
final class FoundryRenderTypes extends RenderType {
 private FoundryRenderTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 static final RenderType WORLD=create("hbm_foundry_world",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS,false,false)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false));
 /** The source GUI mold icon is flat, alpha blended and unlit by directional item lights; its front faces upward. */
 static final RenderType MOLD_ICON=create("hbm_foundry_mold_icon",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(TextureAtlas.LOCATION_BLOCKS,false,false)).setLightmapState(LIGHTMAP).setCullState(CULL).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
 private static final Map<ResourceLocation,RenderType> BASES=new HashMap<>(),GLOWS=new HashMap<>();
 static RenderType base(ResourceLocation texture){return BASES.computeIfAbsent(texture,id->create("hbm_foundry_surface",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(id,false,false)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false)));}
 static RenderType glow(ResourceLocation texture){return GLOWS.computeIfAbsent(texture,id->create("hbm_foundry_glow",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,true,CompositeState.builder().setShaderState(new ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(id,false,false)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setTransparencyState(new TransparencyStateShard("hbm_foundry_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();})).createCompositeState(false)));}
}
