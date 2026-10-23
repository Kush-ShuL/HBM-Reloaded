package com.hbm.client.generator.leviathan;
import com.hbm.machine.generator.leviathan.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LeviathanClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{LeviathanEntity.CLIENT_TICK=LeviathanSound::tick;LeviathanEntity.CLIENT_REMOVED=LeviathanSound::removed;});}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(LeviathanContent.ENTITY.get(),LeviathanRenderer::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private LeviathanItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new LeviathanItemRenderer()):renderer;}},LeviathanContent.ITEM.get());}
 private LeviathanClient(){}
}
