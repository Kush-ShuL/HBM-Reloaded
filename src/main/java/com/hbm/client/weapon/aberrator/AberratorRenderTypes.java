package com.hbm.client.weapon.aberrator;
import com.mojang.blaze3d.vertex.*;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
/** Exact EOTT renderOther GL_GREATER0 alpha test; source remains lit/opaque/depth-writing with the authored HFR normals. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AberratorRenderTypes extends RenderType {
 private static ShaderInstance shader;private static final Map<ResourceLocation,RenderType> TYPES=new HashMap<>();
 private AberratorRenderTypes(){super("hbm_eott_alpha0_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,2048,false,false,()->{},()->{});}
 static RenderType alphaZero(ResourceLocation texture){return TYPES.computeIfAbsent(texture,t->create("hbm_eott_source_alpha0",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,2048,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(()->shader)).setTextureState(new TextureStateShard(t,false,false)).setTransparencyState(NO_TRANSPARENCY).setCullState(CULL).setWriteMaskState(COLOR_DEPTH_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)));}
 @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:aberrator_source_alpha0"),DefaultVertexFormat.NEW_ENTITY),value->shader=value);}
}
