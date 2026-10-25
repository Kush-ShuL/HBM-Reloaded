package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.client.render.WorldEffectsDepth;
import com.hbm.client.render.mixin.SmokeParticleEngineAccess;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class MukeRenderTypes {
    private static final Tesselator LEGACY_TESSELLATOR=new Tesselator(4096);
    private static final ArrayList<java.util.function.BiConsumer<net.minecraft.client.Camera,Float>> PENDING=new ArrayList<>();
    private static ParticleRenderType material;
    private static BufferBuilder vertices;
    private static boolean drawing,depthOnly;
    private static float partial,fogStart,fogEnd;
    private static float[] fogColor;
    private static com.mojang.blaze3d.shaders.FogShape fogShape;
    private static void captureFog(){if(!PENDING.isEmpty())return;fogStart=RenderSystem.getShaderFogStart();fogEnd=RenderSystem.getShaderFogEnd();fogColor=RenderSystem.getShaderFogColor().clone();fogShape=RenderSystem.getShaderFogShape();}
    private MukeRenderTypes(){}
    // Preserve the source layer-3 queue. Draw after water/cloud depths exist; the
    // Fabulous compositor instead consumes the dedicated particle target depth.
    public static final ParticleRenderType ORDERED=new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager manager){return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}
    };
    public static boolean defer(Particle particle,float partialTick){if(drawing)return false;captureFog();PENDING.add((camera,tick)->particle.render(null,camera,partialTick));NuclearTransparencyLayers.request();partial=partialTick;return true;}
    /** World smoke ribbons share the same cloud split and nearest-depth pass as billboards. */
    public static void deferDraw(Runnable draw){captureFog();PENDING.add((camera,tick)->{flush();try{draw.run();}finally{Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();}});NuclearTransparencyLayers.request();}
    public static boolean writesDepth(){return depthOnly;}
    private static java.util.Queue<Particle> admittedSmoke(){return ((SmokeParticleEngineAccess)Minecraft.getInstance().particleEngine).hbm$smokeParticles().get(ORDERED);}
    public static boolean hasDepthSmoke(){var particles=admittedSmoke();if(particles==null)return false;for(var particle:particles)if(particle instanceof SourceSmokeGeometry)return true;return false;}
    /** Only four actual source smoke emitters participate; additive flashes/waves are never depth proxies. */
    public static void renderEarlyDepth(net.minecraft.client.Camera camera,net.minecraft.client.renderer.culling.Frustum frustum,float partial){
        var particles=admittedSmoke();if(particles==null)return;
        boolean previous=depthOnly;depthOnly=true;Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        try{for(var particle:particles)if(particle instanceof SourceSmokeGeometry smoke&&(frustum==null||frustum.isVisible(particle.getRenderBoundingBox(partial))))smoke.emitSmokeGeometry(begin(smoke.smokeMaterial()),camera,partial);flush();}
        finally{if(vertices!=null){var mesh=vertices.build();if(mesh!=null)mesh.close();vertices=null;}material=null;depthOnly=previous;Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();}
    }

    public static BufferBuilder begin(ParticleRenderType next){
        if(next!=material){flush();material=next;vertices=next.begin(LEGACY_TESSELLATOR,Minecraft.getInstance().getTextureManager());}
        return vertices;
    }
    private static void flush(){if(vertices!=null){var mesh=vertices.build();if(mesh!=null)BufferUploader.drawWithShader(mesh);vertices=null;}material=null;}
    static void renderDeferred(RenderLevelStageEvent event){
        // Reset before this frame's entity and particle submissions. ORDERED.begin
        // runs later and must not erase entity renderers' already queued effects.
        // This also drops abandoned work if a prior frame aborted before drawing.
        if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY){PENDING.clear();return;}
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||PENDING.isEmpty())return;
        if(WorldEffectsForward.defer(()->{try{renderPass(event,false);}finally{PENDING.clear();}}))return;
        if(NuclearTransparencyLayers.active()){
            NuclearTransparencyLayers.bindNuclearLayer();
            try{renderPass(event,false);}
            catch(RuntimeException|Error failure){PENDING.clear();throw failure;}
            finally{NuclearTransparencyLayers.restoreAfterNuclear();}
            // Native translucent clouds divide individual fragments, not an entire mushroom.
            // Keep the original mixed particle order in both colour layers.
            NuclearTransparencyLayers.deferBehind(()->{
                NuclearTransparencyLayers.bindNuclearLayer();
                try{renderPass(event,false);}finally{PENDING.clear();NuclearTransparencyLayers.restoreAfterNuclear();}
            });
        }else{
            boolean fabulous=Minecraft.useShaderTransparency();
            if(fabulous)RenderStateShard.PARTICLES_TARGET.setupRenderState();
            try{renderPass(event,fabulous);}finally{PENDING.clear();if(fabulous){RenderStateShard.PARTICLES_TARGET.clearRenderState();RenderStateShard.WEATHER_TARGET.setupRenderState();}}
        }
    }
    private static void renderPass(RenderLevelStageEvent event,boolean fabulous){
        var mc=Minecraft.getInstance();float oldStart=RenderSystem.getShaderFogStart(),oldEnd=RenderSystem.getShaderFogEnd();float[] oldColor=RenderSystem.getShaderFogColor().clone();var oldShape=RenderSystem.getShaderFogShape();RenderSystem.setShaderFogStart(fogStart);RenderSystem.setShaderFogEnd(fogEnd);RenderSystem.setShaderFogColor(fogColor[0],fogColor[1],fogColor[2],fogColor[3]);RenderSystem.setShaderFogShape(fogShape);mc.gameRenderer.lightTexture().turnOnLightLayer();drawing=true;
        try(var cutoff=WorldEffectsDepth.colour()){
            for(var particle:PENDING)particle.accept(event.getCamera(),partial);flush();
            // Fabulous consumes the particle target's nearest depth independently.
            if(fabulous){depthOnly=true;RenderSystem.colorMask(false,false,false,false);for(var particle:PENDING)particle.accept(event.getCamera(),partial);flush();}
        }finally{
            if(vertices!=null){var mesh=vertices.build();if(mesh!=null)mesh.close();vertices=null;}material=null;
            drawing=false;depthOnly=false;RenderSystem.colorMask(true,true,true,true);mc.gameRenderer.lightTexture().turnOffLightLayer();
            RenderSystem.enableCull();RenderSystem.depthMask(false);RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();
            RenderSystem.setShaderFogStart(oldStart);RenderSystem.setShaderFogEnd(oldEnd);RenderSystem.setShaderFogColor(oldColor[0],oldColor[1],oldColor[2],oldColor[3]);RenderSystem.setShaderFogShape(oldShape);
        }
    }
    static final ParticleRenderType CLOUD=cloud("explosion"),BALEFIRE_CLOUD=cloud("explosion_bf"),ROCKET_FLAME=cloud("rocket_flame"),FLASH=additive("flare"),WAVE=additive("shockwave");
    static final ParticleRenderType CONTRAIL=cloud("contrail"),VANILLA_SMOKE=cloudTexture(ResourceLocation.parse("minecraft:textures/atlas/particles.png"));
    private static void depth(){RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(depthOnly);org.lwjgl.opengl.GL11.glDepthMask(depthOnly);}
    private static ParticleRenderType cloud(String texture){return cloudTexture(ResourceLocation.parse("hbm:textures/particle/"+texture+".png"));}
    public static ParticleRenderType cloudTexture(ResourceLocation texture){return cloudTexture(texture,true);}
    public static ParticleRenderType cloudTexture(ResourceLocation texture,boolean cull){return new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(()->WorldEffectsDepth.shader(MukeClient.cloudShader()));RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);else RenderSystem.defaultBlendFunc();depth();if(cull)RenderSystem.enableCull();else RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};}
    static ParticleRenderType additive(String texture){return new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(()->WorldEffectsDepth.shader(MukeClient.additiveShader()));RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/particle/"+texture+".png"));RenderSystem.setShaderColor(1,1,1,1);RenderSystem.enableBlend();if(NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE,GlStateManager.SourceFactor.ZERO,GlStateManager.DestFactor.ONE);else RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);depth();RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}};}
}
