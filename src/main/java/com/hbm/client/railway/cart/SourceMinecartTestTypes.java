package com.hbm.client.railway.cart;

import com.hbm.client.render.WorldEffectMeshes;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

/** Actual RenderMinecartTest untextured, unlit white flash: source SRC_ALPHA/DST_ALPHA. */
final class SourceMinecartTestTypes extends RenderType {
    private SourceMinecartTestTypes(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType FLASH=WorldEffectMeshes.register(create("hbm_source_minecart_test_flash",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,1536,false,true,
        CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(new TransparencyStateShard("source_test_cart_flash",()->{
            RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.DST_ALPHA);
        },()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();})).createCompositeState(false)),"RenderMinecartTest/LilBoyWhitePulse");
}
