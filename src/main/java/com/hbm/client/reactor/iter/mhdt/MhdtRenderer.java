package com.hbm.client.reactor.iter.mhdt;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.iter.mhdt.MhdtEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Full original Turbine/Coils model, original X-pivot at1.5 and modulo15 visual rotor. */
public final class MhdtRenderer implements BlockEntityRenderer<MhdtEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/fusion/mhdt.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/mhdt.png");
 public MhdtRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void model(float rotor,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var out=buffers.getBuffer(RenderType.entityCutout(TEXTURE));LegacyObjRenderer.renderOnly(mesh(),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Turbine");pose.pushPose();pose.translate(0,1.5,0);pose.mulPose(Axis.XP.rotationDegrees(rotor));pose.translate(0,-1.5,0);LegacyObjRenderer.renderOnly(mesh(),pose,out,light,overlay,-1,LegacyObjRenderer.Convention.HFR,"Coils");pose.popPose();}
 @Override public void render(MhdtEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));model((tile.prevRotor+(tile.rotor-tile.prevRotor)*partial)%15,pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(MhdtEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(MhdtEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-7,pos.getY(),pos.getZ()-7,pos.getX()+8,pos.getY()+4,pos.getZ()+8);}
}
