package com.hbm.machine.reactor.zirnox.client;
import com.hbm.machine.reactor.zirnox.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class ZirnoxClient {
    static ShaderInstance flameShader;
    @SubscribeEvent public static void shaders(RegisterShadersEvent event)throws java.io.IOException{event.registerShader(new ShaderInstance(event.getResourceProvider(),ZirnoxRenderer.id("zirnox_flame"),DefaultVertexFormat.POSITION_TEX_COLOR),shader->flameShader=shader);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(ZirnoxContent.MENU.get(),ZirnoxScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(ZirnoxContent.REACTOR_ENTITY.get(),context->new ZirnoxRenderer<>(context,false));event.registerBlockEntityRenderer(ZirnoxContent.DESTROYED_ENTITY.get(),context->new ZirnoxRenderer<>(context,true));event.registerEntityRenderer(ZirnoxContent.DEBRIS.get(),ZirnoxDebrisRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ZirnoxItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new ZirnoxItemRenderer();return renderer;}},ZirnoxContent.ITEM.get());}
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->ItemProperties.register(ZirnoxContent.ROD.get(),ZirnoxRenderer.id("legacy_metadata"),(stack,level,entity,seed)->ZirnoxContent.ROD.get().ordinal(stack)));}
    public static void flame(ZirnoxFlameEffect value){flame(value.x(),value.y(),value.z(),value.maxAge());}
    public static void flame(double x,double y,double z,int maxAge){var minecraft=Minecraft.getInstance();if(minecraft.level!=null)minecraft.particleEngine.add(new ZirnoxFlameParticle(minecraft.level,x,y,z,maxAge));}
}
