package com.hbm.client.thresher;
import com.hbm.client.woodprocessing.AutosawSound;
import com.hbm.machine.thresher.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ThresherClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ThresherEntity.CLIENT_TICK=machine->AutosawSound.tick(machine,machine.isOn&&!machine.isSuspended,machine.muffled);ThresherEntity.CLIENT_REMOVED=AutosawSound::removed;});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ThresherContent.ENTITY.get(),ThresherRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ThresherItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ThresherItemRenderer()):renderer;}},ThresherContent.ITEM.get());}
    private ThresherClient(){}
}
