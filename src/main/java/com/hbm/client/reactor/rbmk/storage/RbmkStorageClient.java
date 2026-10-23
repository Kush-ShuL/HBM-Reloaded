package com.hbm.client.reactor.rbmk.storage;
import com.hbm.client.reactor.rbmk.column.RbmkPassiveModel;
import com.hbm.machine.reactor.rbmk.storage.RbmkStorageContent;
import java.util.*;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RbmkStorageClient {
    @SubscribeEvent public static void screen(RegisterMenuScreensEvent event){event.register(RbmkStorageContent.MENU.get(),RbmkStorageScreen::new);}
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){Map<BakedModel,RbmkPassiveModel> cache=new IdentityHashMap<>();event.getModels().replaceAll((key,model)->key.id().getNamespace().equals("hbm")&&key.id().getPath().equals("rbmk_storage")&&!key.getVariant().equals("inventory")?cache.computeIfAbsent(model,original->new RbmkPassiveModel(original,event.getTextureGetter(),"rbmk_storage")):model);}
    private RbmkStorageClient(){}
}
