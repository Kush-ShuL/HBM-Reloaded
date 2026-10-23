package com.hbm.client.network.cargo.request;
import com.hbm.client.network.cargo.*;
import com.hbm.client.network.rtty.RadioTorchBakedModel;
import com.hbm.network.cargo.request.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CargoRequestClient {
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->CargoDroneEffects.REQUEST_TOOLS=item->item==CargoRequestContent.WAYPOINT_ITEM.get()||CargoRequestContent.ITEMS_BY_KIND.values().stream().anyMatch(holder->holder.get()==item));}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(CargoRequestContent.REQUEST_ENTITY.get(),CargoDroneRenderer::new);}@SubscribeEvent public static void screens(RegisterMenuScreensEvent event){CargoRequestContent.MENUS_BY_KIND.values().forEach(menu->event.register(menu.get(),CargoRequestScreen::new));}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().equals(ResourceLocation.parse("hbm:drone_waypoint_request"))||!id.getVariant().startsWith("legacy_meta="))return model;var sprite=event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/drone_waypoint_request")));return new RadioTorchBakedModel(model,"drone_waypoint_request",Integer.parseInt(id.getVariant().substring(12)),ignored->sprite,entity->false);});}
}
