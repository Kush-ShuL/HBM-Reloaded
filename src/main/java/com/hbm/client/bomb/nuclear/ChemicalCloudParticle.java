package com.hbm.client.bomb.nuclear;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical.ChemicalStyle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
/** Exact single RenderChemical gas/fire billboard bound to its real entity, routed through the existing unique nuclear transparency queue. */
public final class ChemicalCloudParticle extends Particle {
 private static final ParticleRenderType MATERIAL=MukeRenderTypes.cloudTexture(ResourceLocation.parse("hbm:textures/particle/particle_base.png"));private final EntityChemical chemical;
 public ChemicalCloudParticle(ClientLevel world,EntityChemical chemical){super(world,chemical.getX(),chemical.getY(),chemical.getZ());this.chemical=chemical;hasPhysics=false;}
 @Override public void tick(){if(chemical.isRemoved())remove();setPos(chemical.getX(),chemical.getY(),chemical.getZ());}
 @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
 @Override public void render(VertexConsumer ignored,Camera camera,float partial){var style=chemical.getStyle();if(style!=ChemicalStyle.GAS&&style!=ChemicalStyle.GASFLAME)return;if(!chemical.shouldRenderAtSqrDistance(camera.getPosition().distanceToSqr(chemical.position())))return;if(MukeRenderTypes.defer(this,partial))return;var out=MukeRenderTypes.begin(MATERIAL);double fraction=(double)(chemical.tickCount+partial)/chemical.getMaxAge(),size;int color,alpha,u=0,v=0;
  if(style==ChemicalStyle.GAS){size=fraction*10;color=chemical.getFluidType().getColor();alpha=(int)Math.max(127*(1-fraction),0);var random=new Random(chemical.getId());u=random.nextInt(2);v=random.nextInt(2);}else{float exp=(float)(chemical.tickCount+partial)/chemical.getMaxAge();size=exp*2;color=Color.getHSBColor(Math.max((60-exp*100)/360F,0),1-exp*.25F,1-exp*.5F).getRGB();alpha=(int)Math.max(255*(1-exp),0);}
  var eye=camera.getPosition();float x=(float)(Mth.lerp(partial,chemical.xo,chemical.getX())-eye.x),y=(float)(Mth.lerp(partial,chemical.yo,chemical.getY())-eye.y),z=(float)(Mth.lerp(partial,chemical.zo,chemical.getZ())-eye.z);var rotation=new Quaternionf().rotationY((float)Math.toRadians(180-camera.getYRot())).rotateX((float)Math.toRadians(-camera.getXRot()));int light=Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(chemical,partial);
  vertex(out,rotation,x,y,z,-size,-size,1-u,1-v,color,alpha,light);vertex(out,rotation,x,y,z,size,-size,u,1-v,color,alpha,light);vertex(out,rotation,x,y,z,size,size,u,v,color,alpha,light);vertex(out,rotation,x,y,z,-size,size,1-u,v,color,alpha,light);
 }
 private static void vertex(VertexConsumer out,Quaternionf rotation,float x,float y,float z,double dx,double dy,float u,float v,int rgb,int alpha,int light){var point=new Vector3f((float)dx,(float)dy,0).rotate(rotation).add(x,y,z);out.addVertex(point.x,point.y,point.z).setColor((rgb>>16)&255,(rgb>>8)&255,rgb&255,alpha).setUv(u,v).setLight(light);}
 @Override public ParticleRenderType getRenderType(){return MukeRenderTypes.ORDERED;}
}
