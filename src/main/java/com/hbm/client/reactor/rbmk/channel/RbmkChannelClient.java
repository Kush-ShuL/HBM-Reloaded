package com.hbm.client.reactor.rbmk.channel;
import com.hbm.client.reactor.rbmk.column.RbmkPassiveModel;
import com.hbm.machine.reactor.rbmk.channel.*;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkChannelClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RbmkFuelChannels.ENTITY.get(),RbmkChannelRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RbmkFuelChannels.MENU.get(),RbmkChannelScreen::new);}
    @SubscribeEvent public static void geometry(ModelEvent.RegisterGeometryLoaders event){event.register(ResourceLocation.parse("hbm:rbmk_channel"),RbmkChannelGeometry.LOADER);}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){Map<String,RbmkPassiveModel> cache=new HashMap<>();event.getModels().replaceAll((key,model)->{if(!key.id().getNamespace().equals("hbm")||key.getVariant().equals("inventory"))return model;for(var kind:RbmkChannelKind.values())if(key.id().getPath().equals(kind.id))return cache.computeIfAbsent(kind.id,ignored->new RbmkPassiveModel(model,event.getTextureGetter(),kind.texture));return model;});}
    private RbmkChannelClient(){}
}
