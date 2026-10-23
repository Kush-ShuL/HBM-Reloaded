package com.hbm.client.weapon.legendary;

import com.hbm.client.model.*;
import com.hbm.client.bomb.nuclear.MukeRenderTypes;
import com.hbm.client.weapon.pepperbox.GunRenderTypes;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.legendary.CasingEffects;
import com.hbm.weapon.sedna.gun.particle.SpentCasing;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/** Complete ParticleSpentCasing motion, axis-ordered collision/bounce, colored HFR mesh and anchored smoke. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SpentCasingParticle extends Particle {
    private static final java.util.Random SOURCE_RANDOM=new java.util.Random();
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/effect/casings.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/particle/casings.png");
    private static final float SCALE=.05F,JITTER=.001F;
    private static final List<SpentCasingParticle> SMOKE=new ArrayList<>();
    private static final ByteBufferBuilder MEMORY=new ByteBufferBuilder(8192);
    private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(MEMORY);
    private static final ParticleRenderType TYPE=new ParticleRenderType(){@Override public BufferBuilder begin(Tesselator tess,TextureManager textures){RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutShader);RenderSystem.setShaderTexture(0,TEXTURE);RenderSystem.setShaderColor(1,1,1,1);RenderSystem.disableBlend();RenderSystem.enableCull();RenderSystem.depthMask(true);return tess.begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.NEW_ENTITY);}};
    private final SpentCasing config;private final boolean smoking;private final int smokeLife,nodeLife;private final double smokeLift;
    private final List<Node> nodes=new ArrayList<>();private float momentumPitch,momentumYaw,yaw,oldYaw,pitch,oldPitch;
    private boolean previousRender;private double previousRenderX,previousRenderY,previousRenderZ;
    private static final class Node{double x,y,z,alpha;Node(double alpha){this.alpha=alpha;}}
    private SpentCasingParticle(ClientLevel level,CasingEffects.Packet packet,SpentCasing config){
        super(level,packet.x(),packet.y(),packet.z());this.config=config;var data=packet.data();momentumPitch=data.getFloat("mPitch");momentumYaw=data.getFloat("mYaw");
        lifetime=config.getMaxAge();setSize(2*SCALE*Math.max(config.getScaleX(),config.getScaleZ()),SCALE*config.getScaleY());
        // EntityFX.yOffset=height/2: unlike modern particles the source box is centered vertically.
        setBoundingBox(getBoundingBox().move(0,-bbHeight/2,0));
        smoking=data.getBoolean("smoking");smokeLife=data.getInt("smokeLife");smokeLift=data.getDouble("smokeLift");nodeLife=data.getInt("nodeLife");
        xd=data.getDouble("mX");yd=data.getDouble("mY");zd=data.getDouble("mZ");oldYaw=yaw=data.getFloat("yaw");oldPitch=pitch=data.getFloat("pitch");
    }
    public static void receive(CasingEffects.Packet packet){var client=Minecraft.getInstance();if(client.level==null||packet.data()==null)return;var config=SpentCasing.fromName(packet.data().getString("name"));if(config!=null)client.particleEngine.add(new SpentCasingParticle(client.level,packet,config));}
    @Override public void tick(){
        xo=x;yo=y;zo=z;if(age++>=lifetime)remove();yd-=.04;move(xd,yd,zd);xd*=.98;yd*=.98;zd*=.98;
        if(onGround){xd*=.7;zd*=.7;pitch=(float)Math.floor(pitch/180F+.5F)*180;momentumYaw*=.7F;
            // Keep real bounces/sliding; a casing at rest must not integrate residual alternating spin forever.
            if(Math.abs(yd)<=.04D&&xd*xd+zd*zd<=1E-6D){momentumPitch=0;momentumYaw=0;}
            onGround=false;}
        if(age>smokeLife&&!nodes.isEmpty())nodes.clear();
        // Source never calls Entity.onUpdate/handleWaterMovement; inherited inWater stays false here.
        if(smoking&&age<=smokeLife){for(var node:nodes){node.x+=SOURCE_RANDOM.nextGaussian()*JITTER;node.z+=SOURCE_RANDOM.nextGaussian()*JITTER;node.y+=smokeLift*SCALE;node.alpha=Math.max(0,node.alpha-1D/nodeLife);}if(age<smokeLife)nodes.add(new Node(nodes.isEmpty()?0:1));}
        oldPitch=pitch;oldYaw=yaw;pitch+=momentumPitch;yaw+=momentumYaw;
        if(Math.abs(oldPitch-pitch)>180){if(oldPitch<pitch)oldPitch+=360;if(oldPitch>pitch)oldPitch-=360;}
        if(Math.abs(oldYaw-yaw)>180){if(oldYaw<yaw)oldYaw+=360;if(oldYaw>yaw)oldYaw-=360;}
    }
    @Override public void move(double dx,double dy,double dz){
        // The old EntityFX collision loop clips Y, then X, then Z, irrespective of movement magnitude.
        double originalX=dx,originalY=dy,originalZ=dz;var box=getBoundingBox();var shapes=new ArrayList<VoxelShape>();level.getBlockCollisions(null,box.expandTowards(dx,dy,dz)).forEach(shapes::add);shapes.addAll(level.getEntityCollisions(null,box.expandTowards(dx,dy,dz)));
        dy=Shapes.collide(Direction.Axis.Y,box,shapes,dy);box=box.move(0,dy,0);dx=Shapes.collide(Direction.Axis.X,box,shapes,dx);box=box.move(dx,0,0);dz=Shapes.collide(Direction.Axis.Z,box,shapes,dz);box=box.move(0,0,dz);setBoundingBox(box);x=(box.minX+box.maxX)/2;y=box.minY+bbHeight/2;z=(box.minZ+box.maxZ)/2;
        boolean vertical=originalY!=dy;onGround=vertical&&originalY<0;
        if(originalX!=dx){xd*= -.25;if(Math.abs(momentumYaw)>1E-7)momentumYaw*= -.75F;else momentumYaw=(float)SOURCE_RANDOM.nextGaussian()*10*config.getBounceYaw();}
        if(vertical){yd*= -.5;boolean speed=Math.abs(yd)>.04;if(speed||Math.abs(momentumPitch)>1E-7){momentumPitch*= -.75F;if(speed){float multiplier=(float)Mth.clamp(originalY/.2F,-1,1);momentumPitch+=SOURCE_RANDOM.nextGaussian()*10*config.getBouncePitch()*multiplier;momentumYaw+=(float)SOURCE_RANDOM.nextGaussian()*10*config.getBounceYaw()*multiplier;}}}
        if(originalZ!=dz){zd*= -.25;if(Math.abs(momentumYaw)>1E-7)momentumYaw*= -.75F;else momentumYaw=(float)SOURCE_RANDOM.nextGaussian()*10*config.getBounceYaw();}
        if(config.getSound()!=null&&vertical&&Math.abs(originalY)>=.2)level.playLocalSound(x,y,z,HbmSounds.get(config.getSound()),SoundSource.NEUTRAL,SpentCasing.PLINK_LARGE.equals(config.getSound())?1:.5F,1+SOURCE_RANDOM.nextFloat()*.2F,false);
    }
    @Override public void render(VertexConsumer vertices,Camera camera,float partial){
        double px=Mth.lerp(partial,xo,x),py=Mth.lerp(partial,yo,y),pz=Mth.lerp(partial,zo,z);var cam=camera.getPosition();var poses=new PoseStack();poses.translate(px-cam.x,py-cam.y-bbHeight/4+config.getScaleY()*.01,pz-cam.z);poses.scale(SCALE,SCALE,SCALE);poses.mulPose(Axis.YP.rotationDegrees(180-Mth.lerp(partial,oldYaw,yaw)));poses.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partial,oldPitch,pitch)));poses.scale(config.getScaleX(),config.getScaleY(),config.getScaleZ());
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);int light=LevelRenderer.getLightColor(level,BlockPos.containing(px,py,pz)),index=0;for(String name:config.getType().partNames)LegacyObjRenderer.renderOnly(mesh,poses,vertices,light,OverlayTexture.NO_OVERLAY,0xff000000|config.getColors()[index++],LegacyObjRenderer.Convention.HFR,name);
        if(!previousRender){previousRenderX=px;previousRenderY=py;previousRenderZ=pz;previousRender=true;}
        if(!nodes.isEmpty()){for(var node:nodes){node.x+=previousRenderX-px;node.y+=previousRenderY-py;node.z+=previousRenderZ-pz;}SMOKE.add(this);}
        previousRenderX=px;previousRenderY=py;previousRenderZ=pz;
    }
    @SubscribeEvent public static void smoke(RenderLevelStageEvent event){if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_PARTICLES||SMOKE.isEmpty())return;try{float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);var particles=List.copyOf(SMOKE);var camera=event.getCamera();MukeRenderTypes.deferDraw(()->{for(var particle:particles)particle.drawSmoke(camera,partial);BUFFERS.endBatch();});}finally{SMOKE.clear();}}
    private void drawSmoke(Camera camera,float partial){
        var player=Minecraft.getInstance().player;if(player==null)return;double px=Mth.lerp(partial,xo,x),py=Mth.lerp(partial,yo,y),pz=Mth.lerp(partial,zo,z);var cam=camera.getPosition();var poses=new PoseStack();poses.translate(px-cam.x,py-cam.y-bbHeight/4,pz-cam.z);float scale=config.getScaleX()*.5F*SCALE,angle=(float)Math.toRadians(-Mth.lerp(partial,player.yRotO,player.getYRot()));double vx=scale*Mth.cos(angle),vz=-scale*Mth.sin(angle),fade=1D-(double)age/smokeLife;var out=BUFFERS.getBuffer(LegacyGunSmokeType.CASING);int light=getLightColor(partial);
        for(int i=0;i<nodes.size()-1;i++){var node=nodes.get(i);var past=nodes.get(i+1);for(int side:new int[]{1,-1}){vertex(out,poses,node.x,node.y,node.z,(float)(node.alpha*fade),light);vertex(out,poses,node.x+vx*side,node.y,node.z+vz*side,0,light);vertex(out,poses,past.x+vx*side,past.y,past.z+vz*side,0,light);vertex(out,poses,past.x,past.y,past.z,(float)(past.alpha*fade),light);}}
    }
    private static void vertex(VertexConsumer out,PoseStack poses,double x,double y,double z,float alpha,int light){out.addVertex(poses.last(),(float)x,(float)y,(float)z).setColor(1,1,1,alpha).setUv(0,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(poses.last(),0,1,0);}
    @Override public ParticleRenderType getRenderType(){return TYPE;}
    @Override public AABB getRenderBoundingBox(float partial){double radius=Math.max(Math.max(config.getScaleX(),config.getScaleY()),config.getScaleZ())*.5+1;return new AABB(x-radius,y-radius,z-radius,x+radius,y+radius,z+radius);}
}
