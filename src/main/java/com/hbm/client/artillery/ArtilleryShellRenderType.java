package com.hbm.client.artillery;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Source RenderArtilleryShell disables fog just for the real textured/lighted mesh draw, then restores prior fog. */
final class ArtilleryShellRenderType extends RenderType {
    private ArtilleryShellRenderType(){super("hbm_arty_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static float start,end;
    private static final ShaderStateShard SHADER=new ShaderStateShard(()->net.minecraft.client.renderer.GameRenderer.getRendertypeEntitySolidShader());
    private static final LayeringStateShard NO_FOG=new LayeringStateShard("hbm_arty_no_fog",()->{start=RenderSystem.getShaderFogStart();end=RenderSystem.getShaderFogEnd();RenderSystem.setShaderFogStart(Float.MAX_VALUE);RenderSystem.setShaderFogEnd(Float.MAX_VALUE);},()->{RenderSystem.setShaderFogStart(start);RenderSystem.setShaderFogEnd(end);});
    static final RenderType TYPE=create("hbm_arty_shell",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(SHADER).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/projectiles/grenade.png"),false,false)).setLayeringState(NO_FOG).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
}
