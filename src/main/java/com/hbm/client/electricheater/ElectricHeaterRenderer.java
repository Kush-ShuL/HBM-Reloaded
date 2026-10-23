package com.hbm.client.electricheater;
import com.hbm.client.model.*;
import com.hbm.machine.electricheater.ElectricHeaterBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderElectricHeater HFR-flat asVBO geometry and source rotation matrix. */
public final class ElectricHeaterRenderer implements BlockEntityRenderer<ElectricHeaterBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/electric_heater.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/electric_heater.png");
 public ElectricHeaterRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void model(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT);}
 @Override public void render(ElectricHeaterBlockEntity heater,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int angle=switch(DummyableBlock.facing(heater.getBlockState())){case SOUTH->270;case EAST->0;case NORTH->90;case WEST->180;default->0;};LegacyContactShadows.render(heater,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));model(poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(ElectricHeaterBlockEntity heater){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ElectricHeaterBlockEntity heater){var pos=heater.getBlockPos();return new AABB(pos.getX()-2,pos.getY(),pos.getZ()-2,pos.getX()+3,pos.getY()+1,pos.getZ()+3);}
}
