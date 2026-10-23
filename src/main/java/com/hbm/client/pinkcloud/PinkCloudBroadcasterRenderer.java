package com.hbm.client.pinkcloud;

import com.hbm.client.model.legacy.generated.ModelBroadcaster;
import com.hbm.machine.pinkcloud.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Original RenderDecoBlock matrix and generated, unchanged Techne cuboids/UVs. */
public final class PinkCloudBroadcasterRenderer implements BlockEntityRenderer<PinkCloudBroadcasterBlockEntity> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/modelbroadcaster.png");
    private final ModelBroadcaster model=new ModelBroadcaster();
    public PinkCloudBroadcasterRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(PinkCloudBroadcasterBlockEntity entity,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        poses.pushPose();poses.translate(.5,1.5,.5);poses.mulPose(Axis.ZP.rotationDegrees(180));
        int meta=entity.getBlockState().getValue(PinkCloudBroadcasterBlock.META);
        poses.mulPose(Axis.YP.rotationDegrees(switch(meta){case 4->90;case 2->180;case 5->270;default->0;}));
        model.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,0xffffffff,.0625F);poses.popPose();
    }
    @Override public AABB getRenderBoundingBox(PinkCloudBroadcasterBlockEntity entity){var pos=entity.getBlockPos();return new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+2,pos.getZ()+1);}
    @Override public boolean shouldRenderOffScreen(PinkCloudBroadcasterBlockEntity entity){return true;}
    @Override public int getViewDistance(){return 256;}
}
