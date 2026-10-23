package com.hbm.client.fluidtank;
import com.hbm.machine.fluidtank.IndustrialFluidTankContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IndustrialFluidTankClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(IndustrialFluidTankContent.MENU.get(),IndustrialFluidTankScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(IndustrialFluidTankContent.ENTITY.get(),IndustrialFluidTankRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private IndustrialFluidTankItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new IndustrialFluidTankItemRenderer();return renderer;}},IndustrialFluidTankContent.ITEM.get());}
}
