package com.hbm.client.arcwelder;
import com.hbm.client.model.*;import com.hbm.machine.arcwelder.*;import com.hbm.multiblock.DummyableBlock;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.blockentity.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemDisplayContext;import net.minecraft.world.phys.AABB;
/** Original HFR.noSmooth mesh, exact four source yaws and stationary single output display. */
public final class ArcWelderRenderer implements BlockEntityRenderer<ArcWelderBlockEntity> {
 static final ResourceLocation MODEL=ArcWelderContent.id("models/machines/arc_welder.obj"),TEXTURE=ArcWelderContent.id("textures/models/machines/arc_welder.png");
 public ArcWelderRenderer(BlockEntityRendererProvider.Context context){}
 static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT);}
 private static int yaw(ArcWelderBlockEntity machine){return switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(ArcWelderBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int yaw=yaw(machine);LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(-.5F,0,0),poses,buffers);
  poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.translate(-.5,0,0);model(poses,buffers,light,overlay);
  if(!machine.display.isEmpty()){poses.pushPose();poses.translate(.0625*2.5,1.125,0);poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.XP.rotationDegrees(-90));poses.scale(1.5F,1.5F,1.5F);Minecraft.getInstance().getItemRenderer().renderStatic(machine.display.copyWithCount(1),ItemDisplayContext.FIXED,light,overlay,poses,buffers,machine.getLevel(),0);poses.popPose();}poses.popPose();
 }
 @Override public boolean shouldRenderOffScreen(ArcWelderBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ArcWelderBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
