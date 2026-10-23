package com.hbm.client.model.horse;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Actual HorsePronter136line source helper: original mesh/parts/pivots/order,including reset's retained maleSnoot flag. */
public final class LegacyHorsePronter {
 public static final int HEAD=0,LFL=1,RFL=2,LBL=3,RBL=4,TAIL=5,BODY=6,POSITION=7;
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/mobs/horse.obj");
 private static final double[][] OFFSETS={{0,1.125,.375},{.125,.75,.3125},{-.125,.75,.3125},{.125,.75,-.25},{-.125,.75,-.25},{0,1.125,-.4375},{0,0,0},{0,0,0}};
 private final double[][] pose=new double[8][3];private boolean wings,horn,maleSnoot;
 public void reset(){wings=false;horn=false;for(var angles:pose)Arrays.fill(angles,0);}public void enableHorn(){horn=true;}public void enableWings(){wings=true;}public void setMaleSnoot(){maleSnoot=true;}public void setAlicorn(){enableHorn();enableWings();}
 public void pose(int id,double yaw,double pitch,double roll){pose[id][0]=yaw;pose[id][1]=pitch;pose[id][2]=roll;}
 public void poseStandardSit(){double r=60;pose(BODY,0,-r,0);pose(TAIL,0,45,90);pose(LBL,0,-90+r,35);pose(RBL,0,-90+r,-35);pose(LFL,0,r-10,5);pose(RFL,0,r-10,-5);pose(HEAD,0,r,0);}
 private void transform(PoseStack poses,int id){var p=OFFSETS[id];var a=pose[id];poses.translate(p[0],p[1],p[2]);poses.mulPose(Axis.YP.rotationDegrees((float)a[0]));poses.mulPose(Axis.XP.rotationDegrees((float)a[1]));poses.mulPose(Axis.ZP.rotationDegrees((float)a[2]));poses.translate(-p[0],-p[1],-p[2]);}
 private void part(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,String... groups){LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,groups);}
 private void transformed(WavefrontMesh mesh,PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture,int id,String... groups){poses.pushPose();transform(poses,id);part(mesh,poses,buffers,light,overlay,texture,groups);poses.popPose();}
 public void render(PoseStack poses,MultiBufferSource buffers,int light,int overlay,ResourceLocation texture){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);poses.pushPose();transform(poses,BODY);part(mesh,poses,buffers,light,overlay,texture,"Body");transformed(mesh,poses,buffers,light,overlay,texture,HEAD,horn?new String[]{"Head","Mane",maleSnoot?"NoseMale":"NoseFemale","HornPointy"}:new String[]{"Head","Mane",maleSnoot?"NoseMale":"NoseFemale"});transformed(mesh,poses,buffers,light,overlay,texture,LFL,"LeftFrontLeg");transformed(mesh,poses,buffers,light,overlay,texture,RFL,"RightFrontLeg");transformed(mesh,poses,buffers,light,overlay,texture,LBL,"LeftBackLeg");transformed(mesh,poses,buffers,light,overlay,texture,RBL,"RightBackLeg");transformed(mesh,poses,buffers,light,overlay,texture,TAIL,"Tail");if(wings)part(mesh,poses,buffers,light,overlay,texture,"LeftWing","RightWing");poses.popPose();}
}
