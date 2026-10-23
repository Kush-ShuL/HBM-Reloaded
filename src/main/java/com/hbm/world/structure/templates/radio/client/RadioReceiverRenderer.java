package com.hbm.world.structure.templates.radio.client;
import com.hbm.client.model.*;
import com.hbm.client.model.legacy.generated.ModelBroadcaster;
import com.hbm.world.structure.templates.radio.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
/** RenderDecoBlock's actual radiorec branch uses ModelBroadcaster, not ModelRadio. */
public final class RadioReceiverRenderer implements BlockEntityRenderer<RadioReceiverBlockEntity> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/models/modelradioreceiver.png");
    private final ModelBroadcaster model=new ModelBroadcaster();private final WavefrontMesh mesh=RadioReceiverMesh.capture(model);
    public RadioReceiverRenderer(BlockEntityRendererProvider.Context context){}
    private static void transform(PoseStack pose,int meta){pose.translate(.5F,1.5F,.5F);pose.mulPose(Axis.ZP.rotationDegrees(180F));float yaw=switch(meta){case 4->90F;case 2->180F;case 5->270F;default->0F;};if(yaw!=0)pose.mulPose(Axis.YP.rotationDegrees(yaw));}
    @Override public void render(RadioReceiverBlockEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){int meta=tile.getBlockState().getValue(RadioReceiverBlock.META);PoseStack local=new PoseStack();transform(local,meta);LegacyContactShadows.render(tile,mesh,new Matrix4f(local.last().pose()),pose,buffers);pose.pushPose();transform(pose,meta);model.renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),light,overlay,-1,.0625F);pose.popPose();}
    @Override public AABB getRenderBoundingBox(RadioReceiverBlockEntity tile){return new AABB(tile.getBlockPos()).expandTowards(0,.5D,0);}
    @Override public int getViewDistance(){return 64;}
}
