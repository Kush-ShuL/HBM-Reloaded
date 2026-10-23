package com.hbm.client.bobble;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

/** Each original fixed-function blend/cull/light state is retained, including additive Mellow lamps. */
final class BobbleRenderTypes extends RenderType {
    private BobbleRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static ShaderInstance litShader,unlitShader;
    private record Key(ResourceLocation texture,boolean cull,boolean blend,boolean additive,boolean unlit,boolean textured,boolean zeroAlpha,boolean depthWrite){}
    private static final Map<Key,RenderType> TYPES=new HashMap<>();
    static RenderType get(ResourceLocation texture,boolean cull,boolean blend,boolean additive,boolean unlit,boolean textured,boolean zeroAlpha,boolean depthWrite){return TYPES.computeIfAbsent(new Key(texture,cull,blend,additive,unlit,textured,zeroAlpha,depthWrite),key->{
        var shader=textured?(zeroAlpha?new ShaderStateShard(()->unlit?unlitShader:litShader):(unlit?RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER:RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)):POSITION_COLOR_SHADER;
        var state=CompositeState.builder().setShaderState(shader).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(depthWrite?COLOR_DEPTH_WRITE:COLOR_WRITE).setCullState(cull?CULL:NO_CULL).setTransparencyState(!blend?NO_TRANSPARENCY:additive?new TransparencyStateShard("hbm_bobble_additive",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();}):TRANSLUCENT_TRANSPARENCY);
        if(textured)state.setTextureState(new TextureStateShard(texture,false,false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY);
        return create("hbm_bobble",textured?DefaultVertexFormat.NEW_ENTITY:DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,1536,false,false,state.createCompositeState(false));
    });}
    private static final Map<RenderType,RenderType> LABELS=new IdentityHashMap<>();
    /** Original socket text tests depth but never writes depth. Do not use see-through font rendering. */
    static RenderType label(RenderType type){return LABELS.computeIfAbsent(type,Label::new);}
    private static final class Label extends RenderType {
        Label(RenderType original){super("hbm_bobble_label",original.format(),original.mode(),original.bufferSize(),false,false,()->{original.setupRenderState();RenderSystem.depthMask(false);},()->{RenderSystem.depthMask(true);original.clearRenderState();});}
    }
}
