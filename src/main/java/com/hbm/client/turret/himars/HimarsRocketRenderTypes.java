package com.hbm.client.turret.himars;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Source rocket model alone disables fog; original textures/lightmap are retained. Smoke uses the existing ordered cloud queue. */
final class HimarsRocketRenderTypes extends RenderType {
 private HimarsRocketRenderTypes(){super("hbm_himars_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 private static float fogStart,fogEnd;
 private static final LayeringStateShard NO_FOG=new LayeringStateShard("hbm_himars_no_fog",()->{fogStart=RenderSystem.getShaderFogStart();fogEnd=RenderSystem.getShaderFogEnd();RenderSystem.setShaderFogStart(Float.MAX_VALUE);RenderSystem.setShaderFogEnd(Float.MAX_VALUE);},()->{RenderSystem.setShaderFogStart(fogStart);RenderSystem.setShaderFogEnd(fogEnd);});
 private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
 static RenderType type(ResourceLocation texture){return TYPES.computeIfAbsent(texture,key->create("hbm_himars_rocket",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->net.minecraft.client.renderer.GameRenderer.getRendertypeEntitySolidShader())).setTextureState(new TextureStateShard(key,false,false)).setLayeringState(NO_FOG).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));}
}
