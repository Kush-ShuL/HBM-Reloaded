package com.hbm.client.industrialboiler;
import com.hbm.machine.industrialboiler.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class IndustrialBoilerClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{IndustrialBoilerBlockEntity.clientTick=IndustrialBoilerSound::tick;IndustrialBoilerBlockEntity.clientRemoved=IndustrialBoilerSound::removed;});}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(IndustrialBoilerContent.ENTITY.get(),IndustrialBoilerRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private IndustrialBoilerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new IndustrialBoilerItemRenderer()):renderer;}},IndustrialBoilerContent.ITEM.get());}
}
