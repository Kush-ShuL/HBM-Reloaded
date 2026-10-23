package com.hbm.client.drain;
import com.hbm.client.model.*;
import com.hbm.machine.drain.DrainEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
public final class DrainRenderer implements BlockEntityRenderer<DrainEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/drain.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/drain.png");
 public DrainRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void model(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);}
 private static float angle(DrainEntity drain){return switch(DummyableBlock.facing(drain.getBlockState())){case WEST->180;case SOUTH->270;case NORTH->90;default->0;};}
 @Override public void render(DrainEntity drain,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float degrees=angle(drain);LegacyContactShadows.render(drain,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(degrees)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(degrees));model(pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(DrainEntity drain){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(DrainEntity drain){var p=drain.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+1,p.getZ()+3);}
}
