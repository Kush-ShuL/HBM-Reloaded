package com.hbm.item.fluid.client;

import com.hbm.HbmReloaded;
import com.hbm.item.fluid.FluidContainerItem;
import com.hbm.item.fluid.FluidIdentifierItem;
import com.hbm.item.fluid.FluidItemContent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = HbmReloaded.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class FluidItemColors {
    private FluidItemColors() {}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event) {
        for (var holder : java.util.List.of(FluidItemContent.CANISTER, FluidItemContent.GAS, FluidItemContent.TANK, FluidItemContent.LEAD_TANK, FluidItemContent.BARREL, FluidItemContent.PACK)) {
            FluidContainerItem item = holder.get();
            event.register((stack, pass) -> 0xFF000000 | item.color(stack, pass), item);
        }
        event.register((stack, pass) -> 0xFF000000 | (pass == 0 || FluidIdentifierItem.getType(stack, true).getColor() < 0 ? 0xFFFFFF : FluidIdentifierItem.getType(stack, true).getColor()), FluidItemContent.IDENTIFIER.get());
        event.register((stack, pass) -> 0xFF000000 | (FluidItemContent.type(stack).getColor() < 0 ? 0xFFFFFF : FluidItemContent.type(stack).getColor()), FluidItemContent.ICON.get());
    }
}
