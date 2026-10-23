package com.hbm.client.fensu;

import com.hbm.machine.fensu.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class FensuClient {
    static ShaderInstance unlit,plasma;static long renderClock;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:fensu_unlit"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->unlit=shader);event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:fensu_plasma"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->plasma=shader);}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{FensuBlockEntity.CLIENT_TICK=FensuSound::tick;FensuBlockEntity.CLIENT_REMOVED=FensuSound::removed;});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(FensuContent.ENTITY.get(),FensuRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(FensuContent.MENU.get(),FensuScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private FensuItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new FensuItemRenderer()):renderer;}},FensuContent.ITEM.get());}
    public static void receive(FensuPackets.State snapshot){var client=Minecraft.getInstance();if(client.level!=null&&client.level.getBlockEntity(snapshot.pos()) instanceof FensuBlockEntity machine)machine.applyState(snapshot);}
    private FensuClient(){}
}
