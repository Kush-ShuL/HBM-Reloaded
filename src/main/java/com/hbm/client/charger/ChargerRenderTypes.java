package com.hbm.client.charger;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

/** The source's opaque untextured, unlit yellow lamp, retaining ordinary world fog. */
public final class ChargerRenderTypes extends RenderType {
    static ShaderInstance shader,slideShader;
    private ChargerRenderTypes(){super("hbm_charger_unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType LIGHT=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_charger_lamp",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,
        CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"Charger/SourceYellowLamp");
    static final RenderType SLIDE=create("hbm_charger_slide",DefaultVertexFormat.POSITION_TEX_COLOR,VertexFormat.Mode.QUADS,4096,false,false,
        CompositeState.builder().setShaderState(new ShaderStateShard(()->slideShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/machines/charger.png"),false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
}
