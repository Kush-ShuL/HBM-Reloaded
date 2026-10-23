package com.hbm.client.pneumatic;
import com.hbm.pneumatic.tube.*;
import java.util.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class PneumaticClient {
 @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event){event.enqueueWork(()->PneumaticTubeItem.LEFT_SHIFT=()->org.lwjgl.glfw.GLFW.glfwGetKey(net.minecraft.client.Minecraft.getInstance().getWindow().getWindow(),org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT)==org.lwjgl.glfw.GLFW.GLFW_PRESS);}
 @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(PneumaticTubeContent.MENU.get(),PneumoTubeScreen::new);}
 @SubscribeEvent public static void entities(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(PneumaticTubeContent.ANCHOR_ENTITY.get(),PneumaticAnchorRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private PneumaticAnchorItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?renderer=new PneumaticAnchorItemRenderer():renderer;}},PneumaticTubeContent.ANCHOR_ITEM.get());}
 @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event){var cache=new HashMap<String,PneumoTubeModel>();event.getModels().replaceAll((location,model)->{if(!location.id().getNamespace().equals("hbm"))return model;var id=location.id().getPath();if(!id.equals("pneumatic_tube")&&!id.equals("pneumatic_tube_paintable"))return model;return cache.computeIfAbsent(id+":"+location.getVariant().equals("inventory"),s->new PneumoTubeModel(model,id.equals("pneumatic_tube_paintable"),event.getTextureGetter()));});}
 private PneumaticClient(){}
}
