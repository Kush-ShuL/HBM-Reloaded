package com.hbm.client.launchpadsoyuz;

import com.hbm.client.soyuzlauncher.SoyuzModels;
import com.hbm.machine.launchpadsoyuz.*;
import com.hbm.machine.launchpadsoyuz.LaunchpadSoyuzEntity.SoyuzStatus;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Actual source launchpad/carriage/16 wheels/rotor/counter-rotating mount/five struts, not the103 launcher's model. */
public final class LaunchpadSoyuzRenderer implements BlockEntityRenderer<LaunchpadSoyuzEntity> {
    public LaunchpadSoyuzRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(LaunchpadSoyuzEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        float rotation=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};
        float rotor=Mth.clamp(machine.getInterpPos(LaunchpadSoyuzEntity.INDEX_ROTOR,partial)*-180+180,0,180),carriage=Mth.clamp(machine.getInterpPos(LaunchpadSoyuzEntity.INDEX_CARRIAGE,partial)*-19.5F+19.5F,0,19.5F),wheels=(float)(carriage*360D/Math.PI),tilt=machine.getInterpPos(LaunchpadSoyuzEntity.INDEX_TILT,partial);
        boolean renderRocket=machine.loadedType>=0&&machine.soyuzStatus!=SoyuzStatus.ABSENT,lockRocket=machine.soyuzStatus==SoyuzStatus.LAUNCHING;
        pose.pushPose();try{
            pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.translate(-4,0,-4);
            if(renderRocket&&lockRocket){pose.pushPose();pose.translate(0,4,0);pose.mulPose(Axis.YP.rotationDegrees(-rotation));SoyuzModels.rocket(machine.loadedType,pose,buffers,light,overlay);pose.popPose();}
            part("Launchpad",pose,buffers,light,overlay);
            for(int i=1;i<=5;i++){pose.pushPose();float extension=i==5?3F:4.5F;pose.translate(0,0,Mth.clamp(machine.getInterpPos(i-1,partial)*-extension+extension,0,extension));part("Strut"+i,pose,buffers,light,overlay);pose.popPose();}
            pose.translate(0,0,-carriage);pose.translate(0,1.5,-32);pose.mulPose(Axis.XP.rotationDegrees(-tilt));pose.translate(0,-1.5,32);part("Carriage",pose,buffers,light,overlay);
            double[] forward={17,19,29,31},side={6.75,5.25,-5.25,-6.75};
            for(int i=1;i<=4;i++)for(int j=1;j<=4;j++){pose.pushPose();pose.translate(side[j-1],0,-forward[i-1]);pose.mulPose(Axis.YP.rotationDegrees(wheels*(j%2==0?-1:1)));pose.translate(-side[j-1],0,forward[i-1]);part("Wheel_"+i+"_"+j,pose,buffers,light,overlay);pose.popPose();}
            pose.translate(0,24.5,-18);pose.mulPose(Axis.XP.rotationDegrees(-rotor));pose.translate(0,-24.5,18);part("Rotor",pose,buffers,light,overlay);
            pose.translate(0,24.5,-6);pose.mulPose(Axis.XP.rotationDegrees(rotor));pose.translate(0,-24.5,6);part("Mount",pose,buffers,light,overlay);
            if(renderRocket&&!lockRocket){pose.translate(0,4,0);pose.mulPose(Axis.YP.rotationDegrees(-rotation));SoyuzModels.rocket(machine.loadedType,pose,buffers,light,overlay);}
        }finally{pose.popPose();}
    }
    private static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LaunchpadSoyuzModels.part(name,pose,buffers,light,overlay);}
    @Override public boolean shouldRenderOffScreen(LaunchpadSoyuzEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(LaunchpadSoyuzEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-64,p.getY()-1,p.getZ()-64,p.getX()+65,p.getY()+81,p.getZ()+65);}
}
