package com.hbm.client.bomb.nuclear;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Source twenty-tick flash: 24 fixed flare offsets and the delayed original stem/ground/mushroom cloud. */
public final class MukeFlash extends Particle {
    private final boolean balefire;
    public MukeFlash(ClientLevel level,double x,double y,double z,boolean balefire){super(level,x,y,z);this.balefire=balefire;lifetime=20;}
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();if(age==15){
        for(double d=0;d<=1.8D;d+=.1D)cloud(x,y,z,random.nextGaussian()*.05,d+random.nextGaussian()*.02,random.nextGaussian()*.05);
        for(int i=0;i<100;i++)cloud(x,y+.5,z,random.nextGaussian()*.5,random.nextInt(5)==0?.02:0,random.nextGaussian()*.5);
        for(int i=0;i<75;i++){double vx=random.nextGaussian()*.5,vz=random.nextGaussian()*.5;if(vx*vx+vz*vz>1.5){vx*=.5;vz*=.5;}double vy=1.8+(random.nextDouble()*3-1.5)*(.75-(vx*vx+vz*vz))*.5;cloud(x,y,z,vx,vy+random.nextGaussian()*.02,vz);}
    }}
    private void cloud(double x,double y,double z,double vx,double vy,double vz){Minecraft.getInstance().particleEngine.add(new MukeCloud(level,x,y,z,vx,vy,vz,balefire));}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;var batch=MukeRenderTypes.begin(MukeRenderTypes.FLASH);out=batch;float alpha=(1-(age+partial)/lifetime)*.5F,scale=(age+partial)*3F+1F;var eye=camera.getPosition();float dx=(float)(Mth.lerp(partial,xo,x)-eye.x),dy=(float)(Mth.lerp(partial,yo,y)-eye.y),dz=(float)(Mth.lerp(partial,zo,z)-eye.z);var random=new Random();for(int i=0;i<24;i++){random.setSeed(i*31+1);float px=(float)(dx+random.nextDouble()*15-7.5),py=(float)(dy+random.nextDouble()*7.5-3.75),pz=(float)(dz+random.nextDouble()*15-7.5);MukeClient.billboard(out,camera,px,py,pz,scale,1,.9F,.75F,alpha,0,1,0,1,false,false);}}
    @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
    @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
