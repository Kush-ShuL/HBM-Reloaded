package com.hbm.client.airintake;
import com.hbm.client.model.*;
import com.hbm.machine.airintake.AirIntakeBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR Base/Fan groups, exact metadata rotation and fan pivot; no custom geometry. */
public final class AirIntakeRenderer implements BlockEntityRenderer<AirIntakeBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/intake.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/intake.png");
    public AirIntakeRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void all(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    private static void part(String part,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.render(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,face->face.legacyPart().equals(part));}
    @Override public void render(AirIntakeBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(-.5F,0,.5F);LegacyContactShadows.render(machine,model(),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.translate(-.5,0,.5);part("Base",poses,buffers,light,overlay);poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(-(machine.prevFan+(machine.fan-machine.prevFan)*partial)));part("Fan",poses,buffers,light,overlay);poses.popPose();poses.popPose();}
    @Override public boolean shouldRenderOffScreen(AirIntakeBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AirIntakeBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+1,p.getZ()+2);}
}
