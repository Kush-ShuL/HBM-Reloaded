package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FoundryClient {
 @SubscribeEvent public static void sprites(RegisterSpriteSourceTypesEvent event){FoundrySprites.TYPE=event.register(ResourceLocation.fromNamespaceAndPath("hbm","foundry"),FoundrySprites.CODEC);}
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FoundryContent.ENTITY.get(),FoundryRenderer::new);}
 @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->FoundryStreamPacket.CLIENT_HANDLER=packet->{var level=Minecraft.getInstance().level;if(level!=null)Minecraft.getInstance().particleEngine.add(new FoundryStreamParticle(level,packet));});}
 @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->{if(!id.id().getNamespace().equals("hbm")||!id.getVariant().equals("inventory"))return model;for(var kind:FoundryContent.Kind.values())if(kind!=FoundryContent.Kind.SLAG&&id.id().getPath().equals(kind.id()))return new FoundryInventoryModel(model,kind,event.getTextureGetter());return model;});}
}
