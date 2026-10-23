package com.hbm.client.weapon.death;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

/** Original meat/slime/metal billboards and blood-block dust trail. */
final class GibletParticle extends DeathParticle {
    private final int kind;
    private final float momentumYaw,momentumPitch;
    private float yaw,pitch;
    GibletParticle(ClientLevel level,double x,double y,double z,double mx,double my,double mz,int kind){super(level,x,y,z);xd=mx;yd=my;zd=mz;this.kind=kind;lifetime=140+random.nextInt(20);gravity=kind==2?4:2;momentumYaw=(float)random.nextGaussian()*15;momentumPitch=(float)random.nextGaussian()*15;}
    @Override public void tick(){
        xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04D*gravity;move(xd,yd,zd);xd*=.9800000190734863D;yd*=.9800000190734863D;zd*=.9800000190734863D;if(onGround){xd*=.699999988079071D;zd*=.699999988079071D;}
        if(!onGround){pitch+=momentumPitch;yaw+=momentumYaw;if(kind==2)return;var particle=Minecraft.getInstance().particleEngine.createParticle(new BlockParticleOption(ParticleTypes.BLOCK,(kind==1?Blocks.MELON:Blocks.REDSTONE_BLOCK).defaultBlockState()),x,y,z,0,0,0);if(particle!=null)particle.setLifetime(20+random.nextInt(20));}
    }
    @Override public void render(VertexConsumer vertices,Camera camera,float partial){
        var cam=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-cam.x),py=(float)(Mth.lerp(partial,yo,y)-cam.y),pz=(float)(Mth.lerp(partial,zo,z)-cam.z),size=particleScale*.1F;int light=getLightColor(partial);
        // Source updates momentum fields but its billboard never applies them.
        float[][] corners={{-1,-1,0,0},{-1,1,0,1},{1,1,1,1},{1,-1,1,0}};
        for(float[] corner:corners){var point=new Vector3f(corner[0]*size,corner[1]*size,0).rotate(camera.rotation());vertices.addVertex(px+point.x,py+point.y,pz+point.z).setUv(corner[2],corner[3]).setColor(rCol,gCol,bCol,alpha).setLight(light);}
    }
    @Override public ParticleRenderType getRenderType(){return DeathParticleTypes.gibs(kind);}
}
