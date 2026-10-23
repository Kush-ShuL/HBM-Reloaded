package com.hbm.client.pneumatic;
import com.hbm.client.model.*;
import com.hbm.pneumatic.anchor.*;
import com.hbm.pneumatic.tube.PneumaticTubeContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.*;
/** Original Anchor/Pipe/Ring OBJ parts, dominant endpoint, exact rotations and physical ten-block pipe length. */
public final class PneumaticAnchorRenderer implements BlockEntityRenderer<PneumaticAnchorBlockEntity> {
 static final ResourceLocation MODEL=PneumaticTubeContent.id("models/network/pipe_anchor.obj"),TEXTURE=PneumaticTubeContent.id("textures/models/network/pipe_anchor_pneumatic.png");
 public PneumaticAnchorRenderer(BlockEntityRendererProvider.Context context){}
 static void part(String name,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,name);}
 @Override public void render(PneumaticAnchorBlockEntity anchor,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,.5,.5);poses.pushPose();switch(anchor.getBlockState().getValue(PneumaticAnchorBlock.FACING)){case DOWN->poses.mulPose(Axis.XP.rotationDegrees(180));case UP->{}case NORTH->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(180));}case SOUTH->poses.mulPose(Axis.XP.rotationDegrees(90));case WEST->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(90));}case EAST->{poses.mulPose(Axis.XP.rotationDegrees(90));poses.mulPose(Axis.ZP.rotationDegrees(270));}}poses.translate(0,-.5,0);part("Anchor",poses,buffers,light,overlay);poses.popPose();var origin=anchor.connectionPoint();for(var pos:anchor.getConnected())if(anchor.getLevel()!=null&&anchor.getLevel().getBlockEntity(pos) instanceof PneumaticAnchorBlockEntity other){var to=other.connectionPoint();if(!dominant(origin,to))continue;var delta=to.subtract(origin);double hyp=Math.sqrt(delta.x*delta.x+delta.z*delta.z),yaw=Math.toDegrees(Math.atan2(delta.x,delta.z)),pitch=Math.toDegrees(Math.atan2(delta.y,hyp)),length=delta.length();poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees((float)yaw));poses.mulPose(Axis.XP.rotationDegrees((float)(90-pitch)));poses.pushPose();poses.scale(1,(float)length,1);poses.translate(0,-.5,0);part("Pipe",poses,buffers,light,overlay);poses.popPose();poses.translate(0,length/2-1.5,0);part("Ring",poses,buffers,light,overlay);poses.popPose();}poses.popPose();}
 private static boolean dominant(Vec3 a,Vec3 b){return a.x<b.x||a.x==b.x&&(a.y<b.y||a.y==b.y&&a.z<b.z);}
 @Override public boolean shouldRenderOffScreen(PneumaticAnchorBlockEntity anchor){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PneumaticAnchorBlockEntity anchor){var box=new AABB(anchor.getBlockPos());for(var target:anchor.getConnected())box=box.minmax(new AABB(target));return box.inflate(.5);}
}
