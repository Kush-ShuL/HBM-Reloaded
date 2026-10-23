package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

final class TorexRenderTypes extends RenderType {
    private static final TransparencyStateShard SOURCE_FLASH=new TransparencyStateShard("hbm_torex_source_flash",()->{com.mojang.blaze3d.systems.RenderSystem.enableBlend();com.mojang.blaze3d.systems.RenderSystem.blendFuncSeparate(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE,com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ZERO,com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);},()->{com.mojang.blaze3d.systems.RenderSystem.disableBlend();com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();});
    private TorexRenderTypes(String name,VertexFormat format,VertexFormat.Mode mode,int size,boolean crumbling,boolean sorting,Runnable setup,Runnable clear){super(name,format,mode,size,crumbling,sorting,setup,clear);}
    static RenderType cloud(ResourceLocation texture,boolean flash,boolean fabulous){return layer(texture,flash,fabulous,false);}
    static RenderType depth(ResourceLocation texture){return depth(texture,true);}
    static RenderType depth(ResourceLocation texture,boolean fabulous){return layer(texture,false,fabulous,true);}
    private static RenderType layer(ResourceLocation texture,boolean flash,boolean fabulous,boolean depth){
        return create("hbm_torex_"+(flash?"flash":"cloud")+(fabulous?"_particles":"_main")+(depth?"_depth":""),DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,262144,false,false,
            CompositeState.builder()
                // Source explicitly disables alpha testing and fog; vanilla's .1 cutoff removes the
                // condensation rings and fading edges. Muke uses the same original alpha>0 program.
                .setShaderState(new ShaderStateShard(()->{var shader=MukeClient.additiveShader;NuclearTransparencyLayers.setupShader(shader);return shader;}))
                .setTextureState(new TextureStateShard(texture,false,false))
                .setTransparencyState(depth?NO_TRANSPARENCY:flash?SOURCE_FLASH:TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(LEQUAL_DEPTH_TEST).setCullState(NO_CULL)
                .setOutputState(fabulous?PARTICLES_TARGET:MAIN_TARGET)
                // Keep every source colour layer, then write alpha>0 depth in a separate pass for
                // Fabulous composition. Distance ordering alone cannot replace view-axis depth.
                .setWriteMaskState(depth?DEPTH_WRITE:COLOR_WRITE)
                .createCompositeState(false));
    }
}
