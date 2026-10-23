package com.hbm.client.meteor;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.GameRenderer;

/** RenderMeteor disables lighting and culling; no light-map multiplication or face shading. */
final class MeteorRenderTypes extends RenderType {
    private MeteorRenderTypes(){super("unused",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType METEOR=create("hbm_source_meteor",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder()
            .setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader)).setTextureState(new TextureStateShard(MeteorRenderer.TEXTURE,false,false)).setCullState(NO_CULL).createCompositeState(false));
}
