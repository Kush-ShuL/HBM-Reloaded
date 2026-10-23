package com.hbm.client.fan;

import com.hbm.client.model.*;
import com.hbm.machine.fan.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/** RenderFan's original Forge OBJ, flat face normals/UV inset and independent Frame/Blades matrices. */
public final class FanRenderer implements BlockEntityRenderer<FanEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/fan.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/fan.png");
    public FanRenderer(BlockEntityRendererProvider.Context context) {}
    static WavefrontMesh model() { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL); }
    static void all(PoseStack poses,MultiBufferSource buffers,int light,int overlay) { LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE); }
    @Override public void render(FanEntity fan,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        int meta=fan.getBlockState().getValue(FanBlock.META);
        var transform=new Matrix4f().translation(.5F,.5F,.5F);
        switch(meta) { case 0->transform.rotateX((float)Math.PI);case 2->transform.rotateX((float)-Math.PI/2);case 3->transform.rotateX((float)Math.PI/2);case 4->transform.rotateZ((float)Math.PI/2);case 5->transform.rotateZ((float)-Math.PI/2); }
        transform.translate(0,-.5F,0);
        LegacyContactShadows.render(fan,model(),transform,poses,buffers);
        poses.pushPose();poses.translate(.5,.5,.5);
        switch(meta) { case 0->poses.mulPose(Axis.XP.rotationDegrees(180));case 2->poses.mulPose(Axis.XP.rotationDegrees(-90));case 3->poses.mulPose(Axis.XP.rotationDegrees(90));case 4->poses.mulPose(Axis.ZP.rotationDegrees(90));case 5->poses.mulPose(Axis.ZP.rotationDegrees(-90)); }
        poses.translate(0,-.5,0);
        var output=buffers.getBuffer(RenderType.entityCutout(TEXTURE));
        LegacyObjRenderer.renderOnly(model(),poses,output,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"Frame");
        poses.mulPose(Axis.YP.rotationDegrees(-(fan.prevSpin+(fan.spin-fan.prevSpin)*partial)));
        LegacyObjRenderer.renderOnly(model(),poses,output,light,overlay,-1,LegacyObjRenderer.Convention.FORGE,"Blades");
        poses.popPose();
    }
    @Override public int getViewDistance() { return 256; }
}
