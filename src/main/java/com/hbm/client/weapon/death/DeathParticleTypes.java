package com.hbm.client.weapon.death;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/** Source layer3 mesh/billboard state, retaining each original standalone texture. */
final class DeathParticleTypes {
    private static final Map<ResourceLocation,ParticleRenderType> BONES=new HashMap<>(),GIBS=new HashMap<>();
    static final ParticleRenderType ASH=type(ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"),false,true);
    private DeathParticleTypes(){}
    static ParticleRenderType bones(ResourceLocation texture){return BONES.computeIfAbsent(texture,value->type(value,true,true));}
    static ParticleRenderType gibs(int kind){return GIBS.computeIfAbsent(ResourceLocation.parse("hbm:textures/particle/"+(kind==2?"metal":kind==1?"slime":"meat")+".png"),value->type(value,false,false));}
    private static ParticleRenderType type(ResourceLocation texture,boolean mesh,boolean blend){return new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){
            RenderSystem.setShader(mesh?GameRenderer::getRendertypeEntityTranslucentShader:GameRenderer::getParticleShader);RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.depthMask(true);
            if(mesh)RenderSystem.enableCull();else RenderSystem.disableCull();if(blend){RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();}else RenderSystem.disableBlend();
            return tess.begin(VertexFormat.Mode.QUADS,mesh?DefaultVertexFormat.NEW_ENTITY:DefaultVertexFormat.PARTICLE);
        }
        @Override public String toString(){return "HBM death particle "+texture;}
    };}
}
