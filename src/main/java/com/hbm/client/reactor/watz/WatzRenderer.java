package com.hbm.client.reactor.watz;

import com.hbm.client.model.*;
import com.hbm.machine.reactor.watz.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Actual source full Watz HFR mesh at(.5,0,.5), no invented rotation or proxy box shadow. */
public final class WatzRenderer implements BlockEntityRenderer<WatzEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/reactors/watz.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/watz.png");
    public WatzRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(WatzEntity watz,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(watz,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);draw(pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(WatzEntity watz){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(WatzEntity watz){var pos=watz.getBlockPos();return new AABB(pos.getX()-3,pos.getY(),pos.getZ()-3,pos.getX()+4,pos.getY()+3,pos.getZ()+4);}
}
