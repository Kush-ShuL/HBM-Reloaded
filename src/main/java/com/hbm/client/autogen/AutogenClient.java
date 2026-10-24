package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.item.autogen.ScrapsItem;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AutogenClient {
    private AutogenClient() {}
    @SubscribeEvent public static void sprites(RegisterSpriteSourceTypesEvent event) { AutogenSprites.TYPE=event.register(ResourceLocation.fromNamespaceAndPath("hbm","autogen"),AutogenSprites.CODEC); }
    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event) { for(var holder:AutogenContent.all()) event.register((stack,layer)->holder.get().color(stack),holder.get()); }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(()->ScrapsItem.leftShiftHeld=()->InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT)); }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        var fallback=event.getModels().get(ModelResourceLocation.inventory(AutogenContent.all().getFirst().getId()));
        for(var holder:AutogenContent.all()) {
            var id=ModelResourceLocation.inventory(holder.getId());
            var original=event.getModels().getOrDefault(id,fallback);
            if(original==event.getModels().get(net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_VARIANT)) original=fallback;
            if(original!=null) event.getModels().put(id,new AutogenBakedModel(original,holder.get(),event.getTextureGetter()));
        }
    }
}
