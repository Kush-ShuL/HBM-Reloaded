package com.hbm.client.fensu;

import com.hbm.client.model.*;
import com.hbm.machine.fensu.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.*;

/** Exact source fensu2.obj groups/pivots, three fading trail strips, scrolling plasma, sparkle and four seeded two-pass zaps. */
public final class FensuRenderer implements BlockEntityRenderer<FensuBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/fensu2.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/fensu2.png"),PLASMA=ResourceLocation.parse("hbm:textures/models/fusion/plasma.png"),SPARKLE=ResourceLocation.parse("hbm:textures/models/fusion/plasma_sparkle.png");
    public FensuRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void parts(PoseStack pose,MultiBufferSource buffers,int light,int overlay,boolean unlit,String... names){var output=unlit?FensuRenderTypes.unlitVertices(buffers.getBuffer(FensuRenderTypes.unlit(TEXTURE)),0,0):buffers.getBuffer(RenderType.entityCutout(TEXTURE));LegacyObjRenderer.renderOnly(model(),pose,output,unlit?LightTexture.FULL_BRIGHT:light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,names);}
    static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.mulPose(Axis.YP.rotationDegrees(-90));pose.scale(.5F,.5F,.5F);parts(pose,buffers,light,overlay,false,"Base","Wheel");parts(pose,buffers,light,overlay,true,"Lights");}
    @Override public void render(FensuBlockEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->270;case SOUTH->90;case EAST->180;default->0;};
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));parts(pose,buffers,light,overlay,false,"Base");
        pose.pushPose();pose.translate(0,5.5,0);pose.mulPose(Axis.XP.rotationDegrees(machine.prevRotation+(machine.rotation-machine.prevRotation)*partial));pose.translate(0,-5.5,0);parts(pose,buffers,light,overlay,false,"Wheel");parts(pose,buffers,light,overlay,true,"Lights");
        pose.pushPose();pose.translate(0,5.5,0);trails(pose,buffers,machine.speed());pose.popPose();sparkle(machine,pose,buffers,overlay);pose.popPose();
        if(machine.speed()>0)zaps(machine,pose,buffers);pose.popPose();
    }
    private static void trails(PoseStack pose,MultiBufferSource buffers,float speed){double span=speed*.75;if(span<=0)return;var output=buffers.getBuffer(FensuRenderTypes.ARC);double len=4.25,width=.125;
        for(int side=-1;side<=1;side+=2)for(int i=0;i<8;i++){
            // Vec3NT uses double Math.cos/sin with old left-handed rotateAroundX, not a GL/JOML positiveX rotation.
            double angle=i*45D/180D*Math.PI,y=Math.cos(angle),z=-Math.sin(angle),x=.8125*side;
            for(int strip=0;strip<3;strip++){
                double c=Math.cos(span/180D*Math.PI),s=Math.sin(span/180D*Math.PI),nextY=y*c+z*s,nextZ=z*c-y*s;
                int alpha=(int)((.75-strip*.25)*255),nextAlpha=(int)((.5-strip*.25)*255);
                vertex(pose,output,x,y*(len-width),z*(len-width),0x00ffff00|alpha<<24);vertex(pose,output,x,y*(len+width),z*(len+width),0x00ffff00|alpha<<24);vertex(pose,output,x,nextY*(len+width),nextZ*(len+width),0x00ffff00|nextAlpha<<24);vertex(pose,output,x,nextY*(len-width),nextZ*(len-width),0x00ffff00|nextAlpha<<24);y=nextY;z=nextZ;
            }
        }
    }
    private static void sparkle(FensuBlockEntity machine,PoseStack pose,MultiBufferSource buffers,int overlay){long time=FensuClient.renderClock;float multiplier=machine.speed()/15F,alpha=.45F+(float)(Math.sin(time/1000D)*.15F);double main=Math.sin(Math.PI/2D*Math.cos(time/1000D))%1D,spin=time/250D*-1%1D,osc=Math.sin(time/1000D)*.5D%1D;plasma(pose,buffers,overlay,PLASMA,0,(float)main,1,.25F,.75F,alpha*multiplier);var player=Minecraft.getInstance().player;var pos=machine.getBlockPos();if(player!=null&&player.distanceToSqr(pos.getX()+.5,pos.getY()+2.5,pos.getZ()+.5)<10000)plasma(pose,buffers,overlay,SPARKLE,(float)spin,(float)osc,1,.5F,1,.75F*multiplier);}
    private static void plasma(PoseStack pose,MultiBufferSource buffers,int overlay,ResourceLocation texture,float u,float v,float r,float g,float b,float a){int argb=(int)(a*255)<<24|(int)(r*255)<<16|(int)(g*255)<<8|(int)(b*255);var output=FensuRenderTypes.unlitVertices(buffers.getBuffer(FensuRenderTypes.plasma(texture)),u,v);LegacyObjRenderer.renderOnly(model(),pose,output,LightTexture.FULL_BRIGHT,overlay,argb,LegacyObjRenderer.Convention.HFR,"Plasma");}
    private static void zaps(FensuBlockEntity machine,PoseStack pose,MultiBufferSource buffers){var random=new Random(machine.getLevel().getGameTime()/5);random.nextBoolean();for(int i=0;i<4;i++)if(random.nextBoolean()){double x=(i%2==0?1:-1)*3.125,z=i<2?3.75:-3.75;pose.pushPose();pose.translate(x,5.5,0);Vec3 skeleton=new Vec3(x>0?-1.375:1.375,-2.625,z);int seed=(int)(System.currentTimeMillis()%1000)/50;beam(skeleton,pose,buffers,seed,15,.25F);beam(skeleton,pose,buffers,seed,1,0);pose.popPose();}}
    private static void beam(Vec3 skeleton,PoseStack pose,MultiBufferSource buffers,int seed,int segments,float size){pose.pushPose();float yaw=(float)(Math.atan2(skeleton.x,skeleton.z)*180F/Math.PI),horizontal=(float)Math.sqrt(skeleton.x*skeleton.x+skeleton.z*skeleton.z),pitch=(float)(Math.atan2(skeleton.y,horizontal)*180F/Math.PI);pose.mulPose(Axis.YP.rotationDegrees(180));pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.mulPose(Axis.XP.rotationDegrees(pitch-90));var output=buffers.getBuffer(FensuRenderTypes.ARC);var random=new Random(seed);double step=skeleton.length()/segments,lastX=0,lastY=0,lastZ=0;
        for(int i=0;i<=segments;i++){double x=size,z=0;for(int turn=0;turn<2;turn++){float angle=(float)Math.PI*2*random.nextFloat();double nx=x*Mth.cos(angle)+z*Mth.sin(angle);z=z*Mth.cos(angle)-x*Mth.sin(angle);x=nx;}double y=step*i;
            if(i>0)for(int layer=1;layer<=3;layer++){float inter=(layer-1)/2F,radius=.0625F/3*layer;int color=0xff000000|(int)(64-64*inter)<<16|(int)(64+(32-64)*inter)<<8|64;quad(pose,output,color,lastX+radius,lastY,lastZ+radius,lastX+radius,lastY,lastZ-radius,x+radius,y,z-radius,x+radius,y,z+radius);quad(pose,output,color,lastX-radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x-radius,y,z+radius);quad(pose,output,color,lastX+radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ+radius,x-radius,y,z+radius,x+radius,y,z+radius);quad(pose,output,color,lastX+radius,lastY,lastZ-radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x+radius,y,z-radius);}lastX=x;lastY=y;lastZ=z;
        }pose.popPose();
    }
    private static void quad(PoseStack pose,VertexConsumer output,int color,double... positions){for(int i=0;i<12;i+=3)vertex(pose,output,positions[i],positions[i+1],positions[i+2],color);}
    private static void vertex(PoseStack pose,VertexConsumer output,double x,double y,double z,int color){output.addVertex(pose.last(),(float)x,(float)y,(float)z).setColor(color);}
    @Override public boolean shouldRenderOffScreen(FensuBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(FensuBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-4,p.getY(),p.getZ()-4,p.getX()+5,p.getY()+10,p.getZ()+5);}
}
