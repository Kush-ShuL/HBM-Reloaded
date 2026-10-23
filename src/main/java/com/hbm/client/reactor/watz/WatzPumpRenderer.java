package com.hbm.client.reactor.watz;

import com.hbm.client.model.*;
import com.hbm.machine.reactor.watz.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public final class WatzPumpRenderer implements BlockEntityRenderer<WatzPumpEntity> {
    static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/machines/watz_pump.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/machines/watz_pump.png");
    public WatzPumpRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void draw(PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyObjRenderer.renderAll(mesh(),pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.HFR);}
    @Override public void render(WatzPumpEntity pump,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){LegacyContactShadows.render(pump,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F),pose,buffers);pose.pushPose();pose.translate(.5,0,.5);draw(pose,buffers,light,overlay);pose.popPose();}
    @Override public boolean shouldRenderOffScreen(WatzPumpEntity pump){return true;}@Override public int getViewDistance(){return 256;}@Override public AABB getRenderBoundingBox(WatzPumpEntity pump){var pos=pump.getBlockPos();return new AABB(pos.getX()-1,pos.getY(),pos.getZ()-1,pos.getX()+2,pos.getY()+2,pos.getZ()+2);}
}
