package com.hbm.client.batterysocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/** Source BeamPronter RANDOM/SOLID path, including its initial radial offset and three additive layers. */
final class SocketArcRenderer extends RenderType {
    private SocketArcRenderer() {super("unused",com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR,com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    private static final RenderType ARC=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_socket_source_arc",com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR,com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,4096,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"BatterySocket/Arc");
    static void beam(Vec3 skeleton,PoseStack poses,MultiBufferSource buffers,int start,int segments,float size) {
        poses.pushPose();
        float yaw=(float)(Math.atan2(skeleton.x,skeleton.z)*180F/Math.PI),horizontal=(float)Math.sqrt(skeleton.x*skeleton.x+skeleton.z*skeleton.z);
        float pitch=(float)(Math.atan2(skeleton.y,horizontal)*180F/Math.PI);
        poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.mulPose(Axis.XP.rotationDegrees(pitch-90));
        var out=buffers.getBuffer(ARC);var random=new Random(start);double length=skeleton.length(),segmentLength=length/segments,lastX=0,lastY=0,lastZ=0;
        for(int i=0;i<=segments;i++) {
            double x=size,z=0;
            for(int turn=0;turn<2;turn++) {float angle=(float)Math.PI*2*random.nextFloat();double nextX=x*Mth.cos(angle)+z*Mth.sin(angle);z=z*Mth.cos(angle)-x*Mth.sin(angle);x=nextX;}
            double y=segmentLength*i;
            if(i>0) for(int layer=1;layer<=3;layer++) {
                float inter=(layer-1)/2F;int r=(int)(64+(0-64)*inter),g=(int)(64+(32-64)*inter),b=64,color=0xFF000000|r<<16|g<<8|b;
                float radius=.025F/3*layer;
                quad(poses,out,color,lastX+radius,lastY,lastZ+radius,lastX+radius,lastY,lastZ-radius,x+radius,y,z-radius,x+radius,y,z+radius);
                quad(poses,out,color,lastX-radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x-radius,y,z+radius);
                quad(poses,out,color,lastX+radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ+radius,x-radius,y,z+radius,x+radius,y,z+radius);
                quad(poses,out,color,lastX+radius,lastY,lastZ-radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x+radius,y,z-radius);
            }
            lastX=x;lastY=y;lastZ=z;
        }
        poses.popPose();
    }
    private static void quad(PoseStack poses,VertexConsumer out,int color,double...p) {for(int i=0;i<12;i+=3)out.addVertex(poses.last(),(float)p[i],(float)p[i+1],(float)p[i+2]).setColor(color);}
}
