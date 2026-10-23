package com.hbm.client.bobble;
import com.hbm.block.bobble.BobbleContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BobbleClient {
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:bobble_lit"),DefaultVertexFormat.NEW_ENTITY),shader->BobbleRenderTypes.litShader=shader);event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.parse("hbm:bobble_unlit"),DefaultVertexFormat.NEW_ENTITY),shader->BobbleRenderTypes.unlitShader=shader);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(BobbleContent.ENTITY.get(),BobbleRenderer::new);BobbleContent.openScreen=tile->Minecraft.getInstance().setScreen(new BobbleScreen(tile));}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private BobbleItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new BobbleItemRenderer();return renderer;}},BobbleContent.ITEM.get());}
}
