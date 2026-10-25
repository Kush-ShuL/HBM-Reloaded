package com.hbm.client.weapon.energy;
import com.hbm.client.weapon.SednaBeamRenderContext;
import com.hbm.client.weapon.forward.GunForwardEffects;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
/** Source LegoClient Energy callbacks with complete BeamPronter RANDOM/SOLID geometry. */
public final class EnergyBeams {
    public static void lightning(EntityBulletBeamBase beam,float partial,boolean sub){var c=SednaBeamRenderContext.current();var pose=c.poses();pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180-beam.getYRot()));pose.mulPose(Axis.XP.rotationDegrees(-beam.getXRot()-90));
        double age=Mth.clamp(1D-((double)beam.tickCount-2+partial)/beam.getBulletConfig().expires,0,1);float width=(float)(age/2+(sub?.15:.5));pose.scale(width,1,width);
        int inner=((int)(0x20*age)<<16)|((int)(0x20*age)<<8)|(int)(0x40*age),outer=((int)(0x40*age)<<16)|((int)(0x40*age)<<8)|(int)(0x80*age);
        solid(pose,c.buffers(),beam.beamLength,inner,inner,beam.tickCount/3,(int)(beam.beamLength/2+1),.075F,4,.25F);
        solid(pose,c.buffers(),beam.beamLength,outer,outer,beam.tickCount,(int)(beam.beamLength/2+1),.075F*7,2,.0625F);
        solid(pose,c.buffers(),beam.beamLength,outer,outer,beam.tickCount/2,(int)(beam.beamLength/2+1),.075F*7,2,.0625F);pose.popPose();
    }
    public static void laser(EntityBulletBeamBase beam,float partial,boolean emerald){laser(beam,partial,emerald?0x15:0x80,emerald?0x80:0x15,0x15);}
    public static void laser(EntityBulletBeamBase beam,float partial,int r,int g,int b){var c=SednaBeamRenderContext.current();var pose=c.poses();pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180-beam.getYRot()));pose.mulPose(Axis.XP.rotationDegrees(-beam.getXRot()-90));double age=Mth.clamp(1D-((double)beam.tickCount-2+partial)/beam.getBulletConfig().expires,0,1);pose.scale((float)(age/2+.5),1,(float)(age/2+.5));
        int color=((int)(r*age)<<16)|((int)(g*age)<<8)|(int)(b*age);
        if(!GunForwardEffects.captureWorldGeometry(EnergyRenderTypes.LASER_BEAM,buffers->solid(pose,buffers,beam.beamLength,color,color,beam.tickCount/3,(int)(beam.beamLength/2+1),0F,4,.025F,EnergyRenderTypes.LASER_BEAM)))
            solid(pose,c.buffers(),beam.beamLength,color,color,beam.tickCount/3,(int)(beam.beamLength/2+1),0F,4,.025F,EnergyRenderTypes.LASER_BEAM);
        pose.popPose();
    }
    public static void solid(PoseStack pose,MultiBufferSource buffers,double length,int outer,int inner,int seed,int segments,float size,int layers,float thickness){
        solid(pose,buffers,length,outer,inner,seed,segments,size,layers,thickness,EnergyRenderTypes.BEAM);
    }
    /** Explicit solid volume; the source flare and generic lightning beams keep their colour-only materials. */
    public static void solidWithDepth(PoseStack pose,MultiBufferSource buffers,double length,int outer,int inner,int seed,int segments,float size,int layers,float thickness){
        if(!GunForwardEffects.captureWorldGeometry(EnergyRenderTypes.SOLID_BEAM,immediate->solid(pose,immediate,length,outer,inner,seed,segments,size,layers,thickness,EnergyRenderTypes.SOLID_BEAM)))
            solid(pose,buffers,length,outer,inner,seed,segments,size,layers,thickness,EnergyRenderTypes.SOLID_BEAM);
    }
    private static void solid(PoseStack pose,MultiBufferSource buffers,double length,int outer,int inner,int seed,int segments,float size,int layers,float thickness,RenderType material){
        // Skeleton is (0,length,0), so the source BeamPronter's own180Y and
        // 90deg skeleton yaw are retained even for the degenerate xz vector.
        pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(180));float yaw=(float)(Math.atan2(0D,0D)*180F/Math.PI),pitch=(float)(Math.atan2(length,0F)*180F/Math.PI);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.mulPose(Axis.XP.rotationDegrees(pitch-90));
        var out=buffers.getBuffer(material);var random=new Random(seed);double lastX=0,lastY=0,lastZ=0,step=length/segments;
        for(int i=0;i<=segments;i++){double x=size,z=0;for(int turn=0;turn<2;turn++){float angle=(float)Math.PI*2*random.nextFloat();double next=x*Mth.cos(angle)+z*Mth.sin(angle);z=z*Mth.cos(angle)-x*Mth.sin(angle);x=next;}double y=step*i;
            if(i>0)for(int layer=1;layer<=layers;layer++){float inter=(float)(layer-1)/(layers-1);int r=(int)(((outer>>16)&255)+(((inner>>16)&255)-((outer>>16)&255))*inter),g=(int)(((outer>>8)&255)+(((inner>>8)&255)-((outer>>8)&255))*inter),b=(int)((outer&255)+((inner&255)-(outer&255))*inter),color=0xff000000|r<<16|g<<8|b;float radius=thickness/layers*layer;
                quad(out,pose,color,lastX+radius,lastY,lastZ+radius,lastX+radius,lastY,lastZ-radius,x+radius,y,z-radius,x+radius,y,z+radius);
                quad(out,pose,color,lastX-radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x-radius,y,z+radius);
                quad(out,pose,color,lastX+radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ+radius,x-radius,y,z+radius,x+radius,y,z+radius);
                quad(out,pose,color,lastX+radius,lastY,lastZ-radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x+radius,y,z-radius);
            }lastX=x;lastY=y;lastZ=z;
        }pose.popPose();
    }
    private static void quad(VertexConsumer out,PoseStack poses,int color,double...p){for(int i=0;i<p.length;i+=3)out.addVertex(poses.last(),(float)p[i],(float)p[i+1],(float)p[i+2]).setColor(color);}
    private EnergyBeams(){}
}
