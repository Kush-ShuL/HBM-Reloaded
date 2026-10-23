package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.ProgrammableNuclearContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ProgrammableNuclearClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ProgrammableNuclearContent.TIMED_ENTITY.get(),TimedBalefireRenderer::new);event.registerBlockEntityRenderer(ProgrammableNuclearContent.CUSTOM_ENTITY.get(),CustomNuclearRenderer::new);event.registerEntityRenderer(ProgrammableNuclearContent.FALLING.get(),FallingCustomNuclearRenderer::new);event.registerEntityRenderer(ProgrammableNuclearContent.RAINBOW.get(),net.minecraft.client.renderer.entity.NoopRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ProgrammableNuclearContent.TIMED_MENU.get(),TimedBalefireScreen::new);event.register(ProgrammableNuclearContent.CUSTOM_MENU.get(),CustomNuclearScreen::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private TimedBalefireItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new TimedBalefireItemRenderer();return renderer;}},ProgrammableNuclearContent.TIMED_ITEM.get());event.registerItem(new IClientItemExtensions(){private CustomNuclearItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new CustomNuclearItemRenderer();return renderer;}},ProgrammableNuclearContent.CUSTOM_ITEM.get());}
 private ProgrammableNuclearClient(){}
}
