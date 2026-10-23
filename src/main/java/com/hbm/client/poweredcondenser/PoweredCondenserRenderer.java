package com.hbm.client.poweredcondenser;
import com.hbm.client.model.*;
import com.hbm.machine.poweredcondenser.PoweredCondenserEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderCondenser: source yaw, smooth body, counter-rotating fans about the X axis at y1.5. */
public final class PoweredCondenserRenderer implements BlockEntityRenderer<PoweredCondenserEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/condenser.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/condenser.png");
 public PoweredCondenserRenderer(BlockEntityRendererProvider.Context context){}private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,String group){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,group);}
 static void inventory(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(.75F,.75F,.75F);pose.translate(.5,0,0);LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 private static int yaw(Direction direction){return switch(direction){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(PoweredCondenserEntity condenser,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int rotation=yaw(DummyableBlock.facing(condenser.getBlockState()));var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(rotation));LegacyContactShadows.render(condenser,model(),transform,pose,buffers);pose.pushPose();pose.mulPose(transform);part(pose,buffers,light,overlay,"Condenser");float spin=condenser.lastSpin+(condenser.spin-condenser.lastSpin)*partial;pose.pushPose();pose.translate(0,1.5,0);pose.mulPose(Axis.XP.rotationDegrees(spin));pose.translate(0,-1.5,0);part(pose,buffers,light,overlay,"Fan1");pose.popPose();pose.pushPose();pose.translate(0,1.5,0);pose.mulPose(Axis.XN.rotationDegrees(spin));pose.translate(0,-1.5,0);part(pose,buffers,light,overlay,"Fan2");pose.popPose();pose.popPose();}
 @Override public boolean shouldRenderOffScreen(PoweredCondenserEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PoweredCondenserEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-3,p.getY(),p.getZ()-3,p.getX()+4,p.getY()+3,p.getZ()+4);}
}
