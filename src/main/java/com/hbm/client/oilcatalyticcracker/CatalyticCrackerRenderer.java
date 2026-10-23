package com.hbm.client.oilcatalyticcracker;
import com.hbm.client.model.*;
import com.hbm.machine.oilcatalyticcracker.CatalyticCrackerEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Source HFRWavefrontObject/asVBO with original orientation, full bounds and real contact-only mesh shadow. */
public final class CatalyticCrackerRenderer implements BlockEntityRenderer<CatalyticCrackerEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/catalytic_cracker.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/catalytic_cracker.png");
 public CatalyticCrackerRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static int yaw(Direction direction){return switch(direction){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 static void draw(boolean world,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(world?RenderType.entityCutoutNoCull(TEXTURE):RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(CatalyticCrackerEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int yaw=yaw(DummyableBlock.facing(machine.getBlockState()));LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));draw(true,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(CatalyticCrackerEntity entity){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(CatalyticCrackerEntity entity){var p=entity.getBlockPos();return new AABB(p.getX()-3,p.getY(),p.getZ()-3,p.getX()+4,p.getY()+16,p.getZ()+4);}
}
