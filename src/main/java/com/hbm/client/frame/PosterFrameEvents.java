package com.hbm.client.frame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderItemInFrameEvent;

/** X5846's two unlit posters, replacing only their ordinary framed item draw. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class PosterFrameEvents {
    private static final ResourceLocation FLAME_PONY = ResourceLocation.parse("hbm:flame_pony");
    private static final ResourceLocation POSTER = ResourceLocation.parse("hbm:textures/models/misc/poster.png");
    private static final ResourceLocation CAT = ResourceLocation.parse("hbm:textures/models/misc/poster_cat.png");
    private static final float P = 1F / 16F;

    @SubscribeEvent
    public static void render(RenderItemInFrameEvent event) {
        var stack = event.getItemStack();
        ResourceLocation texture;
        if (stack.is(Items.PAPER)) texture = CAT;
        else if (BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(FLAME_PONY)) texture = POSTER;
        else return;

        // Both old and native callbacks receive the frame renderer's already-rotated matrix.
        var pose = event.getPoseStack().last();
        var vertices = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucentEmissive(texture));
        float offset = P * 2.75F, z = P * .5F;
        vertex(vertices, pose, .5F, .5F + offset, z, 1, 0);
        vertex(vertices, pose, -.5F, .5F + offset, z, 0, 0);
        vertex(vertices, pose, -.5F, -.5F + offset, z, 0, 1);
        vertex(vertices, pose, .5F, -.5F + offset, z, 1, 1);
        event.setCanceled(true);
    }

    private static void vertex(VertexConsumer vertices, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        vertices.addVertex(pose, x, y, z).setColor(1F, 1F, 1F, 1F).setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 0, 1);
    }

    private PosterFrameEvents() {}
}
