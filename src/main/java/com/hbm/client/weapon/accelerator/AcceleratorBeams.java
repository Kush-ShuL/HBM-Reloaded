package com.hbm.client.weapon.accelerator;
import com.hbm.client.weapon.SednaBeamRenderContext;
import com.hbm.client.weapon.energy.EnergyBeams;
import com.hbm.client.weapon.pepperbox.*;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
/** Original LegoClient Tau/charged Tau/NI4NI layers and taper matrices. */
final class AcceleratorBeams {
    static void tau(EntityBulletBeamBase beam,float partial,boolean charged){
        var context=SednaBeamRenderContext.current();var poses=context.poses();double age=age(beam,partial);poses.pushPose();
        try{
            poses.mulPose(Axis.YP.rotationDegrees(180-beam.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(-beam.getXRot()-90));
            poses.pushPose();poses.scale((float)(age/2+.5),1,(float)(age/2+.5));
            int r=(int)((charged?0x60:0x30)*age),g=(int)((charged?0x50:0x25)*age),b=(int)((charged?0x30:0x10)*age),color=r<<16|g<<8|b;
            EnergyBeams.solid(poses,context.buffers(),beam.beamLength,color,color,(beam.tickCount+beam.getId())/2,(int)(beam.beamLength/2+1),.075F*4F,2,.0625F);poses.popPose();
            poses.scale((float)(age*2),1,(float)(age*2));poses.translate(0,beam.beamLength,0);poses.mulPose(Axis.ZP.rotationDegrees(-90));taper(beam.beamLength,charged?0xfff0a0:0xffbf00,0xffffff);
        }finally{poses.popPose();}
    }
    static void ni4ni(EntityBulletBeamBase beam,float partial){
        var context=SednaBeamRenderContext.current();var poses=context.poses();double age=age(beam,partial);poses.pushPose();
        try{poses.mulPose(Axis.YP.rotationDegrees(180-beam.getYRot()));poses.mulPose(Axis.XP.rotationDegrees(-beam.getXRot()-90));poses.scale((float)(age*5),1,(float)(age*5));poses.translate(0,beam.beamLength,0);poses.mulPose(Axis.ZP.rotationDegrees(-90));taper(beam.beamLength,0xaad2e5,0xffffff);}finally{poses.popPose();}
    }
    private static double age(EntityBulletBeamBase beam,float partial){return Mth.clamp(1D-((double)beam.tickCount-2+partial)/(double)beam.getBulletConfig().expires,0,1);}
    private static void taper(double length,int dark,int light){var context=SednaBeamRenderContext.current();GunBulletRenderContext.begin(context.poses(),context.buffers(),context.light());try{GunBulletRenderer.renderBulletStandard(dark,light,length,.03125D,.03125D*.25D,true);}finally{GunBulletRenderContext.end();}}
    private AcceleratorBeams(){}
}
