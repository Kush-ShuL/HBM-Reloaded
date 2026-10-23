package com.hbm.client.crystallizer;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Original lit/culling-enabled fluid surface: ordinary alpha blend with depth writes disabled. */
final class CrystallizerRenderTypes extends RenderType {
 private CrystallizerRenderTypes(){super("unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
 private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
 static RenderType fluid(ResourceLocation texture){return TYPES.computeIfAbsent(texture,t->create("hbm_crystallizer_fluid_"+t,DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(t,false,false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));}
}
