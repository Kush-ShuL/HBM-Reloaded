package com.hbm.client.oilmixer;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
/** Source lit, untextured, .75-alpha Fluid OBJ: color-only writes and ordinary alpha blending. */
final class MixerRenderTypes extends RenderType {
 private MixerRenderTypes(String name,VertexFormat format,VertexFormat.Mode mode,int size,boolean crumbling,boolean sorting,Runnable setup,Runnable clear){super(name,format,mode,size,crumbling,sorting,setup,clear);}
 private static final TransparencyStateShard SOURCE_BLEND=new TransparencyStateShard("hbm_mixer_alpha",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
 static final RenderType FLUID=create("hbm_mixer_untextured_fluid",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,1536,false,true,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/machines/mixer_untextured.png"),false,false)).setTransparencyState(SOURCE_BLEND).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
}
