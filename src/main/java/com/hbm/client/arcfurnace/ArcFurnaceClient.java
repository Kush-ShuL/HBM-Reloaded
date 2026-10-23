package com.hbm.client.arcfurnace;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.arcfurnace.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ArcFurnaceClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ArcFurnaceContent.MENU.get(),ArcFurnaceScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ArcFurnaceContent.ENTITY.get(),ArcFurnaceRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ArcFurnaceItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new ArcFurnaceItemRenderer()):renderer;}},ArcFurnaceContent.ITEM.get());}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{ArcFurnaceBlockEntity.CLIENT_TICK=ArcFurnaceAnimation::tick;ArcFurnaceBlockEntity.CLIENT_REMOVED=ArcFurnaceAnimation::removed;for(var item:java.util.List.of(ArcFurnaceContent.ELECTRODE.get(),ArcFurnaceContent.BURNT.get(),ArcFurnaceContent.SAND_ITEM.get()))ItemProperties.register(item,ResourceLocation.parse("hbm:legacy_metadata"),(stack,level,entity,seed)->Math.abs(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%(stack.getItem() instanceof ArcElectrodeItem?4:5)));});}
 private ArcFurnaceClient(){}
}
