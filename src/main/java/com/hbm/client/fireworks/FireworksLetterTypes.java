package com.hbm.client.fireworks;

import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.hbm.client.weapon.forward.mixin.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

/** Only this source letter's font buffers are mapped; global GUI/font/model RenderTypes are not registered or altered. */
final class FireworksLetterTypes extends RenderType {
    static ShaderInstance normal,intensity;
    private static final Map<Key,RenderType> TYPES=new HashMap<>();
    private record Key(ResourceLocation texture,boolean intensity){}
    private static final TransparencyStateShard SOURCE_BLEND=new TransparencyStateShard("hbm_fireworks_letter_source_alpha",()->{RenderSystem.enableBlend();if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);else RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static RenderType map(RenderType original){
        if(!(original instanceof ForwardCompositeTypeAccess access)||original.format()!=DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP)return original;
        var state=(ForwardCompositeStateAccess)(Object)access.hbm$forwardState();
        boolean gray=state.hbm$shader()==RENDERTYPE_TEXT_INTENSITY_SHADER;
        if(!gray&&state.hbm$shader()!=RENDERTYPE_TEXT_SHADER)return original;
        var texture=((ForwardTextureAccess)(Object)state.hbm$texture()).hbm$cutoutTexture();
        return texture.map(location->TYPES.computeIfAbsent(new Key(location,gray),FireworksLetterTypes::create)).orElse(original);
    }
    private static RenderType create(Key key){return create("hbm_source_fireworks_letter_"+key,DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->{var shader=key.intensity?intensity:normal;NuclearTransparencyLayers.setupShader(shader);return shader;})).setTextureState(new TextureStateShard(key.texture,false,false)).setTransparencyState(SOURCE_BLEND).setLightmapState(LIGHTMAP).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));}
    private FireworksLetterTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
