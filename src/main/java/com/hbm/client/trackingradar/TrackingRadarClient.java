package com.hbm.client.trackingradar;

import com.hbm.client.model.LegacyItemTransforms;
import com.hbm.machine.trackingradar.TrackingRadarContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class TrackingRadarClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(TrackingRadarContent.ENTITY.get(), TrackingRadarRenderer::new); }
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) {
        event.register(TrackingRadarContent.MENU.get(), TrackingRadarScreen::new);
    }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new ItemRenderer(); return renderer; }
        }, TrackingRadarContent.ITEM.get());
    }
    private static final class ItemRenderer extends BlockEntityWithoutLevelRenderer {
        ItemRenderer() { super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()); }
        @Override public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
            pose.pushPose(); LegacyItemTransforms.held(pose, context);
            if (context == ItemDisplayContext.GUI) {
                pose.translate(0, 1, .5); pose.scale(1F/16, -1F/16, 1F/16); pose.translate(8, 10, 0);
                pose.mulPose(Axis.XP.rotationDegrees(-30)); pose.mulPose(Axis.YP.rotationDegrees(45)); pose.scale(-1, -1, -1);
                pose.translate(0, -5, 0); pose.scale(2.25F, 2.25F, 2.25F);
            } else {
                if (context == ItemDisplayContext.GROUND) { pose.translate(.5, 0, .5); pose.scale(1.5F, 1.5F, 1.5F); }
                else pose.translate(.5, .25, 0);
                pose.scale(.25F, .25F, .25F);
                if (context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND && context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) pose.mulPose(Axis.YP.rotationDegrees(90));
            }
            pose.scale(.5F, .5F, .5F); pose.mulPose(Axis.YP.rotationDegrees(-90));
            TrackingRadarRenderer.common(15,-45,pose,buffers,light,overlay); pose.popPose();
        }
    }
    private TrackingRadarClient() { }
}
