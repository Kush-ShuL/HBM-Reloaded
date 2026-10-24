package com.hbm.client.reactor.pwr;

import com.hbm.machine.reactor.pwr.*;
import com.hbm.machine.refinery.deco.DecoSteelModel;
import java.util.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PwrClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{PwrControllerBlockEntity.CLIENT_TICK=PwrSound::tick;PwrControllerBlockEntity.CLIENT_REMOVED=PwrSound::removed;});}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PwrContent.MENU.get(),PwrScreen::new);}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){var cache=new HashMap<String,BakedModel>();event.getModels().replaceAll((key,model)->{if(!key.id().equals(PwrContent.ASSEMBLY.getId())||key.getVariant().equals("inventory"))return model;boolean port=key.getVariant().contains("port=true");String name=port?"pwr_casing_port":"pwr_block";return cache.computeIfAbsent(name,ignored->DecoSteelModel.wrap(model,event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,PwrContent.id("block/"+name))),event.getTextureGetter().apply(new Material(TextureAtlas.LOCATION_BLOCKS,PwrContent.id("block/"+name+"_ct"))),(state,other)->other.is(PwrContent.ASSEMBLY.get())||other.is(PwrContent.CONTROLLER.get())));});}
    private PwrClient(){}
}
