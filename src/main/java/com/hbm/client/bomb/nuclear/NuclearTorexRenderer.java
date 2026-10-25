package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearTorex;
import com.hbm.client.render.WorldEffectsForward;
import com.hbm.client.render.WorldEffectsDepth;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/** Original cloudlets and additive flash, submitted after translucent occluders have their depth. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class NuclearTorexRenderer extends EntityRenderer<NuclearTorex> {
    private static final ResourceLocation CLOUD=ResourceLocation.fromNamespaceAndPath("hbm","textures/particle/particle_base.png");
    private static final ResourceLocation FLASH=ResourceLocation.fromNamespaceAndPath("hbm","textures/particle/flare.png");
    private static final RenderType CLOUD_MAIN=TorexRenderTypes.cloud(CLOUD,false,false),FLASH_MAIN=TorexRenderTypes.cloud(FLASH,true,false);
    private static final RenderType CLOUD_PARTICLES=TorexRenderTypes.cloud(CLOUD,false,true),FLASH_PARTICLES=TorexRenderTypes.cloud(FLASH,true,true);
    private static final RenderType CLOUD_DEPTH=TorexRenderTypes.depth(CLOUD),FLASH_DEPTH=TorexRenderTypes.depth(FLASH);
    private static final RenderType CLOUD_EARLY_DEPTH=TorexRenderTypes.depth(CLOUD,false);
    private static final MultiBufferSource.BufferSource BUFFERS=MultiBufferSource.immediate(new ByteBufferBuilder(262144));
    private static final ArrayList<NuclearTorex> CLOUDS=new ArrayList<>();
    private static final Comparator<Billboard> BACK_TO_FRONT=(a,b)->Double.compare(b.distance,a.distance);
    private static Billboard[] billboards=new Billboard[1024];
    private static int count;

    public NuclearTorexRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(NuclearTorex cloud){return CLOUD;}
    @Override public boolean shouldRender(NuclearTorex cloud,Frustum frustum,double x,double y,double z){return true;}
    // LevelRenderer rejects ordinary entities when their centre section is not compiled. A nuclear
    // cloud spans many sections and its centre is being destroyed: draw it in the world stage below.
    @Override public void render(NuclearTorex cloud,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){}

    @SubscribeEvent public static void renderClouds(RenderLevelStageEvent event){
        if(event.getStage()!=RenderLevelStageEvent.Stage.AFTER_WEATHER)return;
        if(WorldEffectsForward.defer(()->renderClouds(event)))return;
        var mc=Minecraft.getInstance();if(mc.level==null||mc.player==null)return;
        CLOUDS.clear();count=0;
        var camera=event.getCamera();var eye=camera.getPosition();
        float partial=event.getPartialTick().getGameTimeDeltaPartialTick(false);
        for(var entity:mc.level.entitiesForRendering())if(entity instanceof NuclearTorex cloud&&!cloud.isRemoved()&&cloud.visualReady()){
            CLOUDS.add(cloud);
            double scale=cloud.getScale(),greying=cloud.getGreying();
            for(var part:cloud.cloudlets){
                float alpha=part.getAlpha();if(alpha<=0)continue;
                populate(next(),cloud,part,partial,eye,scale,greying,alpha);
            }
            renderFeedback(cloud);
        }
        if(CLOUDS.isEmpty())return;
        // Actual interpolated, scaled positions matter here: the shockwave intentionally is unscaled.
        Arrays.sort(billboards,0,count,BACK_TO_FRONT);
        var pose=event.getPoseStack();pose.pushPose();boolean deferred=false;
        try(var cutoff=WorldEffectsDepth.colour()){
            WorldEffectsDepth.shader(MukeClient.additiveShader);
            var matrix=pose.last().pose();var basis=new Basis(camera);
            boolean fabulous=Minecraft.useShaderTransparency()&&!WorldEffectsForward.pack();
            var type=fabulous?CLOUD_PARTICLES:CLOUD_MAIN;
            if(count>0){var out=BUFFERS.getBuffer(type);for(int i=0;i<count;i++){var q=billboards[i];billboard(out,matrix,basis,q.x,q.y,q.z,q.scale,q.r,q.g,q.b,q.alpha);}BUFFERS.endBatch(type);}
            flashes(fabulous?FLASH_PARTICLES:FLASH_MAIN,matrix,basis,partial,eye);
            if(fabulous){
                // Writing depth during the colour pass clips correctly ordered, overlapping
                // translucent sheets. Populate only the compositor depth after all colour is present.
                if(count>0){var out=BUFFERS.getBuffer(CLOUD_DEPTH);for(int i=0;i<count;i++){var q=billboards[i];billboard(out,matrix,basis,q.x,q.y,q.z,q.scale,q.r,q.g,q.b,q.alpha);}BUFFERS.endBatch(CLOUD_DEPTH);}
                flashes(FLASH_DEPTH,matrix,basis,partial,eye);
            }else if(NuclearTransparencyLayers.active()){
                var backMatrix=new Matrix4f(matrix);int backCount=count;
                NuclearTransparencyLayers.deferBehind(()->{
                    try{if(backCount>0){var out=BUFFERS.getBuffer(CLOUD_MAIN);for(int i=0;i<backCount;i++){var q=billboards[i];billboard(out,backMatrix,basis,q.x,q.y,q.z,q.scale,q.r,q.g,q.b,q.alpha);}BUFFERS.endBatch(CLOUD_MAIN);}flashes(FLASH_MAIN,backMatrix,basis,partial,eye);}
                    finally{CLOUDS.clear();}
                });deferred=true;
            }
        }finally{
            pose.popPose();if(!deferred)CLOUDS.clear();
            // AFTER_WEATHER is still inside LevelRenderer's weather target scope; leave that
            // target and its native depth mask intact for other listeners and the world border.
            if(!WorldEffectsForward.pack()&&Minecraft.useShaderTransparency()&&event.getLevelRenderer().getWeatherTarget()!=null)event.getLevelRenderer().getWeatherTarget().bindWrite(false);
            RenderSystem.depthMask(!WorldEffectsForward.pack()&&Minecraft.useShaderTransparency());
        }
    }
    public static boolean hasDepthClouds(){
        var level=Minecraft.getInstance().level;if(level==null)return false;
        for(var entity:level.entitiesForRendering())if(entity instanceof NuclearTorex cloud&&!cloud.isRemoved()&&cloud.visualReady())for(var part:cloud.cloudlets)if(part.getAlpha()>0)return true;
        return false;
    }
    /** Stream actual current cloudlets, without the late lists/sort, feedback, simulation or additive flashes. */
    public static void renderEarlyDepth(net.minecraft.client.Camera camera,float partial,Matrix4f matrix){
        var level=Minecraft.getInstance().level;if(level==null)return;
        var eye=camera.getPosition();var basis=new Basis(camera);var quad=new Billboard();VertexConsumer out=null;
        WorldEffectsDepth.shader(MukeClient.additiveShader);
        try{
            for(var entity:level.entitiesForRendering())if(entity instanceof NuclearTorex cloud&&!cloud.isRemoved()&&cloud.visualReady()){
                double scale=cloud.getScale(),greying=cloud.getGreying();
                for(var part:cloud.cloudlets){
                    float alpha=part.getAlpha();if(alpha<=0)continue;
                    if(out==null)out=BUFFERS.getBuffer(CLOUD_EARLY_DEPTH);
                    populate(quad,cloud,part,partial,eye,scale,greying,alpha);
                    billboard(out,matrix,basis,quad.x,quad.y,quad.z,quad.scale,quad.r,quad.g,quad.b,quad.alpha);
                }
            }
        }finally{if(out!=null)BUFFERS.endBatch(CLOUD_EARLY_DEPTH);}
    }
    private static void populate(Billboard quad,NuclearTorex cloud,NuclearTorex.Cloudlet part,float partial,net.minecraft.world.phys.Vec3 eye,double scale,double greying,float alpha){
                double x=Mth.lerp(partial,part.prevPosX,part.posX)-cloud.getX();
                double y=Mth.lerp(partial,part.prevPosY,part.posY)-cloud.getY();
                double z=Mth.lerp(partial,part.prevPosZ,part.posZ)-cloud.getZ();
                if(part.type!=NuclearTorex.TorexType.SHOCK){x*=scale;y*=scale;z*=scale;}
                x+=cloud.getX()-eye.x;y+=cloud.getY()-eye.y;z+=cloud.getZ()-eye.z;
                quad.x=(float)x;quad.y=(float)y;quad.z=(float)z;quad.distance=x*x+y*y+z*z;
                quad.scale=part.getScale();quad.alpha=alpha;
                if(part.type==NuclearTorex.TorexType.CONDENSATION){quad.r=quad.g=quad.b=.9F;}
                else{
                    double brightness=.75F*part.colorMod*(greying+(part.type==NuclearTorex.TorexType.RING?1:0));
                    quad.r=(float)(Mth.lerp(partial,part.prevColor.xCoord,part.color.xCoord)*brightness);
                    quad.g=(float)(Mth.lerp(partial,part.prevColor.yCoord,part.color.yCoord)*brightness);
                    quad.b=(float)(Mth.lerp(partial,part.prevColor.zCoord,part.color.zCoord)*brightness);
                }
    }
    private static void flashes(RenderType type,Matrix4f matrix,Basis basis,float partial,net.minecraft.world.phys.Vec3 eye){
        VertexConsumer out=null;
        for(var cloud:CLOUDS)if(cloud.tickCount<101){
            if(out==null)out=BUFFERS.getBuffer(type);
            double age=Math.min(cloud.tickCount+partial,100);float alpha=(float)((100D-age)/100F);
            var random=new Random(cloud.getId());
            for(int i=0;i<3;i++){
                float x=(float)(random.nextGaussian()*.5F*cloud.rollerSize+cloud.getX()-eye.x);
                float y=(float)(random.nextGaussian()*.5F*cloud.rollerSize+cloud.coreHeight+cloud.getY()-eye.y);
                float z=(float)(random.nextGaussian()*.5F*cloud.rollerSize+cloud.getZ()-eye.z);
                billboard(out,matrix,basis,x,y,z,(float)(25*cloud.rollerSize),1,1,1,alpha);
            }
        }
        if(out!=null)BUFFERS.endBatch(type);
    }
    private static Billboard next(){
        if(count==billboards.length)billboards=Arrays.copyOf(billboards,billboards.length*2);
        var result=billboards[count];if(result==null)billboards[count]=result=new Billboard();count++;return result;
    }
    private static void renderFeedback(NuclearTorex cloud){
        if(!cloud.initialFeedbackAllowed())return;
        long now=System.currentTimeMillis();if(cloud.tickCount<10&&now-NuclearClientEffects.flashTimestamp>1000)NuclearClientEffects.flashTimestamp=now;
        if(cloud.didPlaySound&&!cloud.didShake&&now-NuclearClientEffects.shakeTimestamp>1000){
            NuclearClientEffects.shakeTimestamp=now;cloud.didShake=true;var player=Minecraft.getInstance().player;
            player.animateHurt(0);player.hurtTime=15;player.hurtDuration=15;
        }
    }
    private static final class Billboard{float x,y,z,scale,r,g,b,alpha;double distance;}
    /** The original ActiveRenderInfo basis, computed once instead of four temporary vectors per quad. */
    private static final class Basis{
        final float x,z,tx,tz,vertical;
        Basis(Camera camera){float yaw=camera.getYRot()*Mth.DEG_TO_RAD,pitch=camera.getXRot()*Mth.DEG_TO_RAD;x=Mth.cos(yaw);z=Mth.sin(yaw);tx=-z*Mth.sin(pitch);tz=x*Mth.sin(pitch);vertical=Mth.cos(pitch);}
    }
    private static void billboard(VertexConsumer out,Matrix4f pose,Basis basis,float x,float y,float z,float scale,float r,float g,float b,float alpha){
        r=Mth.clamp(r,0,1);g=Mth.clamp(g,0,1);b=Mth.clamp(b,0,1);alpha=Mth.clamp(alpha,0,1);
        float sx=basis.x*scale,sz=basis.z*scale,tx=basis.tx*scale,tz=basis.tz*scale,sy=basis.vertical*scale;
        vertex(out,pose,x-sx-tx,y-sy,z-sz-tz,1,1,r,g,b,alpha);
        vertex(out,pose,x-sx+tx,y+sy,z-sz+tz,1,0,r,g,b,alpha);
        vertex(out,pose,x+sx+tx,y+sy,z+sz+tz,0,0,r,g,b,alpha);
        vertex(out,pose,x+sx-tx,y-sy,z+sz-tz,0,1,r,g,b,alpha);
    }
    private static void vertex(VertexConsumer out,Matrix4f pose,float x,float y,float z,float u,float v,float r,float g,float b,float alpha){out.addVertex(pose,x,y,z).setUv(u,v).setColor(r,g,b,alpha);}
}
