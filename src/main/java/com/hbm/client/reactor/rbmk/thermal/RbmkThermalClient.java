package com.hbm.client.reactor.rbmk.thermal;
import com.hbm.client.reactor.rbmk.column.RbmkPassiveModel;
import com.hbm.machine.reactor.rbmk.thermal.RbmkThermalContent;
import java.util.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkThermalClient{
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RbmkThermalContent.MENU.get(),RbmkHeaterScreen::new);}
    @SubscribeEvent public static void geometry(ModelEvent.RegisterGeometryLoaders event){event.register(ResourceLocation.parse("hbm:rbmk_heater"),RbmkHeaterGeometry.LOADER);}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){Map<BakedModel,RbmkPassiveModel> cache=new IdentityHashMap<>();event.getModels().replaceAll((key,model)->key.id().getNamespace().equals("hbm")&&key.id().getPath().equals("rbmk_cooler")&&!key.getVariant().equals("inventory")?cache.computeIfAbsent(model,original->new RbmkPassiveModel(original,event.getTextureGetter(),"rbmk_cooler")):model);}
    private RbmkThermalClient(){}
}
