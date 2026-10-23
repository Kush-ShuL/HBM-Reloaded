package com.hbm.client.crystallizer;
import com.hbm.client.model.*;
import com.hbm.machine.crystallizer.CrystallizerBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Authored HFR acidizer Body/Spinner/Fluid groups and exact original metadata yaw/pivots. */
public final class CrystallizerRenderer implements BlockEntityRenderer<CrystallizerBlockEntity> {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/acidizer.png");
 public CrystallizerRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/acidizer.obj"));}
 static void part(String group,PoseStack poses,MultiBufferSource buffers,int light,int overlay,RenderType type){LegacyObjRenderer.render(model(),poses,buffers.getBuffer(type),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,face->face.legacyPart().equals(group));}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){part("Body",poses,buffers,light,overlay,RenderType.entityCutout(TEXTURE));part("Spinner",poses,buffers,light,overlay,RenderType.entityCutout(TEXTURE));}
 @Override public void render(CrystallizerBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(machine,model(),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));part("Body",poses,buffers,light,overlay,RenderType.entityCutout(TEXTURE));poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(machine.prevAngle+(machine.angle-machine.prevAngle)*partial));part("Spinner",poses,buffers,light,overlay,RenderType.entityCutout(TEXTURE));poses.popPose();if(machine.prevAngle!=machine.angle)part("Fluid",poses,buffers,light,overlay,CrystallizerRenderTypes.fluid(machine.tank.getTankType().getTexture()));poses.popPose();}
 @Override public boolean shouldRenderOffScreen(CrystallizerBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(CrystallizerBlockEntity m){var p=m.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+10,p.getZ()+2);}
}
