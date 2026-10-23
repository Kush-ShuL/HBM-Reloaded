package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original LilBoy1 OBJ with CustomNuke texture, source no-cull world metadata transforms. */
public final class CustomNuclearRenderer implements BlockEntityRenderer<CustomNuclearBlockEntity> {
 public static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/customnuke.png"),MODEL=ResourceLocation.parse("hbm:models/lilboy1.obj");
 public CustomNuclearRenderer(BlockEntityRendererProvider.Context context){}
 public static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay){draw(poses,buffers,light,overlay,false);}
 public static void draw(PoseStack poses,MultiBufferSource buffers,int light,int overlay,boolean cull){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderAll(mesh,poses,buffers.getBuffer(cull?RenderType.entityCutout(TEXTURE):RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);}
 public static void sourceOrientation(PoseStack pose,int metadata){if(metadata<2||metadata>5)return;pose.mulPose(Axis.YP.rotationDegrees(switch(metadata){case 2->90;case 4->180;case 3->270;default->0;}));pose.translate(-2,0,0);}
 @Override public void render(CustomNuclearBlockEntity bomb,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){pose.pushPose();pose.translate(.5,0,.5);sourceOrientation(pose,bomb.getBlockState().getValue(ProgrammableBombBlock.META));draw(pose,buffers,light,overlay);pose.popPose();}
 @Override public boolean shouldRenderOffScreen(CustomNuclearBlockEntity bomb){return true;}
 @Override public int getViewDistance(){return 256;}
 @Override public AABB getRenderBoundingBox(CustomNuclearBlockEntity bomb){return AABB.INFINITE;}
}
