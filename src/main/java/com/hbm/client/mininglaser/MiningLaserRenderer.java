package com.hbm.client.mininglaser;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.hbm.machine.mininglaser.MiningLaserEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Literal RenderLaserMiner matrices and three BeamPronter SPIRAL/SOLID calls using the existing real beam kernel. */
public final class MiningLaserRenderer implements BlockEntityRenderer<MiningLaserEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/mining_laser.obj");
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void part(String name,String texture,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(ResourceLocation.parse("hbm:textures/models/machines/mining_laser_"+texture+".png"))),light,overlay,-1,LegacyObjRenderer.Convention.HFR,name);}
    public MiningLaserRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(MiningLaserEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        double tx=(machine.targetX-machine.lastTargetX)*partial+machine.lastTargetX;
        double ty=(machine.targetY-machine.lastTargetY)*partial+machine.lastTargetY;
        double tz=(machine.targetZ-machine.lastTargetZ)*partial+machine.lastTargetZ;
        double vx=tx-machine.getBlockPos().getX(),vy=ty-machine.getBlockPos().getY()+3,vz=tz-machine.getBlockPos().getZ();
        var normal=LegacyVector.createVectorHelper(vx,vy,vz).normalize();normal.xCoord*=1.5;normal.yCoord*=1.5;normal.zCoord*=1.5;
        var vec=LegacyVector.createVectorHelper(vx-normal.xCoord,vy-normal.yCoord,vz-normal.zCoord);
        double length=vec.lengthVector(),yaw=Math.toDegrees(Math.atan2(vec.xCoord,vec.zCoord));
        double horizontal=(double)(float)Math.sqrt(vec.xCoord*vec.xCoord+vec.zCoord*vec.zCoord),pitch=Math.toDegrees(Math.atan2(vec.yCoord,horizontal));
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,-1,.5F),pose,buffers);
        pose.pushPose();pose.translate(.5,-1,.5);part("Base","base",pose,buffers,light,overlay);
        pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)yaw));part("Pivot","pivot",pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees((float)yaw));pose.translate(0,-1,0);pose.mulPose(Axis.XN.rotationDegrees((float)(pitch+90)));pose.translate(0,1,0);part("Laser","laser",pose,buffers,light,overlay);pose.popPose();
        if(machine.beam){pose.translate(normal.xCoord,normal.yCoord-1,normal.zCoord);int range=(int)Math.ceil(length*.5D),start=(int)machine.getLevel().getGameTime()*-25%360;for(int i=0;i<3;i++)DfcComponentBeams.draw(pose,buffers,new Vec3(vec.xCoord,vec.yCoord,vec.zCoord),false,true,0xA00000,0xA00000,start+120*i,range*2,.075F,3,.025F);}
        pose.popPose();
    }
    @Override public boolean shouldRenderOffScreen(MiningLaserEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(MiningLaserEntity machine){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
