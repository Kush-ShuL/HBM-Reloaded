package com.hbm.client.cable;
import com.hbm.compat.computercraft.cable.*;
import java.util.HashMap;
import net.minecraft.client.resources.model.BakedModel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class PaintableComputerCableClient {
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){if(!PaintableComputerCableContent.ENABLED)return;var models=new HashMap<BakedModel,PaintableComputerCableModel>();event.getModels().replaceAll((location,model)->location.id().getNamespace().equals("hbm")&&location.id().getPath().equals("oc_cable_paintable")?models.computeIfAbsent(model,key->new PaintableComputerCableModel(model,event.getTextureGetter())):model);}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Block event){if(PaintableComputerCableContent.ENABLED)event.register((state,level,pos,tint)->tint==1&&level!=null&&pos!=null&&level.getBlockEntity(pos) instanceof PaintableComputerCableEntity cable?cable.color():0xffffff,PaintableComputerCableContent.CABLE.get());}
    private PaintableComputerCableClient(){}
}
