package com.hbm.client.pylon;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

/** Original unculled crossed ribbons use their explicit vertex color and sampled lightmap. */
final class WireRenderTypes extends RenderType {
    private static final Map<ResourceLocation,RenderType> TYPES=new ConcurrentHashMap<>();
    private WireRenderTypes(String name,VertexFormat format,VertexFormat.Mode mode,int size,boolean crumbling,boolean sorting,Runnable setup,Runnable clear) { super(name,format,mode,size,crumbling,sorting,setup,clear); }
    static RenderType wire(ResourceLocation texture) {
        return TYPES.computeIfAbsent(texture,value->create("hbm_wire_"+value,DefaultVertexFormat.BLOCK,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder().setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(new TextureStateShard(value,false,false)).setLightmapState(LIGHTMAP).setCullState(NO_CULL).createCompositeState(false)));
    }
}
