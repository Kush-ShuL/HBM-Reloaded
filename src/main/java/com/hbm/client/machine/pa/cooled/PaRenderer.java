package com.hbm.client.machine.pa.cooled;
import com.hbm.client.model.*;
import com.hbm.machine.pa.cooled.*;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Five original HFR VBO models, smooth normals and source rotations; dipole is deliberately unrotated. */
public final class PaRenderer implements BlockEntityRenderer<PaCooledEntity>{
 public PaRenderer(BlockEntityRendererProvider.Context context){}static String name(PaKind kind){return kind.id.substring(3);}static ResourceLocation modelId(PaKind kind){return ResourceLocation.parse("hbm:models/particleaccelerator/"+name(kind)+".obj");}static ResourceLocation texture(PaKind kind){return ResourceLocation.parse("hbm:textures/models/particleaccelerator/"+name(kind)+".png");}static WavefrontMesh model(PaKind kind){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),modelId(kind));}static void draw(PaKind kind,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(kind),poses,buffers.getBuffer(RenderType.entityCutout(texture(kind))),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(PaCooledEntity be,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=be.kind==PaKind.DIPOLE?0:switch(DummyableBlock.facing(be.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,-be.kind.height,.5F).rotateY((float)Math.toRadians(yaw));LegacyContactShadows.render(be,model(be.kind),transform,poses,buffers);poses.pushPose();poses.translate(.5,-be.kind.height,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));draw(be.kind,poses,buffers,light,overlay);poses.popPose();}
 @Override public boolean shouldRenderOffScreen(PaCooledEntity be){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(PaCooledEntity be){var p=be.getBlockPos();int extent=be.kind==PaKind.QUADRUPOLE||be.kind==PaKind.DIPOLE?1:4;return new AABB(p.getX()-extent,p.getY()-be.kind.height,p.getZ()-extent,p.getX()+extent+1,p.getY()+be.kind.height+1,p.getZ()+extent+(be.kind==PaKind.SOURCE?2:1));}
}
