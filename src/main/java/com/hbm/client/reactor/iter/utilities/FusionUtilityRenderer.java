package com.hbm.client.reactor.iter.utilities;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.iter.utilities.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original full boiler mesh and creative Klystron/Rotor groups, exact authored world transforms and full model bounds. */
public final class FusionUtilityRenderer<T extends FusionUtilityEntity> implements BlockEntityRenderer<T> {
 private final boolean creative;
 public FusionUtilityRenderer(BlockEntityRendererProvider.Context context,boolean creative){this.creative=creative;}
 static WavefrontMesh mesh(boolean creative){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/fusion/"+(creative?"klystron":"boiler")+".obj"));}
 static void model(boolean creative,float fan,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var texture=ResourceLocation.parse("hbm:textures/models/fusion/"+(creative?"klystron_creative":"boiler")+".png");var out=buffers.getBuffer(RenderType.entityCutout(texture));if(!creative){LegacyObjRenderer.renderAll(mesh(false),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR);return;}LegacyObjRenderer.renderOnly(mesh(true),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Klystron");pose.pushPose();pose.translate(0,2.5,0);pose.mulPose(Axis.XP.rotationDegrees(fan));pose.translate(0,-2.5,0);LegacyObjRenderer.renderOnly(mesh(true),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Rotor");pose.popPose();}
 @Override public void render(T tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var matrix=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));if(creative)matrix.translate(-1,0,0);LegacyContactShadows.render(tile,mesh(creative),matrix,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));if(creative)pose.translate(-1,0,0);float fan=tile instanceof CreativeKlystronEntity klystron?klystron.prevFan+(klystron.fan-klystron.prevFan)*partial:0;model(creative,fan,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(T tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(T tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-4,pos.getY(),pos.getZ()-4,pos.getX()+5,pos.getY()+(creative?5:4),pos.getZ()+5);}
}
