package com.hbm.client.charger;

import com.hbm.machine.charger.ChargerContent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ChargerClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ChargerContent.ENTITY.get(),ChargerRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ChargerItemRenderer renderer;public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ChargerItemRenderer();return renderer;}},ChargerContent.ITEM.get());}
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:charger_unlit"),DefaultVertexFormat.POSITION_COLOR),shader->ChargerRenderTypes.shader=shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:charger_slide"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->ChargerRenderTypes.slideShader=shader);
    }
}
