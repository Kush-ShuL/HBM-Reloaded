package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.PrimedExplosive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** RenderTNTPrimedBase: source Y rotation, fourth-power swelling and independent additive white pulse. */
public final class PrimedExplosiveRenderer extends EntityRenderer<PrimedExplosive> {
    private final BlockRenderDispatcher blocks;
    public PrimedExplosiveRenderer(EntityRendererProvider.Context context) { super(context); blocks = context.getBlockRenderDispatcher(); shadowRadius = .5F; }
    @Override public ResourceLocation getTextureLocation(PrimedExplosive entity) { return TextureAtlas.LOCATION_BLOCKS; }
    @Override public void render(PrimedExplosive entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        pose.pushPose(); pose.translate(0, entity.getBbHeight() / 2F, 0); pose.mulPose(Axis.YP.rotationDegrees(-90));
        float time = entity.fuse() - partial + 1;
        if (time < 10) { float swelling = Mth.clamp(1 - time / 10F, 0, 1); swelling *= swelling; swelling *= swelling; pose.scale(1 + swelling * .3F, 1 + swelling * .3F, 1 + swelling * .3F); }
        pose.pushPose(); pose.translate(-.5, -.5, -.5);
        net.minecraft.client.resources.model.BakedModel barrel=null;
        if(entity.bombBlock() instanceof com.hbm.bomb.conventional.HazardousBarrelBlock || entity.bombBlock() instanceof com.hbm.bomb.conventional.TimedChargeBlock){
            barrel=net.minecraft.client.Minecraft.getInstance().getItemRenderer().getModel(new net.minecraft.world.item.ItemStack(entity.bombBlock()),entity.level(),null,0);
            blocks.getModelRenderer().renderModel(pose.last(),buffers.getBuffer(net.minecraft.client.renderer.RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)),entity.bombBlock().defaultBlockState(),barrel,1,1,1,light,OverlayTexture.NO_OVERLAY);
        }else blocks.renderSingleBlock(entity.bombBlock().defaultBlockState(), pose, buffers, light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
        if (entity.fuse() / 5 % 2 == 0) {
            pose.scale(1.01F, 1.01F, 1.01F);
            int alpha = Mth.clamp((int) ((1 - time / 100F) * .8F * 255), 0, 255), color = alpha << 24 | 0xFFFFFF;
            var out = buffers.getBuffer(ExplosiveRenderTypes.FLASH);
            // RenderTNTPrimedBase re-renders the real inventory mesh, including rotated OBJ charges.
            var pulse = barrel != null ? barrel : net.minecraft.client.Minecraft.getInstance().getItemRenderer().getModel(
                    new net.minecraft.world.item.ItemStack(entity.bombBlock()), entity.level(), null, 0);
            pose.pushPose();
            try {
                pose.translate(-.5, -.5, -.5);
                var random = net.minecraft.util.RandomSource.create(0);
                int stride = com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;
                for (int side = 0; side <= 6; side++) {
                    random.setSeed(0);
                    var direction = side == 6 ? null : net.minecraft.core.Direction.from3DDataValue(side);
                    for (var quad : pulse.getQuads(null, direction, random)) {
                        int[] data = quad.getVertices();
                        for (int i = 0; i < 4; i++) {
                            int offset = i * stride;
                            out.addVertex(pose.last(), Float.intBitsToFloat(data[offset]), Float.intBitsToFloat(data[offset + 1]), Float.intBitsToFloat(data[offset + 2])).setColor(color);
                        }
                    }
                }
            } finally { pose.popPose(); }
        }
        pose.popPose(); super.render(entity, yaw, partial, pose, buffers, light);
    }
}
