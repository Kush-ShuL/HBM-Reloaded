package com.hbm.client.polarizedglass;

import com.hbm.client.environment.impact.ImpactClient;
import com.hbm.client.render.WorldEffectMeshes;
import com.hbm.client.render.WorldEffectsForward;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Original chainloader: textured colour, no fog/alpha discard, SRC_ALPHA/ONE and no depth writes. */
final class SourceSkyMarkerTypes extends RenderType {
    private SourceSkyMarkerTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final TransparencyStateShard SOURCE_BLEND=new TransparencyStateShard("hbm_source_sky_marker",()->{
        RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(770,1,1,0);
    },()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    // A sky overlay originally precedes terrain regardless of its authored100-unit sphere.
    // Late Iris replay must remain behind ALL written scene geometry, not sit in front of distant mountains.
    // The sole forward scope enters with range0..1 and restores its incoming state even on interruption.
    private static final LayeringStateShard SKY_DEPTH=new LayeringStateShard("hbm_source_sky_depth",()->{
        if(WorldEffectsForward.sourcePass())GL11.glDepthRange(1,1);
    },()->{if(WorldEffectsForward.sourcePass())GL11.glDepthRange(0,1);});
    static final RenderType LODE=marker("lode","hbm:textures/environment/polarizedglass/star_lode.png");
    static final RenderType DIGAMMA=marker("digamma","hbm:textures/environment/impact/star_digamma.png");
    static final RenderType BOBMAZON=marker("bobmazon","hbm:textures/environment/impact/sat_bobmazon.png");
    private static RenderType marker(String name,String texture){
        return WorldEffectMeshes.register(create("hbm_source_sky_"+name,DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder()
            .setShaderState(new ShaderStateShard(ImpactClient::skyShader))
            .setTextureState(new TextureStateShard(ResourceLocation.parse(texture),false,false))
            .setTransparencyState(SOURCE_BLEND).setCullState(CULL).setDepthTestState(LEQUAL_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE).setLayeringState(SKY_DEPTH).createCompositeState(false)),"SourceSky/"+name);
    }
}
