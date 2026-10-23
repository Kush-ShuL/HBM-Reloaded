package com.hbm.client.turbofan;
import com.hbm.machine.turbofan.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class TurbofanClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(TurbofanContent.ENTITY.get(),TurbofanRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(TurbofanContent.MENU.get(),TurbofanScreen::new);}@SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){e.registerItem(new IClientItemExtensions(){private TurbofanItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new TurbofanItemRenderer()):renderer;}},TurbofanContent.ITEM.get());}@SubscribeEvent public static void setup(FMLClientSetupEvent e){e.enqueueWork(()->{TurbofanEntity.clientTick=TurbofanClient::tick;TurbofanEntity.clientRemoved=TurbofanSound::removed;});}
 private static void tick(TurbofanEntity t){TurbofanSound.tick(t);var player=Minecraft.getInstance().player;if(!t.wasOn||player==null||player.getAbilities().instabuild)return;var d=TurbofanEntity.airflow(t.getBlockState());if(TurbofanEntity.volume(t.getBlockPos(),d,0).intersects(player.getBoundingBox()))TurbofanEntity.push(player,d);if(TurbofanEntity.volume(t.getBlockPos(),d,1).intersects(player.getBoundingBox()))TurbofanEntity.push(player,d);if(TurbofanEntity.volume(t.getBlockPos(),d,2).intersects(player.getBoundingBox()))TurbofanEntity.stick(player);}
}
