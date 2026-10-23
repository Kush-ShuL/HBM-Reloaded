package com.hbm.client.plasmaforge;

import com.hbm.machine.plasmaforge.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PlasmaForgeClient {
    static ShaderInstance unlit,additive,glow;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{for(String name:new String[]{"unlit","additive","glow"})event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:plasma_forge_"+name),DefaultVertexFormat.POSITION_TEX_COLOR),shader->{switch(name){case "unlit"->unlit=shader;case "additive"->additive=shader;case "glow"->glow=shader;}});}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->{PlasmaForgeBlockEntity.CLIENT_TICK=PlasmaForgeAnimation::tick;PlasmaForgeBlockEntity.CLIENT_REMOVED=PlasmaForgeAnimation::removed;});}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PlasmaForgeContent.ENTITY.get(),PlasmaForgeRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PlasmaForgeContent.MENU.get(),PlasmaForgeScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PlasmaForgeItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new PlasmaForgeItemRenderer()):renderer;}},PlasmaForgeContent.ITEM.get());}
    private PlasmaForgeClient(){}
}
