package com.hbm.client.entity.sourcecreepers;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class SourceCreeperRenderTypes extends RenderType {
 private SourceCreeperRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 static RenderType power(ResourceLocation texture,float offset,boolean invisible){return create("hbm_source_creeper_power",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new TextureStateShard(texture,false,false)).setTexturingState(new OffsetTexturingStateShard(offset,offset)).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(invisible?COLOR_WRITE:COLOR_DEPTH_WRITE).createCompositeState(false));}
}
