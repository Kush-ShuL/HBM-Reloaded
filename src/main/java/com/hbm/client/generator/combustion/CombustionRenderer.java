package com.hbm.client.generator.combustion;

import com.hbm.client.model.*;
import com.hbm.fluid.HbmFluidType;
import com.hbm.machine.generator.combustion.CombustionBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original Engine/Canister/Hatch HFR mesh and yaw/offset/negativeY hinge, without a proxy collision shadow. */
public final class CombustionRenderer implements BlockEntityRenderer<CombustionBlockEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/combustion_engine.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/combustion_engine.png");
    public CombustionRenderer(BlockEntityRendererProvider.Context context){}
    private static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    private static void part(PoseStack pose,MultiBufferSource buffers,int light,int overlay,int color,String name){LegacyObjRenderer.renderOnly(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,color,LegacyObjRenderer.Convention.HFR,name);}
    static void item(PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,0,2.75);LegacyObjRenderer.renderAll(model(),pose,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(CombustionBlockEntity engine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(engine.getBlockState())){case SOUTH->270;case NORTH->90;case WEST->180;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)).translate(-.5F,0,3);LegacyContactShadows.render(engine,model(),transform,pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));pose.translate(-.5,0,3);part(pose,buffers,light,overlay,0xffffffff,"Engine");int color=0xffffffff;var canister=engine.tank.getTankType().getContainer(HbmFluidType.Canister.class);if(canister!=null){int c=canister.color();color=0xff000000|((int)((c>>16&255)/256F*255)<<16)|((int)((c>>8&255)/256F*255)<<8)|(int)((c&255)/256F*255);}part(pose,buffers,light,overlay,color,"Canister");pose.translate(1,0,-2.6875);pose.mulPose(Axis.YN.rotationDegrees(engine.prevDoorAngle+(engine.doorAngle-engine.prevDoorAngle)*partial));pose.translate(-1,0,2.6875);part(pose,buffers,light,overlay,0xffffffff,"Hatch");pose.popPose();}
    @Override public boolean shouldRenderOffScreen(CombustionBlockEntity engine){return true;}@Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(CombustionBlockEntity engine){var pos=engine.getBlockPos();return new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+2,pos.getZ()+4);}
}
