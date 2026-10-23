package com.hbm.client.tesla;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
/** Exact BeamPronter RANDOM/SOLID: two 0x404040 additive layers, .125 jitter, .03125 thickness. */
public final class TeslaArcRenderer extends RenderType {
    private TeslaArcRenderer(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
    // BeamPronter explicitly disables depth writes while retaining ordinary depth tests.
    private static final RenderType ARC=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_tesla_arc",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,1536,false,false,CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).createCompositeState(false)),"Tesla/Crab/TaintArc");
    public static void beam(Vec3 skeleton,PoseStack poses,MultiBufferSource buffers,int start,int segments){
        if(segments<=0)return;
        poses.pushPose();float yaw=(float)(Math.atan2(skeleton.x,skeleton.z)*180F/Math.PI),horizontal=(float)Math.sqrt(skeleton.x*skeleton.x+skeleton.z*skeleton.z),pitch=(float)(Math.atan2(skeleton.y,horizontal)*180F/Math.PI);
        poses.mulPose(Axis.YP.rotationDegrees(180));poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.mulPose(Axis.XP.rotationDegrees(pitch-90));
        var out=buffers.getBuffer(ARC);var random=new Random(start);double segmentLength=skeleton.length()/segments,lastX=0,lastY=0,lastZ=0;
        for(int i=0;i<=segments;i++){
            double x=.125F,z=0;for(int turn=0;turn<2;turn++){float angle=(float)Math.PI*2*random.nextFloat();double nextX=x*Mth.cos(angle)+z*Mth.sin(angle);z=z*Mth.cos(angle)-x*Mth.sin(angle);x=nextX;}double y=segmentLength*i;
            if(i>0)for(int layer=1;layer<=2;layer++){
                float radius=.03125F/2*layer;
                quad(poses,out,lastX+radius,lastY,lastZ+radius,lastX+radius,lastY,lastZ-radius,x+radius,y,z-radius,x+radius,y,z+radius);
                quad(poses,out,lastX-radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x-radius,y,z+radius);
                quad(poses,out,lastX+radius,lastY,lastZ+radius,lastX-radius,lastY,lastZ+radius,x-radius,y,z+radius,x+radius,y,z+radius);
                quad(poses,out,lastX+radius,lastY,lastZ-radius,lastX-radius,lastY,lastZ-radius,x-radius,y,z-radius,x+radius,y,z-radius);
            }lastX=x;lastY=y;lastZ=z;
        }poses.popPose();
    }
    private static void quad(PoseStack poses,VertexConsumer out,double...vertices){for(int i=0;i<12;i+=3)out.addVertex(poses.last(),(float)vertices[i],(float)vertices[i+1],(float)vertices[i+2]).setColor(0xff404040);}
}
