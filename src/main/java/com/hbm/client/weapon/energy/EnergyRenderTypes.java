package com.hbm.client.weapon.energy;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import com.hbm.client.weapon.forward.GunForwardEffects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
final class EnergyRenderTypes extends RenderType {
    private EnergyRenderTypes(){super("hbm_energy_unused",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    static final RenderType FLASH=GunForwardEffects.additive(create("hbm_energy_flash",DefaultVertexFormat.NEW_ENTITY,VertexFormat.Mode.QUADS,256,false,false,CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new TextureStateShard(ResourceLocation.parse("hbm:textures/models/weapons/laser_flash.png"),false,false)).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false)),ResourceLocation.parse("hbm:textures/models/weapons/laser_flash.png"),false,true,false);
    static final RenderType BEAM=GunForwardEffects.additive(create("hbm_energy_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),ResourceLocation.withDefaultNamespace("textures/misc/white.png"),false,false,true);
    private static final WriteMaskStateShard LASER_DEPTH=new WriteMaskStateShard(true,true){
        private boolean previousDepthWrite;
        @Override public void setupRenderState(){
            previousDepthWrite=GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            // COLOR_DEPTH_WRITE is a no-op when the forward pass starts with writes disabled.
            RenderSystem.depthMask(true);GL11.glDepthMask(true);
        }
        @Override public void clearRenderState(){RenderSystem.depthMask(previousDepthWrite);GL11.glDepthMask(previousDepthWrite);}
    };
    static final RenderType LASER_BEAM=GunForwardEffects.additive(create("hbm_energy_laser_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(LASER_DEPTH).createCompositeState(false)),ResourceLocation.withDefaultNamespace("textures/misc/white.png"),true,false,true);
    // The silver bullet keeps its existing independent solid volume; its flare stays transparent.
    static final RenderType SOLID_BEAM=GunForwardEffects.additive(create("hbm_folly_solid_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),ResourceLocation.withDefaultNamespace("textures/misc/white.png"),true,false,true);
}
