package com.hbm.client.electricpress;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import com.hbm.machine.electricpress.ElectricPressContent;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ElectricPressClient {
    static net.minecraft.network.chat.Component stamp(net.minecraft.world.item.ItemStack stack){return net.minecraft.network.chat.Component.translatable("gui.hbm.electricpress.stamp",stack.isEmpty()?net.minecraft.network.chat.Component.translatable("gui.hbm.electricpress.none").withStyle(net.minecraft.ChatFormatting.RED):stack.getHoverName());}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ElectricPressContent.MENU.get(),ElectricPressScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ElectricPressContent.ELECTRIC_ENTITY.get(),ElectricPressRenderer::new);event.registerBlockEntityRenderer(ElectricPressContent.CONVEYOR_ENTITY.get(),ConveyorPressRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){for(boolean conveyor:new boolean[]{false,true})event.registerItem(new IClientItemExtensions(){private ElectricPressItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ElectricPressItemRenderer(conveyor);return renderer;}},conveyor?ElectricPressContent.CONVEYOR_ITEM.get():ElectricPressContent.ELECTRIC_ITEM.get());}
}
