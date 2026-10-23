package com.hbm.client.annihilator;
import com.hbm.client.model.*;
import com.hbm.machine.annihilator.AnnihilatorEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original smooth model/clock-driven roller and repeat-U belt texture transform, including idle animation. */
public final class AnnihilatorRenderer implements BlockEntityRenderer<AnnihilatorEntity> {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/annihilator.png"),BELT=ResourceLocation.parse("hbm:textures/models/machines/annihilator_belt.png");
 public AnnihilatorRenderer(BlockEntityRendererProvider.Context context){}
 static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/machines/annihilator.obj"));}
 private static void part(String group,PoseStack poses,MultiBufferSource buffers,int light,int overlay,float u){var texture=group.equals("Belt")?BELT:TEXTURE;var vc=buffers.getBuffer(RenderType.entityCutout(texture));if(u!=0)vc=new Scroll(vc,u);LegacyObjRenderer.render(model(),poses,vc,light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR,WavefrontMesh.legacyPart(group));}
 static void inventory(PoseStack poses,MultiBufferSource buffers,int light,int overlay){part("Annihilator",poses,buffers,light,overlay,0);part("Roller",poses,buffers,light,overlay,0);part("Belt",poses,buffers,light,overlay,0);}
 @Override public void render(AnnihilatorEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(machine.getBlockState())){case WEST->90;case SOUTH->180;case EAST->270;default->0;};var transform=new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(90+yaw));LegacyContactShadows.render(machine,model(),transform,poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(90));poses.mulPose(Axis.YP.rotationDegrees(yaw));part("Annihilator",poses,buffers,light,overlay,0);poses.pushPose();poses.translate(0,1.75,0);poses.mulPose(Axis.ZN.rotationDegrees((float)(System.currentTimeMillis()*.15%360)));poses.translate(0,-1.75,0);part("Roller",poses,buffers,light,overlay,0);poses.popPose();part("Belt",poses,buffers,light,overlay,(float)(-System.currentTimeMillis()/3000D%1D));poses.popPose();}
 private record Scroll(VertexConsumer delegate,float u)implements VertexConsumer {public VertexConsumer addVertex(float x,float y,float z){delegate.addVertex(x,y,z);return this;}public VertexConsumer setColor(int r,int g,int b,int a){delegate.setColor(r,g,b,a);return this;}public VertexConsumer setUv(float x,float y){delegate.setUv(x+u,y);return this;}public VertexConsumer setUv1(int x,int y){delegate.setUv1(x,y);return this;}public VertexConsumer setUv2(int x,int y){delegate.setUv2(x,y);return this;}public VertexConsumer setNormal(float x,float y,float z){delegate.setNormal(x,y,z);return this;}}
 @Override public boolean shouldRenderOffScreen(AnnihilatorEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(AnnihilatorEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-5,p.getY(),p.getZ()-5,p.getX()+6,p.getY()+8,p.getZ()+6);}
}
