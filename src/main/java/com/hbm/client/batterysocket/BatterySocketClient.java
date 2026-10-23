package com.hbm.client.batterysocket;

import com.hbm.machine.batterysocket.BatterySocketContent;
import com.hbm.machine.batterysocket.SocketDischargeBeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BatterySocketClient {
    private BatterySocketClient(){}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BatterySocketContent.ENTITY.get(),BatterySocketRenderer::new);event.registerEntityRenderer(BatterySocketContent.DISCHARGE.get(),InvisibleBeam::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(BatterySocketContent.MENU.get(),BatterySocketScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){BatterySocketContent.detailedTooltip=()->com.mojang.blaze3d.platform.InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT);event.registerItem(new IClientItemExtensions(){private BatterySocketItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new BatterySocketItemRenderer();return renderer;}},BatterySocketContent.ITEM.get());}
    /** Source RenderBeam only draws config.rendererBeam, which is null for the socket discharge config. */
    private static final class InvisibleBeam extends EntityRenderer<SocketDischargeBeam> {
        InvisibleBeam(EntityRendererProvider.Context context){super(context);}
        @Override public ResourceLocation getTextureLocation(SocketDischargeBeam entity){return ResourceLocation.withDefaultNamespace("textures/misc/white.png");}
    }
}
