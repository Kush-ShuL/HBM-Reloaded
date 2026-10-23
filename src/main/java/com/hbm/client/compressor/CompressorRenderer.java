package com.hbm.client.compressor;
import com.hbm.client.model.*;
import com.hbm.machine.compressor.CompressorBlockEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Authored HFR smooth normals, real Pump/Fan pivots, no proxy collision shadows. */
public final class CompressorRenderer implements BlockEntityRenderer<CompressorBlockEntity> {
 public CompressorRenderer(BlockEntityRendererProvider.Context context){}
 static ResourceLocation texture(boolean compact){return ResourceLocation.parse("hbm:textures/models/machines/compressor"+(compact?"_compact":"")+".png");}
 static WavefrontMesh model(boolean compact){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/compressor"+(compact?"_compact":"")+".obj"));}
 static void part(boolean compact,String part,PoseStack poses,MultiBufferSource buffers,int light,int overlay){var consumer=buffers.getBuffer(compact?RenderType.entityCutout(texture(true)):RenderType.entityCutoutNoCull(texture(false)));LegacyObjRenderer.render(model(compact),poses,consumer,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,f->part==null||f.legacyPart().equals(part));}
 static void render(boolean compact,double lift,double fan,PoseStack poses,MultiBufferSource buffers,int light,int overlay){if(compact){part(true,null,poses,buffers,light,overlay);return;}part(false,"Compressor",poses,buffers,light,overlay);poses.pushPose();poses.translate(0,lift,0);part(false,"Pump",poses,buffers,light,overlay);poses.popPose();poses.pushPose();poses.translate(0,1.5,0);poses.mulPose(Axis.XP.rotationDegrees((float)fan));poses.translate(0,-1.5,0);part(false,"Fan",poses,buffers,light,overlay);poses.popPose();}
 @Override public void render(CompressorBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(machine,model(machine.compact()),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));render(machine.compact(),(machine.prevPiston+(machine.piston-machine.prevPiston)*partial)*3-3,machine.prevFanSpin+(machine.fanSpin-machine.prevFanSpin)*partial,poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(CompressorBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(CompressorBlockEntity m){var p=m.getBlockPos();return m.compact()?new AABB(p.getX()-3,p.getY(),p.getZ()-3,p.getX()+4,p.getY()+3,p.getZ()+4):new AABB(p.getX()-2,p.getY(),p.getZ()-2,p.getX()+3,p.getY()+9,p.getZ()+3);}
}
