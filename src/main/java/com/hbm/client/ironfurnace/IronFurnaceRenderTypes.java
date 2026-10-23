package com.hbm.client.ironfurnace;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
/** Only the source On group disables lighting/culling. No additive blend or fullbright furnace body. */
final class IronFurnaceRenderTypes extends RenderType {
    private IronFurnaceRenderTypes(){super("unused",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType ON=create("hbm_iron_furnace_on",DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getParticleShader)).setTextureState(new TextureStateShard(IronFurnaceRenderer.TEXTURE,false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
}
