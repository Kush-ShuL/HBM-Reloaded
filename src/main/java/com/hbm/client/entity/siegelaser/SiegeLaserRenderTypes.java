package com.hbm.client.entity.siegelaser;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;

final class SiegeLaserRenderTypes extends RenderType {
    static ShaderInstance shader;
    private static final TransparencyStateShard SOURCE_ADD=new TransparencyStateShard("siege_source_add",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();});
    static final RenderType TRIANGLES=type("hbm_siege_laser_front",VertexFormat.Mode.TRIANGLES),QUADS=type("hbm_siege_laser_tail",VertexFormat.Mode.QUADS);
    private static RenderType type(String name,VertexFormat.Mode mode){return WorldEffectMeshes.register(create(name,DefaultVertexFormat.POSITION_COLOR,mode,1536,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTransparencyState(SOURCE_ADD).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"SiegeLaser228/source additive dart");}
    private SiegeLaserRenderTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
