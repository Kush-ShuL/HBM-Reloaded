package com.hbm.client.reactor.rbmk.console.item;
import com.hbm.machine.reactor.rbmk.console.RbmkConsoleContent;import com.hbm.machine.reactor.rbmk.crane.RbmkCraneContent;import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkConsoleItemClient {
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(extension(false),RbmkConsoleContent.ITEM.get());event.registerItem(extension(true),RbmkCraneContent.ITEM.get());}
 private static IClientItemExtensions extension(boolean crane){return new IClientItemExtensions(){private RbmkConsoleItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new RbmkConsoleItemRenderer(crane);return renderer;}};}
 private RbmkConsoleItemClient(){}
}
