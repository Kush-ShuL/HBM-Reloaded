package com.hbm.client.generator.gasturbine;

import com.hbm.client.model.*;
import com.hbm.machine.generator.gasturbine.GasTurbineBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original HFR full turbinegas.obj with source culling disabled and full authored bounds. */
public final class GasTurbineRenderer implements BlockEntityRenderer<GasTurbineBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/turbinegas.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/turbinegas.png");
    public GasTurbineRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void mesh(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    static void item(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.scale(.75F,.75F,.75F);pose.mulPose(Axis.YP.rotationDegrees(90));mesh(pose,buffers,light,overlay);}
    @Override public void render(GasTurbineBlockEntity engine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(engine.getBlockState())){case SOUTH->270;case NORTH->90;case WEST->180;default->0;};LegacyContactShadows.render(engine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));mesh(pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(GasTurbineBlockEntity engine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(GasTurbineBlockEntity engine){var pos=engine.getBlockPos();return new AABB(pos.getX()-5,pos.getY(),pos.getZ()-5,pos.getX()+6,pos.getY()+3,pos.getZ()+6);}
}
