package com.hbm.client.oilmixer;
import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluids;
import com.hbm.machine.oilmixer.MixerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Actual HFR Main/Mixer/Fluid parts, no invented facing rotation or liquid texture. */
public final class MixerRenderer implements BlockEntityRenderer<MixerEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/mixer.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/mixer.png");
 public MixerRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay,float rotor){var mesh=model();var vertices=buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));LegacyObjRenderer.renderOnly(mesh,pose,vertices,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Main");pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(-rotor));LegacyObjRenderer.renderOnly(mesh,pose,vertices,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Mixer");pose.popPose();}
 @Override public void render(MixerEntity mixer,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var mesh=model();LegacyContactShadows.render(mixer,mesh,new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);draw(pose,buffers,light,overlay,mixer.prevRotation+(mixer.rotation-mixer.prevRotation)*partial);int fill=0,max=0;for(var tank:mixer.tanks)if(tank.getTankType()!=HbmFluids.NONE){fill+=tank.getFill();max+=tank.getMaxFill();}if(fill>0&&max>0){if(buffers instanceof MultiBufferSource.BufferSource source)source.endBatch(RenderType.entityCutoutNoCull(TEXTURE));pose.pushPose();pose.translate(0,1,0);pose.scale(1,(float)((double)fill/max*.99),1);pose.translate(0,-1,0);LegacyObjRenderer.renderOnly(mesh,pose,buffers.getBuffer(MixerRenderTypes.FLUID),light,overlay,0xbf000000|mixer.tanks[2].getTankType().getColor()&0xffffff,LegacyObjRenderer.Convention.HFR,"Fluid");pose.popPose();}pose.popPose();}
 @Override public boolean shouldRenderOffScreen(MixerEntity e){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(MixerEntity e){var p=e.getBlockPos();return new AABB(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+3,p.getZ()+1);}
}
