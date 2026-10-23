package com.hbm.client.groundwater;

import com.hbm.machine.groundwater.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class GroundwaterPumpClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(GroundwaterPumpContent.STEAM_ENTITY.get(),GroundwaterPumpRenderer<SteamGroundwaterPump>::new);
        event.registerBlockEntityRenderer(GroundwaterPumpContent.ELECTRIC_ENTITY.get(),GroundwaterPumpRenderer<ElectricGroundwaterPump>::new);
    }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        var extension=new IClientItemExtensions(){private GroundwaterPumpItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new GroundwaterPumpItemRenderer();return renderer;}};
        event.registerItem(extension,GroundwaterPumpContent.ITEM.get(GroundwaterPumpKind.STEAM).get(),GroundwaterPumpContent.ITEM.get(GroundwaterPumpKind.ELECTRIC).get());
    }
}
