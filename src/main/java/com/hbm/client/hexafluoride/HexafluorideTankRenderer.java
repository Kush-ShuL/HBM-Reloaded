package com.hbm.client.hexafluoride;

import com.hbm.client.model.*;
import com.hbm.machine.hexafluoride.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original Forge Wavefront tank.obj; unchanged world T(.5,0,.5) and metadata Y headings. */
public final class HexafluorideTankRenderer implements BlockEntityRenderer<HexafluorideTankEntity> {
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/tank.obj");
    private static final ResourceLocation UF6=ResourceLocation.parse("hbm:textures/models/uf6tank.png"),PUF6=ResourceLocation.parse("hbm:textures/models/puf6tank.png");
    public HexafluorideTankRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);}
    static void all(boolean plutonium,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        LegacyObjRenderer.renderAll(mesh(),poses,buffers.getBuffer(RenderType.entityCutout(plutonium?PUF6:UF6)),light,overlay,0xffffffff,LegacyObjRenderer.Convention.FORGE);
    }
    @Override public void render(HexafluorideTankEntity tank,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        int meta=tank.getBlockState().getValue(HexafluorideTankBlock.META);float yaw=switch(meta){case 4->90;case 3->180;case 5->270;default->0;};
        LegacyContactShadows.render(tank,mesh(),new org.joml.Matrix4f().translation(.5F,0,.5F).rotateY((float)Math.toRadians(yaw)),poses,buffers);
        poses.pushPose();poses.translate(.5,0,.5);poses.mulPose(Axis.YP.rotationDegrees(yaw));
        all(((HexafluorideTankBlock)tank.getBlockState().getBlock()).plutonium,poses,buffers,light,overlay);poses.popPose();
    }
    @Override public boolean shouldRenderOffScreen(HexafluorideTankEntity tank){return true;}
    @Override public AABB getRenderBoundingBox(HexafluorideTankEntity tank){var p=tank.getBlockPos();return new AABB(p.getX()-.0125,p.getY(),p.getZ()-.0125,p.getX()+1.0125,p.getY()+2,p.getZ()+1.0125);}
}
