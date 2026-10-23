package com.hbm.client.reactor.iter.mhdt;
import com.hbm.machine.reactor.iter.mhdt.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class MhdtClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(MhdtContent.ENTITY.get(),MhdtRenderer::new);MhdtEntity.CLIENT_TICK=MhdtSound::tick;MhdtEntity.CLIENT_REMOVED=MhdtSound::removed;}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private MhdtItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new MhdtItemRenderer()):renderer;}},MhdtContent.ITEM.get());}
 private MhdtClient(){}
}
