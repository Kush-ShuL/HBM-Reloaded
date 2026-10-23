package com.hbm.client.assembler;
import com.hbm.machine.assembler.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AssemblerClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{AssemblerBlockEntity.CLIENT_TICK=AssemblerAnimation::tick;AssemblerBlockEntity.CLIENT_REMOVED=AssemblerAnimation::removed;AssemblerBlockEntity.CLIENT_STOP=AssemblerAnimation::stopped;});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(AssemblerContent.ENTITY.get(),AssemblerRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private AssemblerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new AssemblerItemRenderer();return renderer;}},AssemblerContent.ITEM.get());}
}
