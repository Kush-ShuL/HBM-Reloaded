package com.hbm.client.custommachine;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.custommachine.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class CustomMachineClient {
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(CustomMachineContent.MENU.get(),CustomMachineScreen::new);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(CustomMachineContent.ENTITY.get(),CustomMachineRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{CustomMachineContent.LANGUAGE=()->Minecraft.getInstance().getLanguageManager().getSelected();CustomMachineNetwork.CLIENT=json->CustomMachinesConfig.replace(JsonParser.parseString(json).getAsJsonObject());for(var kind:CMComponentBlock.Kind.values())if(kind.variants.length>1)ItemProperties.register(CustomMachineContent.COMPONENT_ITEMS.get(kind).get(),ResourceLocation.parse("hbm:legacy_metadata"),(stack,world,entity,seed)->stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)%kind.variants.length);ItemBlockRenderTypes.setRenderLayer(CustomMachineContent.COMPONENTS.get(CMComponentBlock.Kind.TANK).get(),RenderType.cutout());});}
}
