package com.hbm.client.coker;
import com.hbm.machine.coker.*;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CokerClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->CokerEntity.clientTick=m->{var level=m.getLevel();if(level!=null&&m.wasOn&&level.getGameTime()%2==0){var p=m.getBlockPos();LegacyFluidEffects.tower(level,p.getX()+.5,p.getY()+22,p.getZ()+.5,new LegacyFluidEffects.Tower(10,.75F,3,200+level.random.nextInt(50)).color(0x404040));}});}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(CokerContent.MENU.get(),CokerScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(CokerContent.ENTITY.get(),CokerRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private CokerItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new CokerItemRenderer();return renderer;}},CokerContent.ITEM.get());}
}
