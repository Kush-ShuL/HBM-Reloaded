package com.hbm.machine.ashpit.client;
import com.hbm.client.model.*;
import com.hbm.machine.ashpit.AshpitBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderAshpit reuses heating_oven.obj with its own texture and translates the drawer. */
public final class AshpitRenderer implements BlockEntityRenderer<AshpitBlockEntity> {
 static final ResourceLocation MODEL=ResourceLocation.fromNamespaceAndPath("hbm","models/machines/heating_oven.obj"),TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/models/machines/ashpit.png");
 public AshpitRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void parts(PoseStack poses,MultiBufferSource buffers,int light,int overlay,String... names){LegacyObjRenderer.renderOnly(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR_FLAT,names);}
 @Override public void render(AshpitBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){int angle=switch(DummyableBlock.facing(machine.getBlockState())){case SOUTH->-90;case EAST->0;case NORTH->90;case WEST->180;default->0;};LegacyContactShadows.render(machine,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(angle)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(angle));parts(poses,buffers,light,overlay,"Main");poses.pushPose();float door=machine.previousDoorAngle+(machine.doorAngle-machine.previousDoorAngle)*partial;poses.translate(0,0,door*.75/135);parts(poses,buffers,light,overlay,"Door");poses.popPose();parts(poses,buffers,light,overlay,machine.isFull?"InnerBurning":"Inner");poses.popPose();}
 @Override public boolean shouldRenderOffScreen(AshpitBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AshpitBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+1,p.getZ()+2);}
}
