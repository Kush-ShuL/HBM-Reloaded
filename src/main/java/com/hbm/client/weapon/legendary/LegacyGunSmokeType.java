package com.hbm.client.weapon.legendary;
import com.mojang.blaze3d.platform.GlStateManager;
import com.hbm.client.weapon.pepperbox.GunRenderTypes;
import com.hbm.client.weapon.forward.GunForwardEffects;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.bomb.nuclear.NuclearTransparencyLayers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
/** Source immediate smoke uses alpha>0; native entity-translucent's0.1 cutoff would shorten each plume. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegacyGunSmokeType extends RenderType {
    private static ShaderInstance shader;
    private LegacyGunSmokeType(){super("hbm_sedna_casing_smoke",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,8192,false,false,()->{},()->{});}
    private static final TransparencyStateShard BLEND=new TransparencyStateShard("hbm_sedna_source_smoke",()->{RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    private static final TransparencyStateShard WORLD_BLEND=new TransparencyStateShard("hbm_world_source_smoke",()->{RenderSystem.enableBlend();if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);else RenderSystem.defaultBlendFunc();},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    private static final WriteMaskStateShard WORLD_DEPTH=new WriteMaskStateShard(true,true){@Override public void setupRenderState(){RenderSystem.depthMask(MukeRenderTypes.writesDepth());}@Override public void clearRenderState(){RenderSystem.depthMask(false);}};
    private static ShaderInstance smokeShader(boolean world){if(world)NuclearTransparencyLayers.setupShader(shader);else if(shader!=null)shader.safeGetUniform("HbmCloudLayer").set(0F);return shader;}
    private static final TransparencyStateShard ADD=new TransparencyStateShard("hbm_sedna_gap_flash",()->{RenderSystem.enableBlend();RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);},()->{RenderSystem.disableBlend();RenderSystem.defaultBlendFunc();});
    public static final RenderType GAP=GunForwardEffects.additive(create("hbm_sedna_gap_flash",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GunRenderTypes::flashShader)).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/weapons/lilmac_plume.png"),false,false)).setTransparencyState(ADD).setCullState(NO_CULL).setWriteMaskState(COLOR_DEPTH_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)),ResourceLocation.parse("hbm:textures/models/weapons/lilmac_plume.png"),true,true,false);
    public static final RenderType TYPE=type("hbm_sedna_source_smoke",false),CASING=type("hbm_sedna_casing_smoke",true);
    private static RenderType type(String name,boolean world){return create(name,DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,8192,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->smokeShader(world))).setTextureState(new TextureStateShard(ResourceLocation.withDefaultNamespace("textures/misc/white.png"),false,false)).setTransparencyState(world?WORLD_BLEND:BLEND).setCullState(NO_CULL).setWriteMaskState(world?WORLD_DEPTH:COLOR_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:sedna_source_smoke"),DefaultVertexFormat.NEW_ENTITY),value->shader=value);}
}
