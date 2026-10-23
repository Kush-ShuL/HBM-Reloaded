package com.hbm.block.pipe.client;

import com.hbm.HbmReloaded;
import com.hbm.block.pipe.PipeContent;
import com.hbm.item.fluid.FluidItemContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PipeClientSetup {
    private PipeClientSetup() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(PipeContent.ENTITY.get(), PipeRenderer::new); }
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) { event.register(PipeKeys.CONTROL); event.register(PipeKeys.ALT); }
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event) { event.register((stack, layer) -> layer == 0 ? -1 : 0xFF000000 | FluidItemContent.type(stack).getColor(), PipeContent.TYPED_ITEM.get()); }
    @SubscribeEvent public static void item(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private PipeItemRenderer renderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new PipeItemRenderer(); return renderer; }
        }, PipeContent.ITEM.get());
        var special = PipeContent.SPECIAL_ITEMS.entrySet().stream().filter(entry -> entry.getKey().box() || entry.getKey().anchor()).map(entry -> entry.getValue().get()).toArray(net.minecraft.world.item.Item[]::new);
        event.registerItem(new IClientItemExtensions() {
            private SpecialPipeItemRenderer renderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() { if (renderer == null) renderer = new SpecialPipeItemRenderer(); return renderer; }
        }, special);
    }
}
