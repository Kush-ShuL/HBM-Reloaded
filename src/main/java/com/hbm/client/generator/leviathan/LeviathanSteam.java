package com.hbm.client.generator.leviathan;

import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.*;
/** Official1.7.10 EntityCloudFX(bks) constructor/tick/7..0atlas/earlysizegrowth/playerentrainment, notcustomcoolingtowersmoke. */
final class LeviathanSteam extends SingleQuadParticle {
 private final float sourceSize;
 private static final ResourceLocation SHEET=ResourceLocation.parse("hbm:textures/particle/vanilla_1_7_10.png");
 private static final ParticleRenderType TYPE=MukeRenderTypes.cloudTexture(SHEET,true);
 LeviathanSteam(ClientLevel world,double x,double y,double z,double vx,double vy,double vz){super(world,x,y,z);random.nextFloat();random.nextFloat();quadSize=(random.nextFloat()*.5F+.5F)*2F*.1F;random.nextFloat();xd=(double)((float)(Math.random()*2D-1D)*.4F);yd=(double)((float)(Math.random()*2D-1D)*.4F);zd=(double)((float)(Math.random()*2D-1D)*.4F);float speed=(float)(Math.random()+Math.random()+1D)*.15F,norm=(float)Math.sqrt(xd*xd+yd*yd+zd*zd);xd=xd/(double)norm*(double)speed*(double).4F;yd=yd/(double)norm*(double)speed*(double).4F+(double).1F;zd=zd/(double)norm*(double)speed*(double).4F;xd=xd*(double).1F+vx;yd=yd*(double).1F+vy;zd=zd*(double).1F+vz;rCol=gCol=bCol=1F-(float)(Math.random()*(double).3F);quadSize*=.75F;quadSize*=2.5F;sourceSize=quadSize;lifetime=(int)(8D/(Math.random()*.8D+.3D));lifetime=(int)(lifetime*2.5F);centerAt(x,y,z);}
 private void centerAt(double x,double y,double z){setPos(x,y,z);setBoundingBox(new AABB(x-(double).1F,y-(double).1F,z-(double).1F,x+(double).1F,y+(double).1F,z+(double).1F));}
 @Override public void move(double dx,double dy,double dz){var clipped=Entity.collideBoundingBox(null,new Vec3(dx,dy,dz),getBoundingBox(),level,List.of());var box=getBoundingBox().move(clipped);setBoundingBox(box);x=(box.minX+box.maxX)*.5;y=box.minY+(double).1F;z=(box.minZ+box.maxZ)*.5;onGround=dy!=clipped.y&&dy<0;if(dx!=clipped.x)xd=0;if(dy!=clipped.y)yd=0;if(dz!=clipped.z)zd=0;}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();move(xd,yd,zd);xd*=(double).96F;yd*=(double).96F;zd*=(double).96F;net.minecraft.world.entity.player.Player closest=null;double distance=4;for(var player:level.players()){double current=player.distanceToSqr(x,y,z);if(current<distance){distance=current;closest=player;}}if(closest!=null&&y>closest.getBoundingBox().minY){y+=(closest.getBoundingBox().minY-y)*.2;yd+=(closest.getDeltaMovement().y-yd)*.2;centerAt(x,y,z);}if(onGround){xd*=(double).7F;zd*=(double).7F;}}
 @Override public void render(VertexConsumer vertices,Camera camera,float partial){if(MukeRenderTypes.defer(this,partial))return;vertices=MukeRenderTypes.begin(TYPE);quadSize=sourceSize*Math.clamp(((float)age+partial)/lifetime*32F,0,1);super.render(vertices,camera,partial);}
 @Override protected float getU0(){return (7-age*8/lifetime)%16/16F;}@Override protected float getU1(){return getU0()+.0624375F;}@Override protected float getV0(){return (7-age*8/lifetime)/16/16F;}@Override protected float getV1(){return getV0()+.0624375F;}@Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
