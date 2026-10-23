package com.hbm.client.render;

import com.mojang.blaze3d.vertex.*;
import java.util.function.Consumer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Three real independent generic-particle passes, retaining their native source shader/format/state.
 * Geometry is produced once; the finite173 mesh bridge owns its copy in the sole168 world queue.
 * Off-pack, GUI, shadow and source replay retain the existing ParticleEngine path.
 */
public final class WorldEffectParticles extends RenderType {
    private WorldEffectParticles(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.TRIANGLES,256,false,false,()->{},()->{});}
    public static final RenderType RBMK_STEAM=textured("steam","hbm:textures/particle/rbmk_jet_steam.png");
    public static final RenderType RBMK_FIRE=textured("fire","hbm:textures/particle/rbmk_fire.png");
    private static final RenderType AMAT_CULL=flash(true),AMAT_NO_CULL=flash(false);
    private static RenderType textured(String name,String texture){
        return WorldEffectMeshes.register(create("hbm_source_rbmk_"+name,DefaultVertexFormat.PARTICLE,VertexFormat.Mode.QUADS,256,false,false,
            CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionTexColorShader))
            .setTextureState(new TextureStateShard(ResourceLocation.parse(texture),false,false))
            .setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"IndependentParticle/RBMK_"+name);
    }
    private static RenderType flash(boolean cull){
        return WorldEffectMeshes.register(create("hbm_source_amat_flash_"+cull,DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.TRIANGLES,32768,false,false,
            CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY)
            .setCullState(cull?CULL:NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"IndependentParticle/AmatFlash");
    }
    /** FLASH.begin does not change culling: retain the actual incoming ParticleEngine state. */
    public static RenderType amatFlash(){return GL11.glIsEnabled(GL11.GL_CULL_FACE)?AMAT_CULL:AMAT_NO_CULL;}
    public static boolean draw(RenderType type,Consumer<VertexConsumer> geometry){
        if(!WorldEffectsForward.acceptsSourceDraw())return false;
        try(var memory=new ByteBufferBuilder(type.bufferSize())){
            var vertices=new BufferBuilder(memory,type.mode(),type.format());
            geometry.accept(vertices);
            var mesh=vertices.build();
            if(mesh!=null)type.draw(mesh); // Native draw closes the submission;173 owns its retained copy.
        }
        return true;
    }
}
