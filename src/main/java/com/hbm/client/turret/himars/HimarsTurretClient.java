package com.hbm.client.turret.himars;
import com.hbm.machine.turret.himars.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class HimarsTurretClient {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(HimarsTurretContent.ENTITY.get(),HimarsTurretRenderer::new);event.registerEntityRenderer(HimarsTurretContent.ROCKET.get(),HimarsRocketRenderer::new);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(HimarsTurretContent.MENU.get(),HimarsTurretScreen::new);}
 @SubscribeEvent public static void items(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private HimarsTurretItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new HimarsTurretItemRenderer();return renderer;}},HimarsTurretContent.ITEM.get());event.registerItem(new IClientItemExtensions(){private HimarsAmmoItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new HimarsAmmoItemRenderer();return renderer;}},HimarsTurretContent.ammoItems().toArray(net.minecraft.world.item.Item[]::new));}
 private HimarsTurretClient(){}
}
