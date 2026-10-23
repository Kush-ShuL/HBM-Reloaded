package com.hbm.client.reactor.iter.klystron;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.iter.klystron.KlystronEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Exact original Klystron/rotor mesh, HFRnormal+UV,world T(.5,0,.5) yaw/T(-1,0,0),rotorpivot(0,2.5,0). */
public final class KlystronRenderer implements BlockEntityRenderer<KlystronEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/fusion/klystron.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/fusion/klystron.png");
    public KlystronRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void part(String group,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,group);}
    @Override public void render(KlystronEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(machine,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(-1,0,0),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.translate(-1,0,0);part("Klystron",pose,buffers,light,overlay);pose.pushPose();pose.translate(0,2.5,0);pose.mulPose(Axis.XP.rotationDegrees(machine.prevFan+(machine.fan-machine.prevFan)*partial));pose.translate(0,-2.5,0);part("Rotor",pose,buffers,light,overlay);pose.popPose();pose.popPose();}
    @Override public boolean shouldRenderOffScreen(KlystronEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(KlystronEntity machine){var pos=machine.getBlockPos();return new AABB(pos.getX()-4,pos.getY(),pos.getZ()-4,pos.getX()+5,pos.getY()+5,pos.getZ()+5);}
}
