package com.hbm.client.cargoelevator;

import com.hbm.machine.cargoelevator.CargoElevatorContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CargoElevatorClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CargoElevatorContent.ENTITY.get(),CargoElevatorRenderer::new);}
    @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private CargoElevatorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new CargoElevatorItemRenderer()):renderer;}},CargoElevatorContent.ITEM.get());}
    private CargoElevatorClient(){}
}
