package com.hbm.client.fluidcontrols;
import com.hbm.client.model.*;
import com.hbm.network.fluidcontrols.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact source HFR meshes, per-family yaw, rail visibility and rotor pivot; both automatic item providers retained. */
public final class PumpRenderer implements BlockEntityRenderer<PumpEntity> {
 static final ResourceLocation OLD=ResourceLocation.parse("hbm:models/network/fluid_diode.obj"),FLOW=ResourceLocation.parse("hbm:models/network/flow_control_pump.obj"),OLD_TEX=ResourceLocation.parse("hbm:textures/models/network/fluid_diode.png"),FLOW_TEX=ResourceLocation.parse("hbm:textures/models/network/flow_control_pump.png");
 public PumpRenderer(BlockEntityRendererProvider.Context context){}
 static void part(boolean old,String name,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),old?OLD:FLOW);var out=buffers.getBuffer(RenderType.entityCutout(old?OLD_TEX:FLOW_TEX));if(name==null)LegacyObjRenderer.renderAll(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR);else LegacyObjRenderer.renderOnly(mesh,poses,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,name);}
 static void inventory(boolean old,PoseStack poses,MultiBufferSource buffers,int light,int overlay){if(old){poses.scale(2,2,2);poses.mulPose(Axis.YP.rotationDegrees(90));part(true,null,poses,buffers,light,overlay);}else{part(false,"Pump",poses,buffers,light,overlay);part(false,"Rotor",poses,buffers,light,overlay);part(false,"RailBottom",poses,buffers,light,overlay);}}
 @Override public void render(PumpEntity pump,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int meta=pump.getBlockState().getValue(pump.legacy?FluidPumpBlock.META:DummyableBlock.LEGACY_META)-(pump.legacy?0:10);float yaw=pump.legacy?switch(meta){case 2->180;case 4->270;case 5->90;default->0;}:switch(meta){case 2->90;case 4->180;case 3->270;default->0;};poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));if(pump.legacy)part(true,null,poses,buffers,light,overlay);else{part(false,"Pump",poses,buffers,light,overlay);if(pump.topRail)part(false,"RailTop",poses,buffers,light,overlay);if(pump.bottomRail)part(false,"RailBottom",poses,buffers,light,overlay);poses.translate(0,.5,0);poses.mulPose(Axis.ZN.rotationDegrees(pump.prevRotor+(pump.rotor-pump.prevRotor)*partial));poses.translate(0,-.5,0);part(false,"Rotor",poses,buffers,light,overlay);}poses.popPose();}
 @Override public boolean shouldRenderOffScreen(PumpEntity pump){return !pump.legacy;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PumpEntity pump){var p=pump.getBlockPos();return pump.legacy?new AABB(p):new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+1,p.getZ()+2);}
}
