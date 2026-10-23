package com.hbm.client.reactor.dfc.components;
import com.hbm.bomb.nuclear.LegacyVector;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import java.util.*;
import net.minecraft.client.renderer.*;
/** Exact BeamPronter SPIRAL/RANDOM and SOLID/LINE, including its two random rotations and depth-mask distinction. */
public final class DfcComponentBeams extends RenderType {
 static final RenderType SOLID=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_solid_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,16384,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader)).setTransparencyState(LIGHTNING_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false)),"DFC+MiningLaser/SolidBeam");
 static final RenderType LINES=com.hbm.client.render.WorldEffectMeshes.register(create("hbm_dfc_line_beam",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.DEBUG_LINES,16384,false,false,CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).setLineState(new LineStateShard(OptionalDouble.of(1D))).createCompositeState(false)),"DFC+MiningLaser/LineBeam");
 static void draw(PoseStack pose,MultiBufferSource buffers,int length,boolean randomWave,boolean solid,int outer,int inner,int start,int segments,float size,int layers,float thickness){draw(pose,buffers,(double)length,randomWave,solid,outer,inner,start,segments,size,layers,thickness);}
 public static void draw(PoseStack pose,MultiBufferSource buffers,double length,boolean randomWave,boolean solid,int outer,int inner,int start,int segments,float size,int layers,float thickness){drawDirected(pose,buffers,length,0,0,randomWave,solid,outer,inner,start,segments,size,layers,thickness);}
 /** Original BeamPronter yaw/pitch for non-+Z skeletons; reuse the identical vertex/random kernel. */
 public static void draw(PoseStack pose,MultiBufferSource buffers,net.minecraft.world.phys.Vec3 skeleton,boolean randomWave,boolean solid,int outer,int inner,int start,int segments,float size,int layers,float thickness){float yaw=(float)(Math.atan2(skeleton.x,skeleton.z)*180F/Math.PI),horizontal=(float)Math.sqrt(skeleton.x*skeleton.x+skeleton.z*skeleton.z),pitch=(float)(Math.atan2(skeleton.y,(double)horizontal)*180F/Math.PI);double length=LegacyVector.createVectorHelper(skeleton.x,skeleton.y,skeleton.z).lengthVector();drawDirected(pose,buffers,length,yaw,pitch,randomWave,solid,outer,inner,start,segments,size,layers,thickness);}
 private static void drawDirected(PoseStack pose,MultiBufferSource buffers,double length,float yaw,float pitch,boolean randomWave,boolean solid,int outer,int inner,int start,int segments,float size,int layers,float thickness){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(180));if(yaw!=0)pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.mulPose(Axis.XP.rotationDegrees(pitch-90));var out=buffers.getBuffer(solid?SOLID:LINES);var random=new Random(start);double step=(double)length/segments,lastX=0,lastY=0,lastZ=0;
  for(int i=0;i<=segments;i++){var spinner=LegacyVector.createVectorHelper(size,0,0);if(randomWave){spinner.rotateAroundY((float)Math.PI*2*random.nextFloat());spinner.rotateAroundY((float)Math.PI*2*random.nextFloat());}else{spinner.rotateAroundY((float)Math.PI*(float)start/180F);spinner.rotateAroundY((float)Math.PI*45F/180F*i);}double x=spinner.xCoord,y=step*i,z=spinner.zCoord;if(i>0){if(!solid){vertex(out,pose,outer,x,y,z);vertex(out,pose,outer,lastX,lastY,lastZ);}else{float radius=thickness/layers;for(int layer=1;layer<=layers;layer++){float inter=(float)(layer-1)/(float)(layers-1);int r=(int)(((outer>>16)&255)+(((inner>>16)&255)-((outer>>16)&255))*inter),g=(int)(((outer>>8)&255)+(((inner>>8)&255)-((outer>>8)&255))*inter),b=(int)((outer&255)+((inner&255)-(outer&255))*inter),color=r<<16|g<<8|b;float width=radius*layer;
    quad(out,pose,color,lastX+width,lastY,lastZ+width,lastX+width,lastY,lastZ-width,x+width,y,z-width,x+width,y,z+width);
    quad(out,pose,color,lastX-width,lastY,lastZ+width,lastX-width,lastY,lastZ-width,x-width,y,z-width,x-width,y,z+width);
    quad(out,pose,color,lastX+width,lastY,lastZ+width,lastX-width,lastY,lastZ+width,x-width,y,z+width,x+width,y,z+width);
    quad(out,pose,color,lastX+width,lastY,lastZ-width,lastX-width,lastY,lastZ-width,x-width,y,z-width,x+width,y,z-width);
   }}}lastX=x;lastY=y;lastZ=z;
  }if(!solid){vertex(out,pose,inner,0,0,0);vertex(out,pose,inner,0,length,0);}pose.popPose();
 }
 private static void vertex(VertexConsumer output,PoseStack pose,int color,double x,double y,double z){output.addVertex(pose.last(),(float)x,(float)y,(float)z).setColor(0xff000000|color);}
 private static void quad(VertexConsumer output,PoseStack pose,int color,double... coordinates){for(int i=0;i<coordinates.length;i+=3)vertex(output,pose,color,coordinates[i],coordinates[i+1],coordinates[i+2]);}
 private DfcComponentBeams(){super("unused",DefaultVertexFormat.POSITION_COLOR,VertexFormat.Mode.QUADS,256,false,false,()->{},()->{});}
}
