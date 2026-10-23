package com.hbm.client.woodprocessing;
import com.hbm.client.model.*;
import com.hbm.machine.woodprocessing.AutosawEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact HFR noSmooth render chain; cumulative joint matrices and -.01 lower-arm offset retained. */
public final class AutosawRenderer implements BlockEntityRenderer<AutosawEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/autosaw.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/autosaw.png");
    public AutosawRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void part(String name,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.render(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,WavefrontMesh.legacyPart(name));}
    static void common(double turn,double angle,double spin,double engine,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        part("Base",pose,buffers,light,overlay);pose.mulPose(Axis.YN.rotationDegrees((float)turn));part("Main",pose,buffers,light,overlay);
        pose.pushPose();pose.translate(0,engine*.01,0);part("Engine",pose,buffers,light,overlay);pose.popPose();
        pose.translate(0,1.75,0);pose.mulPose(Axis.XP.rotationDegrees((float)angle));pose.translate(0,-1.75,0);part("ArmUpper",pose,buffers,light,overlay);
        pose.translate(0,1.75,-4);pose.mulPose(Axis.XP.rotationDegrees((float)(angle*-2)));pose.translate(0,-1.75,4);pose.translate(-.01,0,0);part("ArmLower",pose,buffers,light,overlay);pose.translate(.01,0,0);
        pose.translate(0,1.75,-8);pose.mulPose(Axis.XP.rotationDegrees((float)angle));pose.translate(0,-1.75,8);part("ArmTip",pose,buffers,light,overlay);
        pose.translate(0,1.75,-10);pose.mulPose(Axis.YN.rotationDegrees((float)spin));pose.translate(0,-1.75,10);part("Sawblade",pose,buffers,light,overlay);
    }
    @Override public void render(AutosawEntity saw,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){double turn=saw.prevRotationYaw+(saw.rotationYaw-saw.prevRotationYaw)*partial,angle=80-(saw.prevRotationPitch+(saw.rotationPitch-saw.prevRotationPitch)*partial),spin=saw.lastSpin+(saw.spin-saw.lastSpin)*partial,engine=saw.isOn?Math.sin(saw.getLevel().getGameTime()*2%(Math.PI*2)+partial):0;LegacyContactShadows.render(saw,model(),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);common(turn,angle,spin,engine,pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(AutosawEntity saw){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AutosawEntity saw){var p=saw.getBlockPos();return new AABB(p.getX()-12,p.getY(),p.getZ()-12,p.getX()+13,p.getY()+10,p.getZ()+13);}
}
