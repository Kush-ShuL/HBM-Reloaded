package com.hbm.client.structureloot;
import com.hbm.world.structure.loot.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class StructureLootClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{LoreBookItem.OPEN=stack->Minecraft.getInstance().setScreen(new LoreBookScreen(stack));LoreBookItem.HAS_TRANSLATION=I18n::exists;});}
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event){event.register((stack,tint)->0xff000000|LoreBookItem.tint(stack,tint),StructureLootContent.BOOK.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(net.minecraft.resources.ResourceLocation.parse("hbm:crate_supply"))?new com.hbm.client.food.ConserveCrateModel(model,id.getVariant().equals("inventory"),event.getTextureGetter()):model);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(StructureLootContent.PILE_ENTITY.get(),LootPileRenderer::new);}
}
