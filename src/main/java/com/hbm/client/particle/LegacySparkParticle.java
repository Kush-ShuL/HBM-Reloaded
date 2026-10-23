package com.hbm.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;

/** Original Spark velocity history, gravity, bounce and three-pixel untextured trail. */
public final class LegacySparkParticle extends Particle {
    private static final ParticleRenderType TYPE=new ParticleRenderType(){
        @Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.disableBlend();RenderSystem.disableCull();RenderSystem.depthMask(true);RenderSystem.lineWidth(3);return tess.begin(VertexFormat.Mode.LINES,DefaultVertexFormat.POSITION_COLOR_NORMAL);}
        @Override public String toString(){return "HBM legacy spark";}
    };
    private final List<Vec3> steps=new ArrayList<>();
    private int threshold;
    public LegacySparkParticle(ClientLevel level,double x,double y,double z,double mx,double my,double mz){
        super(level,x,y,z);setBoundingBox(getBoundingBox().move(0,-.1,0));double vx=mx+(float)(Math.random()*2-1)*.4F,vy=my+(float)(Math.random()*2-1)*.4F,vz=mz+(float)(Math.random()*2-1)*.4F;float speed=(float)(Math.random()+Math.random()+1)*.15F,length=(float)Math.sqrt(vx*vx+vy*vy+vz*vz);xd=vx/length*speed*.4000000059604645D;yd=vy/length*speed*.4000000059604645D+.10000000149011612D;zd=vz/length*speed*.4000000059604645D;threshold=4+random.nextInt(3);steps.add(new Vec3(xd,yd,zd));lifetime=20+random.nextInt(10);gravity=.5F;
    }
    public LegacySparkParticle makeSmall(boolean small){if(!small)return this;var box=getBoundingBox();bbWidth=bbHeight=.01F;setBoundingBox(new AABB(box.minX,box.minY,box.minZ,box.minX+.01F,box.minY+.01F,box.minZ+.01F));threshold=3;lifetime=2+random.nextInt(3);yd=-Math.abs(yd);return this;}
    @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();steps.add(new Vec3(xd,yd,zd));while(steps.size()>threshold)steps.removeFirst();yd-=.04D*gravity;double lastY=yd;move(xd,yd,zd);if(onGround){onGround=false;yd=-lastY*.8D;}}
    @Override public void move(double dx,double dy,double dz){var motion=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=dy!=motion.y&&dy<0;if(dx!=motion.x)xd=0;if(dy!=motion.y)yd=0;if(dz!=motion.z)zd=0;}
    @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=.1D;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){
        if(steps.size()<3)return;var eye=camera.getPosition();var previous=new Vec3(Mth.lerp(partial,xo,x)-eye.x,Mth.lerp(partial,yo,y)-eye.y,Mth.lerp(partial,zo,z)-eye.z);
        // Source LINE_STRIP deliberately omits the final computed point.
        for(int i=1;i<steps.size()-1;i++){var current=previous.add(steps.get(i));var direction=current.subtract(previous).normalize();out.addVertex((float)previous.x,(float)previous.y,(float)previous.z).setColor(255,255,255,255).setNormal((float)direction.x,(float)direction.y,(float)direction.z);out.addVertex((float)current.x,(float)current.y,(float)current.z).setColor(255,255,255,255).setNormal((float)direction.x,(float)direction.y,(float)direction.z);previous=current;}
    }
    @Override public ParticleRenderType getRenderType(){return TYPE;}
}
