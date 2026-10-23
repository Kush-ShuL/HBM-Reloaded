package com.hbm.client.weapon.death;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Original rotating airborne ashes and flattened floor flakes. */
final class AshesParticle extends DeathParticle {
    private float rotation,oldRotation;
    AshesParticle(ClientLevel level,double x,double y,double z,float scale){super(level,x,y,z);lifetime=1200+random.nextInt(20);particleScale=scale*.9F+random.nextFloat()*.2F;gravity=.01F;rCol=gCol=bCol=random.nextFloat()*.1F+.1F;}
    @Override public void tick(){
        xo=x;yo=y;zo=z;age++;if(age>=lifetime)remove();yd-=gravity;oldRotation=rotation;
        if(!onGround)rotation+=2*((legacyId%2)-.5);xd*=.95D;yd*=.99D;zd*=.95D;boolean grounded=onGround;move(xd,yd,zd);
        if(!grounded&&onGround)rotation=random.nextFloat()*360;if(legacyId%5==0&&onGround&&random.nextInt(15)==0)level.addParticle(ParticleTypes.SMOKE,x,y+.125,z,0,.05,0);
    }
    @Override public void render(VertexConsumer vertices,Camera camera,float partial){
        float left=lifetime-(age+partial);alpha=left<40?left/40:1;var cam=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-cam.x),py=(float)(Mth.lerp(partial,yo,y)-cam.y),pz=(float)(Mth.lerp(partial,zo,z)-cam.z);int light=getLightColor(partial);
        if(onGround){
            Vec3 point=new Vec3(particleScale,0,particleScale).yRot(rotation*(float)Math.PI/180);
            float[][] uv={{1,1},{1,0},{0,0},{0,1}};
            for(int i=0;i<4;i++){vertices.addVertex(px+(float)point.x,py-.09F,pz+(float)point.z).setColor(rCol,gCol,bCol,alpha).setUv(uv[i][0],uv[i][1]).setLight(light);point=point.yRot((float)Math.PI/2);}
        }else{
            float angle=Mth.lerp(partial,oldRotation,rotation)*(float)Math.PI/180;float[][] corners={{-1,-1,1,1},{-1,1,1,0},{1,1,0,0},{1,-1,0,1}};
            for(float[] corner:corners){var point=new Vector3f(corner[0]*particleScale,corner[1]*particleScale,0).rotateZ(-angle).rotate(camera.rotation());vertices.addVertex(px+point.x,py+point.y,pz+point.z).setColor(rCol,gCol,bCol,alpha).setUv(corner[2],corner[3]).setLight(light);}
        }
    }
    @Override public ParticleRenderType getRenderType(){return DeathParticleTypes.ASH;}
}
