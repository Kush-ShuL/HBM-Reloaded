package com.hbm.client.soldering;
import com.hbm.client.model.*;import com.hbm.machine.soldering.*;import com.hbm.multiblock.DummyableBlock;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;import net.minecraft.client.Minecraft;import net.minecraft.client.renderer.*;import net.minecraft.client.renderer.blockentity.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemDisplayContext;import net.minecraft.world.phys.AABB;
/** Original HFR.noSmooth mesh, exact four source yaws and stationary single output display. */
public final class SolderingRenderer implements BlockEntityRenderer<SolderingBlockEntity> {
 static final ResourceLocation MODEL=SolderingContent.id("models/machines/soldering_station.obj"),TEXTURE=SolderingContent.id("textures/models/machines/soldering_station.png");
 public SolderingRenderer(BlockEntityRendererProvider.Context context){}
 static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR_FLAT);}
 private static int yaw(SolderingBlockEntity machine){return switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(SolderingBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int yaw=yaw(machine);LegacyContactShadows.render(machine,LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(-.5F,0,.5F),poses,buffers);
  poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));poses.translate(-.5,0,.5);model(poses,buffers,light,overlay);
  if(!machine.display.isEmpty()){poses.pushPose();poses.translate(.0625*2.5,1.125,0);poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.XP.rotationDegrees(-90));poses.scale(1.5F,1.5F,1.5F);Minecraft.getInstance().getItemRenderer().renderStatic(machine.display.copyWithCount(1),ItemDisplayContext.FIXED,light,overlay,poses,buffers,machine.getLevel(),0);poses.popPose();}poses.popPose();
 }
 @Override public boolean shouldRenderOffScreen(SolderingBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SolderingBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+3,p.getZ()+2);}
}
