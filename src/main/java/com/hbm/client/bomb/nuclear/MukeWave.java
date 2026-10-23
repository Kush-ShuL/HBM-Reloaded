package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Source ParticleMukeWave: horizontal additive, unfogged wave at y-.25. */
public final class MukeWave extends Particle {
    private float waveScale=45;
    public MukeWave(ClientLevel level,double x,double y,double z){super(level,x,y,z);lifetime=25;}
    public void setup(float scale,int maxAge){waveScale=scale;lifetime=maxAge;}
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;var batch=MukeRenderTypes.begin(MukeRenderTypes.WAVE);out=batch;float alpha=1-(age+partial)/lifetime,scale=(1-(float)Math.pow(Math.E,(age+partial)*-.125))*waveScale;var eye=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y)-.25F,pz=(float)(Mth.lerp(partial,zo,z)-eye.z);vertex(out,px-scale,py,pz-scale,1,1,alpha);vertex(out,px-scale,py,pz+scale,1,0,alpha);vertex(out,px+scale,py,pz+scale,0,0,alpha);vertex(out,px+scale,py,pz-scale,0,1,alpha);}
    private void vertex(VertexConsumer out,float x,float y,float z,float u,float v,float alpha){out.addVertex(x,y,z).setUv(u,v).setColor(1F,1F,1F,alpha);}
    @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
