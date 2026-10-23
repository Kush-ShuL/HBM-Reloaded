package com.hbm.client.reactor.icf;
import com.hbm.client.model.*;
import com.hbm.machine.reactor.icf.IcfReactorEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
public final class IcfRenderer implements BlockEntityRenderer<IcfReactorEntity>{
 static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/reactors/icf.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/icf.png");public IcfRenderer(BlockEntityRendererProvider.Context context){}static WavefrontMesh model(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,overlay,-1,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(IcfReactorEntity reactor,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){float yaw=switch(DummyableBlock.facing(reactor.getBlockState())){case NORTH->90;case WEST->180;case SOUTH->270;default->0;};LegacyContactShadows.render(reactor,model(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));draw(poses,buffers,light,overlay);poses.popPose();}@Override public boolean shouldRenderOffScreen(IcfReactorEntity reactor){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(IcfReactorEntity reactor){var p=reactor.getBlockPos();return new AABB(p.getX()-7.5,p.getY(),p.getZ()-7.5,p.getX()+9.5,p.getY()+5.5,p.getZ()+9.5);}
}
