package com.hbm.machine.chimney.client;
import com.hbm.client.model.*;
import com.hbm.machine.chimney.ChimneyBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original smooth HFR normals, no culling, source 180 degree world turn. */
public final class ChimneyRenderer implements BlockEntityRenderer<ChimneyBlockEntity> {
 public ChimneyRenderer(BlockEntityRendererProvider.Context context){}static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}static String name(boolean industrial){return industrial?"chimney_industrial":"chimney_brick";}static WavefrontMesh model(boolean industrial){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id("models/machines/"+name(industrial)+".obj"));}static void model(boolean industrial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(model(industrial),poses,buffers.getBuffer(RenderType.entityCutoutNoCull(id("textures/models/machines/"+name(industrial)+".png"))),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
 @Override public void render(ChimneyBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(machine,model(machine.industrial),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.PI),poses,buffers);poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(180));model(machine.industrial,poses,buffers,light,overlay);poses.popPose();}@Override public boolean shouldRenderOffScreen(ChimneyBlockEntity machine){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(ChimneyBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-1,p.getY(),p.getZ()-1,p.getX()+2,p.getY()+(machine.industrial?23:13),p.getZ()+2);}
}
