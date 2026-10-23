package com.hbm.client.electricpress;

import com.hbm.client.model.*;
import com.hbm.machine.electricpress.ElectricPressEntity;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.AABB;

/** Source AdvancedModelLoader geometry,body/head/RenderDecoItem matrices and 2tick interpolated integer piston. */
public final class ElectricPressRenderer implements BlockEntityRenderer<ElectricPressEntity> {
    static final ResourceLocation BODY=id("models/epress_body.obj"),HEAD=id("models/epress_head.obj"),BODY_TEX=id("textures/models/epress_body.png"),HEAD_TEX=id("textures/models/epress_head.png");
    static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public ElectricPressRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(ResourceLocation id){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),id);}
    static void draw(ResourceLocation model,ResourceLocation texture,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(model),pose,buffers.getBuffer(RenderType.entityCutout(texture)),light,overlay,-1,LegacyObjRenderer.Convention.FORGE);}
    static float yaw(int metadata){return switch(metadata-10){case 2->90;case 4->180;case 3->270;default->0;};}
    @Override public void render(ElectricPressEntity machine,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){float rotation=yaw(machine.getBlockState().getValue(DummyableBlock.LEGACY_META));LegacyContactShadows.render(machine,mesh(BODY),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(rotation)),pose,buffers);
        pose.pushPose();pose.translate(.5,0,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));draw(BODY,BODY_TEX,pose,buffers,light,overlay);pose.popPose();
        pose.pushPose();pose.translate(.5,1,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.translate(0,Mth.clamp(1-machine.renderedPress(partial)/200D,0,1)*.875,0);draw(HEAD,HEAD_TEX,pose,buffers,light,overlay);pose.popPose();
        if(!machine.renderedInput.isEmpty()){pose.pushPose();pose.translate(.5,1,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.mulPose(Axis.YP.rotationDegrees(90));pose.mulPose(Axis.XP.rotationDegrees(-90));pose.translate(1,1-.0625F*165/100,0);pose.translate(-1,-1.15F,0);pose.scale(.5F,.5F,.5F);Minecraft.getInstance().getItemRenderer().renderStatic(machine.renderedInput.copyWithCount(1),ItemDisplayContext.FIXED,light,overlay,pose,buffers,machine.getLevel(),0);pose.popPose();}
    }
    @Override public boolean shouldRenderOffScreen(ElectricPressEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(ElectricPressEntity machine){var p=machine.getBlockPos();return new AABB(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+3,p.getZ()+1);}
}
