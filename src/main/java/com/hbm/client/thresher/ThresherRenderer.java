package com.hbm.client.thresher;
import com.hbm.client.model.*;
import com.hbm.machine.thresher.ThresherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original smooth HFR/VBO geometry, cumulative82.5deg arm chain and exact source offsets. */
public final class ThresherRenderer implements BlockEntityRenderer<ThresherEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/thresher.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/thresher.png");
    public ThresherRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(String name,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,name);}
    static void common(double angle,double spin,double engine,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        part("Base",poses,buffers,light,overlay);
        poses.pushPose();poses.translate(0,engine*.01,0);part("Engine",poses,buffers,light,overlay);poses.popPose();
        poses.translate(0,.5,-1);poses.mulPose(Axis.XP.rotationDegrees((float)angle));poses.translate(0,-.5,1);part("ArmUpper",poses,buffers,light,overlay);
        poses.translate(0,.5,-5);poses.mulPose(Axis.XP.rotationDegrees((float)(angle*-2)));poses.translate(0,-.5,5);poses.translate(-.01,0,0);part("ArmLower",poses,buffers,light,overlay);poses.translate(.01,0,0);
        poses.translate(0,.5,-9);poses.mulPose(Axis.XP.rotationDegrees((float)angle));poses.translate(0,-.5,9);poses.translate(.01,0,0);part("Front",poses,buffers,light,overlay);
        poses.translate(0,.5,-11);poses.mulPose(Axis.XP.rotationDegrees((float)-spin));poses.translate(0,-.5,11);part("Wheel",poses,buffers,light,overlay);
    }
    @Override public void render(ThresherEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int metadata=machine.getBlockState().getValue(com.hbm.machine.thresher.ThresherBlock.META);float yaw=switch(metadata){case 3->180;case 5->270;case 4->90;default->0;};
        double angle=machine.prevAngle+(machine.angle-machine.prevAngle)*partial,spin=machine.lastSpin+(machine.spin-machine.lastSpin)*partial;
        double engine=machine.isOn?Math.sin(machine.getLevel().getGameTime()*2%(Math.PI*2)+partial):0;
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));common(82.5-angle,spin,engine,poses,buffers,light,overlay);poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(ThresherEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(ThresherEntity machine){var pos=machine.getBlockPos();return new AABB(pos.getX()-10,pos.getY(),pos.getZ()-10,pos.getX()+11,pos.getY()+7,pos.getZ()+11);}
}
