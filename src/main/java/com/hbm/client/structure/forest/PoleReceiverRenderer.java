package com.hbm.client.structure.forest;
import com.hbm.client.model.legacy.generated.ModelSatelliteReceiver;
import com.hbm.world.structure.forest.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
/** Original RenderPoleSatelliteReceiver Java boxes, UVs and exact TESR transforms. */
public final class PoleReceiverRenderer implements BlockEntityRenderer<PoleReceiverBlockEntity> {
 static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/pole_satellite_receiver.png");private final ModelSatelliteReceiver model=new ModelSatelliteReceiver();
 public PoleReceiverRenderer(BlockEntityRendererProvider.Context context){}
 @Override public void render(PoleReceiverBlockEntity entity,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){poses.pushPose();poses.translate(.5,1.5,.5);poses.mulPose(Axis.ZP.rotationDegrees(180));int rotation=switch(entity.getBlockState().getValue(ForestFixtureBlock.META)){case 5->90;case 3->180;case 4->270;default->0;};poses.mulPose(Axis.YP.rotationDegrees(rotation));model.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,.0625F);poses.popPose();}
 @Override public AABB getRenderBoundingBox(PoleReceiverBlockEntity entity){return new AABB(entity.getBlockPos()).inflate(.75,.25,.75);}
 @Override public int getViewDistance(){return 256;}@Override public boolean shouldRenderOffScreen(PoleReceiverBlockEntity entity){return true;}
}
