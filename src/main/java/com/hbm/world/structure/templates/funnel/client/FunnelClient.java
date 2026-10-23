package com.hbm.world.structure.templates.funnel.client;
import com.hbm.world.structure.templates.funnel.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import org.lwjgl.glfw.GLFW;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FunnelClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(FunnelContent.MENU.get(),FunnelScreen::new);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->FunnelItem.leftShift=()->InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT));}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((key,model)->key.id().getNamespace().equals("hbm")&&key.id().getPath().equals("machine_funnel")?new FunnelModel(model,key.getVariant().equals("inventory"),event.getTextureGetter()):model);}
    private FunnelClient(){}
}
