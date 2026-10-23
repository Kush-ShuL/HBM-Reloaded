package com.hbm.client.electrolyser;
import com.hbm.client.model.*;
import com.hbm.machine.electrolyser.ElectrolyserBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original HFR smooth OBJ, metadata yaw plus180; world unculling and separate original culled item path. */
public final class ElectrolyserRenderer implements BlockEntityRenderer<ElectrolyserBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/electrolyser.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/electrolyser.png");
 public ElectrolyserRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(ElectrolyserBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case WEST->90;case SOUTH->180;case EAST->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw+180));LegacyContactShadows.render(machine,model(),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw+180));LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(ElectrolyserBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ElectrolyserBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-5,p.getY(),p.getZ()-5,p.getX()+6,p.getY()+4,p.getZ()+6);}
}
