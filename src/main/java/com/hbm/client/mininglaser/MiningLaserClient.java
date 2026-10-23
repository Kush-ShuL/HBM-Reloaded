package com.hbm.client.mininglaser;
import com.hbm.machine.mininglaser.MiningLaserContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MiningLaserClient {
    @SubscribeEvent public static void renderer(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(MiningLaserContent.ENTITY.get(),MiningLaserRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(MiningLaserContent.MENU.get(),MiningLaserScreen::new);}
    @SubscribeEvent public static void extension(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private MiningLaserItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new MiningLaserItemRenderer()):renderer;}},MiningLaserContent.ITEM.get());}
    private MiningLaserClient(){}
}
