package com.hbm.client.heatingoven;

import com.hbm.client.model.*;
import com.hbm.machine.heatingoven.HeatingOvenBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original RenderHeatingOven HFR-flat mesh, translated drawer and fullbright lit inner. */
public final class HeatingOvenRenderer implements BlockEntityRenderer<HeatingOvenBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/heating_oven.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/heating_oven.png");
 public HeatingOvenRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void parts(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String... names){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,names);}
 @Override public void render(HeatingOvenBlockEntity oven,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int angle=switch(DummyableBlock.facing(oven.getBlockState())){case SOUTH->-90;case EAST->0;case NORTH->90;case WEST->180;default->0;};LegacyContactShadows.render(oven,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));parts(poses,buffers,light,overlay,"Main");poses.pushPose();float door=oven.previousDoorAngle+(oven.doorAngle-oven.previousDoorAngle)*partial;poses.translate(0,0,door*.75/135);parts(poses,buffers,light,overlay,"Door");poses.popPose();if(oven.wasOn)LegacyObjRenderer.renderOnly(model(),poses,HeatingOvenRenderTypes.unlitVertices(buffers.getBuffer(HeatingOvenRenderTypes.unlit(TEXTURE))),LightTexture.FULL_BRIGHT,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,"InnerBurning");else parts(poses,buffers,light,overlay,"Inner");poses.popPose();}
 @Override public boolean shouldRenderOffScreen(HeatingOvenBlockEntity oven){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(HeatingOvenBlockEntity oven){var pos=oven.getBlockPos();return new AABB(pos.getX()-1,pos.getY(),pos.getZ()-1,pos.getX()+2,pos.getY()+1,pos.getZ()+2);}
}
