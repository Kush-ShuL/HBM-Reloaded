package com.hbm.client.bomb.conventional.grenade;

import com.hbm.bomb.conventional.grenade.FlameEffects;
import com.hbm.client.bomb.nuclear.MukeClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/** Original ParticleFlamethrower and EntityFXRotating's Rodrigues billboard rotation. */
public final class LingeringFlameParticle extends Particle {
    private static final AtomicInteger IDS=new AtomicInteger();
    private static final ParticleRenderType MATERIAL=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager manager){RenderSystem.setShader(MukeClient::cloudShader);RenderSystem.setShaderTexture(0,ResourceLocation.parse("hbm:textures/block/particle/particle_base.png"));RenderSystem.enableBlend();if(com.hbm.client.bomb.nuclear.NuclearTransparencyLayers.active())RenderSystem.blendFuncSeparate(770,771,1,771);else RenderSystem.defaultBlendFunc();RenderSystem.enableDepthTest();RenderSystem.depthFunc(515);RenderSystem.depthMask(true);RenderSystem.disableCull();return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.PARTICLE);}};
    private final int id=IDS.getAndIncrement(),type;private float rotation,previousRotation;
    private LingeringFlameParticle(ClientLevel world,double x,double y,double z,int type){super(world,x,y,z);setBoundingBox(getBoundingBox().move(0,-(double).1F,0));this.type=type;random.nextFloat();random.nextFloat();random.nextFloat();lifetime=20+random.nextInt(10);xd=world.random.nextGaussian()*.02;zd=world.random.nextGaussian()*.02;float hue=15+random.nextFloat()*25;if(type==1)hue=65+random.nextFloat()*35;if(type==2)hue=-random.nextFloat()*15;var color=Color.getHSBColor(hue/255F,1,1);rCol=color.getRed()/255F;gCol=color.getGreen()/255F;bCol=color.getBlue()/255F;if(type==3||type==4)rCol=gCol=bCol=1;}
    public static void receive(FlameEffects data){var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new LingeringFlameParticle(mc.level,data.x(),data.y(),data.z(),data.meta()));}
    @Override public void tick(){xo=x;yo=y;zo=z;if(++age>=lifetime)remove();xd*=.91;yd*=.91;zd*=.91;yd+=.01;previousRotation=rotation;rotation+=30*(id%2-.5);move(xd,yd,zd);}
    @Override public void move(double x,double y,double z){var motion=Entity.collideBoundingBox(null,new Vec3(x,y,z),getBoundingBox(),level,List.of());setBoundingBox(getBoundingBox().move(motion));setLocationFromBoundingbox();onGround=y!=motion.y&&y<0;if(x!=motion.x)xd=0;if(y!=motion.y)yd=0;if(z!=motion.z)zd=0;}
    @Override protected void setLocationFromBoundingbox(){super.setLocationFromBoundingbox();y+=(double).1F;}
    @Override public void render(VertexConsumer out,Camera camera,float partial){if(com.hbm.client.bomb.nuclear.MukeRenderTypes.defer(this,partial))return;out=com.hbm.client.bomb.nuclear.MukeRenderTypes.begin(MATERIAL);double fraction=(double)age/lifetime;float r=rCol,g=gCol,b=bCol;
        if(type==3){alpha=(float)(1-fraction);float add=(float)fraction*1.25F-.25F;r-=add;g-=add*.75F;}
        else if(type==4){alpha=(float)(1-fraction);float add=(float)fraction*2F-.25F;r-=add*.75F;g-=add;b-=add*.5F;}
        else{alpha=(float)Math.pow(1-Math.min(fraction,1),.5)*.5F;float add=.75F-(float)fraction;r+=add;g+=add;b+=add;}
        r=Mth.clamp(r,0,1);g=Mth.clamp(g,0,1);b=Mth.clamp(b,0,1);double scale=(fraction*1.25+.25)*.5;
        float yaw=camera.getYRot()*(float)Math.PI/180F,pitch=camera.getXRot()*(float)Math.PI/180F;double sx=Mth.cos(yaw),sz=Mth.sin(yaw),sy=Mth.cos(pitch),dx=-sz*Mth.sin(pitch),dz=sx*Mth.sin(pitch);double nx=sy*sz,ny=dz*sx-dx*sz,nz=-sy*sx,length=Math.sqrt(nx*nx+ny*ny+nz*nz);nx/=length;ny/=length;nz/=length;
        double rotation=Mth.lerp(partial,previousRotation,this.rotation)*Math.PI/180,cos=Math.cos(rotation),sin=Math.sin(rotation);var eye=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-eye.x),py=(float)(Mth.lerp(partial,yo,y)-eye.y),pz=(float)(Mth.lerp(partial,zo,z)-eye.z);
        vertex(out,px,py,pz,(-sx-dx)*scale,-sy*scale,(-sz-dz)*scale,nx,ny,nz,cos,sin,1,1,r,g,b);
        vertex(out,px,py,pz,(-sx+dx)*scale,sy*scale,(-sz+dz)*scale,nx,ny,nz,cos,sin,1,0,r,g,b);
        vertex(out,px,py,pz,(sx+dx)*scale,sy*scale,(sz+dz)*scale,nx,ny,nz,cos,sin,0,0,r,g,b);
        vertex(out,px,py,pz,(sx-dx)*scale,-sy*scale,(sz-dz)*scale,nx,ny,nz,cos,sin,0,1,r,g,b);
    }
    private void vertex(VertexConsumer out,float px,float py,float pz,double x,double y,double z,double nx,double ny,double nz,double cos,double sin,float u,float v,float r,float g,float b){out.addVertex(px+(float)(x*cos+(ny*z-nz*y)*sin),py+(float)(y*cos+(nz*x-nx*z)*sin),pz+(float)(z*cos+(nx*y-ny*x)*sin)).setColor(r,g,b,alpha).setUv(u,v).setLight(240);}
    @Override public ParticleRenderType getRenderType(){return com.hbm.client.bomb.nuclear.MukeRenderTypes.ORDERED;}
}
