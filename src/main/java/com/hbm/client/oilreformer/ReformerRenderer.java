package com.hbm.client.oilreformer;
import com.hbm.LegacyStartup;
import com.hbm.client.model.*;
import com.hbm.client.model.horse.LegacyHorsePronter;
import com.hbm.client.structureloot.LootPileRenderer;
import com.hbm.machine.oilreformer.ReformerEntity;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
/** Exact original world Yaw/complete HFR smooth mesh and the enabled polaroid11 horse+cigarette branch. */
public final class ReformerRenderer implements BlockEntityRenderer<ReformerEntity> {
 private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/catalytic_reformer.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/catalytic_reformer.png"),EXTRA=ResourceLocation.parse("hbm:textures/models/horse/dyx.png");private final LegacyHorsePronter horse=new LegacyHorsePronter();
 public ReformerRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 private static float yaw(Direction side){return switch(side){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};}
 @Override public void render(ReformerEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=yaw(DummyableBlock.facing(machine.getBlockState()));LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);if(LegacyStartup.polaroidId==11){poses.translate(-1.125,1.375,1);poses.scale(.125F,.125F,.125F);poses.mulPose(Axis.YP.rotationDegrees((float)-(System.currentTimeMillis()/5D%360D)));poses.translate(0,.1,-.5);horse.reset();horse.poseStandardSit();horse.setAlicorn();horse.render(poses,buffers,light,overlay,EXTRA);poses.translate(.02,1.13,-.42);poses.scale(.25F,.25F,.25F);poses.mulPose(Axis.YP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(-60));var mc=Minecraft.getInstance();var sprite=mc.getItemRenderer().getModel(new ItemStack(HbmContent.item("cigarette")),mc.level,mc.player,0).getParticleIcon();LootPileRenderer.extrude(sprite.getU1(),sprite.getU0(),sprite.getV0(),sprite.getV1(),sprite.contents().width(),sprite.contents().height(),.0625F,poses.last(),buffers.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS)),light,overlay,-1);}poses.popPose();}
 @Override public boolean shouldRenderOffScreen(ReformerEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ReformerEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+7,p.getZ()+3);}
}
