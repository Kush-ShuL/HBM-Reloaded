package com.hbm.client.bomb.nuclear;
import com.hbm.bomb.nuclear.NuclearPlasmaBlastEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
/** Original static, horizontal ParticlePlasmaBlast and inherited post-increment EntityFX lifetime. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearPlasmaBlastParticle extends Particle {
 private static final ArrayList<NuclearPlasmaBlastParticle> FRAME=new ArrayList<>();
 private static final ParticleRenderType QUEUE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);}};
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/particle/shockwave.png");
 private static final RenderType MAIN=com.hbm.client.render.WorldEffectMeshes.register(TorexRenderTypes.cloud(TEXTURE,true,false),"PlasmaBlast/IndependentQueue"),TRANSLUCENT=TorexRenderTypes.cloud(TEXTURE,true,true),DEPTH=TorexRenderTypes.depth(TEXTURE);
 private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(4096));
 private final float size,pitch,yaw;
 private NuclearPlasmaBlastParticle(ClientLevel level,NuclearPlasmaBlastEffects packet){super(level,packet.x(),packet.y(),packet.z());setSize(.2F,.2F);lifetime=20;rCol=packet.r();gCol=packet.g();bCol=packet.b();size=packet.scale();pitch=packet.pitch();yaw=packet.yaw();xd=yd=zd=0;}
 public static void receive(NuclearPlasmaBlastEffects packet){var mc=Minecraft.getInstance();if(mc.level!=null)mc.particleEngine.add(new NuclearPlasmaBlastParticle(mc.level,packet));}
 @Override public void tick(){xo=x;yo=y;zo=z;if(age++>=lifetime)remove();move(xd,yd,zd);xd*=(double).98F;yd*=(double).98F;zd*=(double).98F;if(onGround){xd*=(double).7F;zd*=(double).7F;}}
 @Override public void render(VertexConsumer out,Camera camera,float partial){FRAME.add(this);NuclearTransparencyLayers.request();}
 @Override public ParticleRenderType getRenderType(){return QUEUE;}
 @Override public AABB getRenderBoundingBox(float partial){return AABB.INFINITE;}
 @SubscribeEvent public static void draw(RenderLevelStageEvent event){
  if(event.getStage()==RenderLevelStageEvent.Stage.AFTER_SKY){FRAME.clear();return;}if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER||FRAME.isEmpty())return;
  boolean fabulous=Minecraft.useShaderTransparency()&&!com.hbm.client.render.WorldEffectsForward.pack(),deferred=false;
  try{
   drawPass(event,fabulous?TRANSLUCENT:MAIN);
   if(fabulous)drawPass(event,DEPTH);
   else if(NuclearTransparencyLayers.active()){NuclearTransparencyLayers.deferBehind(()->{try{drawPass(event,MAIN);}finally{FRAME.clear();}});deferred=true;}
  }finally{if(!deferred)FRAME.clear();if(fabulous&&event.getLevelRenderer().getWeatherTarget()!=null)event.getLevelRenderer().getWeatherTarget().bindWrite(false);RenderSystem.depthMask(fabulous);}
 }
 private static void drawPass(RenderLevelStageEvent event,RenderType type){
  var pose=event.getPoseStack();var eye=event.getCamera().getPosition();float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);var out=BUFFERS.getBuffer(type);
  for(var particle:FRAME){
   float alpha=1-(particle.age+partial)/particle.lifetime,scale=(1-(float)Math.pow(Math.E,(particle.age+partial)*-.125))*particle.size;
   pose.pushPose();pose.translate(Mth.lerp(partial,particle.xo,particle.x)-eye.x,Mth.lerp(partial,particle.yo,particle.y)-eye.y,Mth.lerp(partial,particle.zo,particle.z)-eye.z);pose.mulPose(Axis.YP.rotationDegrees(particle.yaw));pose.mulPose(Axis.XP.rotationDegrees(particle.pitch));var matrix=pose.last().pose();
   out.addVertex(matrix,-scale,0,-scale).setUv(1,1).setColor(particle.rCol,particle.gCol,particle.bCol,alpha);out.addVertex(matrix,-scale,0,scale).setUv(1,0).setColor(particle.rCol,particle.gCol,particle.bCol,alpha);out.addVertex(matrix,scale,0,scale).setUv(0,0).setColor(particle.rCol,particle.gCol,particle.bCol,alpha);out.addVertex(matrix,scale,0,-scale).setUv(0,1).setColor(particle.rCol,particle.gCol,particle.bCol,alpha);pose.popPose();
  }BUFFERS.endBatch(type);
 }
}
