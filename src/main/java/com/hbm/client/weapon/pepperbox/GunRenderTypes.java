package com.hbm.client.weapon.pepperbox;

import com.mojang.blaze3d.platform.GlStateManager;
import com.hbm.client.weapon.forward.GunForwardEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

/** Source smoke writes color only; muzzle flash uses SRC_ALPHA/ONE with no depth write. */
public final class GunRenderTypes extends RenderType {
    private GunRenderTypes(){super("hbm_gun_effects",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,false,()->{},()->{});}
    private static final TransparencyStateShard SMOKE_BLEND=new TransparencyStateShard("hbm_gun_smoke",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    private static final TransparencyStateShard FLASH_BLEND=new TransparencyStateShard("hbm_gun_flash",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    public static final RenderType SMOKE=type("hbm_gun_smoke",ResourceLocation.withDefaultNamespace("textures/misc/white.png"),SMOKE_BLEND);
    public static final RenderType FLASH=type("hbm_gun_flash",ResourceLocation.parse("hbm:textures/models/weapons/lilmac_plume.png"),FLASH_BLEND);
    /** Native off-pack supplier; enabled-pack additive materials use the real source forward draw. */
    public static ShaderInstance flashShader(){return GameRenderer.getRendertypeEntityTranslucentShader();}
    private static RenderType type(String name,ResourceLocation texture,TransparencyStateShard blend){RenderType type=create(name,DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(blend==FLASH_BLEND?new ShaderStateShard(GunRenderTypes::flashShader):RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(texture,false,false)).setTransparencyState(blend).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));return blend==FLASH_BLEND?GunForwardEffects.additive(type,texture,false,true,false):type;}
}
