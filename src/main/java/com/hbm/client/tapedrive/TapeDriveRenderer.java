package com.hbm.client.tapedrive;

import com.hbm.client.model.*;
import com.hbm.machine.tapedrive.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;

/** Original Frame + twelve independently occupied Drive/Light parts; no proxy cube model. */
public final class TapeDriveRenderer implements BlockEntityRenderer<TapeDriveEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/tape_drive.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/tape_drive.png");
    public TapeDriveRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void frame(PoseStack pose,MultiBufferSource buffers,int light,int overlay){part("Frame",pose,buffers,light,overlay);}
    private static void part(String part,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT,part);}
    @Override public void render(TapeDriveEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        float yaw=switch(machine.getBlockState().getValue(TapeDriveBlock.META)){case 3->270;case 2->90;case 4->180;default->0;};
        LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));frame(pose,buffers,light,overlay);
        for(int i=0;i<12;i++)if(machine.tapes[i]!=TapeDriveEntity.SLOT_EMPTY){pose.pushPose();pose.translate(0,.25-.5*(i/6),.3125-(i%6)*.125);part("Drive",pose,buffers,light,overlay);pose.popPose();}
        for(int i=0;i<12;i++){
            byte type=machine.tapes[i];if(type==TapeDriveEntity.SLOT_EMPTY)continue;
            int color=switch(type){case TapeDriveEntity.SLOT_ANY->0xffff0000;case TapeDriveEntity.SLOT_EMPTY_TAPE->0xffffbf00;case TapeDriveEntity.SLOT_FILLED_TAPE->0xff00ff00;default->-1;};
            pose.pushPose();pose.translate(0,.25-.5*(i/6),.3125-(i%6)*.125);
            LegacyObjRenderer.renderOnly(model(),pose,TapeDriveRenderTypes.colorOnly(buffers.getBuffer(TapeDriveRenderTypes.LIGHT)),LightTexture.FULL_BRIGHT,overlay,color,LegacyObjRenderer.Convention.HFR_FLAT,"Light");pose.popPose();
        }
        pose.popPose();
    }
}
