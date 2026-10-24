package com.hbm.item.battery.client;

import com.hbm.HbmReloaded;
import com.hbm.item.battery.BatteryContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/** Client-only renderer hooks; common battery registration has no Minecraft client references. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class BatteryItemModels {
    private BatteryItemModels() {}
    @SubscribeEvent public static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new BatteryPackRenderer();
                return renderer;
            }
        }, BatteryContent.packItems().toArray(net.minecraft.world.item.Item[]::new));
    }
}
