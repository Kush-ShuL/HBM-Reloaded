package com.hbm.client.supercomputer;
import com.hbm.client.fensu.FensuRenderTypes;
import com.hbm.client.model.*;
import com.hbm.machine.supercomputer.SuperComputerEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** OriginalComputer/Lights HFR meshes; true20tickscanUV andblackinactivefullbrightlights. */
public final class SuperComputerRenderer implements BlockEntityRenderer<SuperComputerEntity> {
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/supercomputer.obj"),BODY=ResourceLocation.parse("hbm:textures/models/machines/supercomputer.png"),SCAN=ResourceLocation.parse("hbm:textures/models/machines/supercomputer_scan.png");
 public SuperComputerRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
 static void part(String group,ResourceLocation texture,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderOnly(mesh(),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.HFR,group);}
 @Override public void render(SuperComputerEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(tile.getBlockState())){case NORTH->180;case WEST->270;case EAST->90;default->0;};LegacyContactShadows.render(tile,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(yaw));part("Computer",BODY,pose,buffers,light,overlay);float scroll=(tile.getLevel().getGameTime()%20+partial)/20;var out=FensuRenderTypes.unlitVertices(buffers.getBuffer(FensuRenderTypes.unlit(SCAN)),-scroll,0);LegacyObjRenderer.renderOnly(mesh(),pose,out,LightTexture.FULL_BRIGHT,overlay,tile.didProcess?-1:0xff000000,LegacyObjRenderer.Convention.HFR,"Lights");pose.popPose();}
 @Override public boolean shouldRenderOffScreen(SuperComputerEntity tile){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(SuperComputerEntity tile){var pos=tile.getBlockPos();return new AABB(pos.getX()-8,pos.getY(),pos.getZ()-8,pos.getX()+9,pos.getY()+9,pos.getZ()+9);}
}
